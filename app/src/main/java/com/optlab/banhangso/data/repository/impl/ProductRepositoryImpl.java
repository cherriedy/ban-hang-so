package com.optlab.banhangso.data.repository.impl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class ProductRepositoryImpl implements ProductRepository {
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<Product>> products = new MutableLiveData<>();

    public ProductRepositoryImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
        retrieveData();
    }

    /**
     * Converts a DocumentSnapshot to a Product object.
     *
     * @param doc The DocumentSnapshot to convert.
     * @return The converted Product object, or null if the conversion fails.
     */
    private Product docToProduct(DocumentSnapshot doc) {
        Product product = doc.toObject(Product.class);
        if (product == null) {
            Timber.e("Document %s is null", doc.getId());
            return null;
        } else {
            Timber.d("Document %s is not null", doc.getId());
            product.setId(doc.getId()); // Set the document ID to
            return product;
        }
    }

    private void retrieveData() {
        firestore.collection("products")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Timber.e("Error getting documents: %s", error.getMessage());
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        List<Product> productList = snapshots.getDocuments().stream()
                                .map(this::docToProduct)
                                .filter(Objects::nonNull) // Filter out null products, if any
                                .collect(Collectors.toList());
                        Timber.d("Products retrieved: %d", productList.size());
                        products.setValue(productList);
                    } else {
                        if (snapshots == null) {
                            Timber.e("Snapshot is null");
                        } else {
                            Timber.d("Snapshot is not null but empty");
                        }
                    }
                });
    }

    @Override
    public LiveData<List<Product>> getProducts() {
        return products;
    }

    @Override
    public Product getProductById(String id) {
        return Optional.ofNullable(products.getValue())
                .flatMap(pl -> pl.stream()
                        .filter(product -> product.getId().equals(id))
                        .findFirst()
                )
                .map(this::safeClone)
                .orElse(null);
    }

    @Override
    public int getPositionById(String id) {
        List<Product> productList = products.getValue();
        if (productList == null) return -1;
        return IntStream.range(0, productList.size())
                .filter(i -> productList.get(i).getId().equals(id))
                .findFirst()
                .orElse(-1);
    }

    @Override
    public void addProduct(Product newProduct, Consumer<Boolean> callback) {
        firestore.collection("products")
                .add(newProduct)
                .addOnSuccessListener(docRef -> {
                    Timber.d("Product added with ID: %s", docRef.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Timber.e("Failed to add product: %s", e.getMessage());
                    callback.accept(false);
                });
    }

    @Override
    public void updateProduct(Product updatedProduct, Consumer<Boolean> callback) {
        if (updatedProduct.getId() == null) {
            Timber.e("Cannot update the product: ID is null!");
            return;
        }

        firestore.collection("products")
                .document(updatedProduct.getId())
                .set(updatedProduct)
                .addOnSuccessListener(unused -> {
                    Timber.d("Product updated: %s", updatedProduct.getId());
                    callback.accept(true);
                })
                .addOnFailureListener(e -> {
                    Timber.e("Failed to update product: %s", e.getMessage());
                    callback.accept(false);
                });
    }

    private Product safeClone(Product product) {
        try {
            return (Product) product.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cannot clone product: " + e);
        }
    }
}