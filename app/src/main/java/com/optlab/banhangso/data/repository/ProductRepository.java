package com.optlab.banhangso.data.repository;

import androidx.lifecycle.LiveData;

import com.optlab.banhangso.data.model.Product;

import java.util.List;
import java.util.function.Consumer;

public interface ProductRepository {
    LiveData<List<Product>> getProducts();

    Product getProductById(String id);

    int getPositionById(String id);

    void addProduct(Product newProduct, Consumer<Boolean> callback);

    void updateProduct(Product updatedProduct, Consumer<Boolean> callback);

    void deleteProduct(Product deletedProduct, Consumer<Boolean> callback);
    void createProduct(Product creatingProduct, Consumer<Boolean> callback);
}
