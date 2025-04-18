package com.optlab.banhangso.data.repository.impl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.repository.BrandRepository;

import timber.log.Timber;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Singleton;

@Singleton
public class BrandRepositoryImpl implements BrandRepository {
    public static final String COLLECTION_PATH = "brands";
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<Brand>> brands = new MutableLiveData<>();

    public BrandRepositoryImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
        retrieveData();
    }

    /**
     * Converts a DocumentSnapshot to a Brand object.
     *
     * @param doc The DocumentSnapshot to convert.
     * @return The converted Brand object, or null if the conversion fails.
     */
    private Brand docToBrand(DocumentSnapshot doc) {
        Brand brand = doc.toObject(Brand.class);
        if (brand == null) {
            Timber.e("Document %s is null", doc.getId());
            return null;
        } else {
            Timber.d("Document %s is not null", doc.getId());
            brand.setId(doc.getId());
            return brand;
        }
    }

    /** Retrieves data from Firestore and updates the brands LiveData. */
    private void retrieveData() {
        firestore
                .collection(COLLECTION_PATH)
                .addSnapshotListener(
                        (snapshots, error) -> {
                            if (error != null) {
                                Timber.e("Failed getting documents: %s", error.getMessage());
                                return;
                            }

                            if (snapshots != null && !snapshots.isEmpty()) {
                                List<Brand> brandList =
                                        snapshots.getDocuments().stream()
                                                .map(this::docToBrand)
                                                .filter(Objects::nonNull) // Filter out null brands
                                                // if any
                                                .collect(Collectors.toList());
                                brands.setValue(brandList);
                            }
                        });
    }

    @Override
    public LiveData<List<Brand>> getBrands() {
        return brands;
    }

    @Override
    public Brand getBrandById(String id) {
        return Optional.ofNullable(brands.getValue())
                .flatMap(
                        brandList ->
                                brandList.stream()
                                        .filter(brand -> brand.getId().equals(id))
                                        .findFirst())
                .map(this::safeClone)
                .orElse(null);
    }

    public Brand getBrandByPosition(int position) {
        return Optional.ofNullable(brands.getValue())
                .flatMap(brands -> Optional.of(brands.get(position)))
                .map(this::safeClone)
                .orElse(null);
    }

    @Override
    public int getPositionById(String id) {
        List<Brand> brands = this.brands.getValue();
        if (brands == null) return -1;
        return IntStream.range(0, brands.size())
                .filter(i -> brands.get(i).getId().equals(id))
                .findFirst()
                .orElse(-1);
    }

    @Override
    public void updateBrand(Brand brand, Consumer<Boolean> callback) {
        firestore
                .collection(COLLECTION_PATH)
                .document(brand.getId())
                .set(brand)
                .addOnSuccessListener(unused -> {
                    Timber.d("Document %s updated", brand.getId());
                    callback.accept(true);
                }).addOnFailureListener(e -> {
                    Timber.e("Failed updating document: %s", e.getMessage());
                    callback.accept(false);
                });
    }

    @Override
    public void deleteBrand(Brand brand, Consumer<Boolean> callback) {}

    @Override
    public void createBrand(Brand brand, Consumer<Boolean> callback) {
        firestore
                .collection(COLLECTION_PATH)
                .add(brand)
                .addOnSuccessListener(
                        docRef -> {
                            Timber.d("Document %s created", docRef.getId());
                            callback.accept(true);
                        })
                .addOnFailureListener(
                        e -> {
                            Timber.e("Failed creating document: %s", e.getMessage());
                            callback.accept(false);
                        });
    }

    private Brand safeClone(Brand brand) {
        try {
            return (Brand) brand.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Not an instance of Brand");
        }
    }
}
