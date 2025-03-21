package com.optlab.banhangso.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ProductRepository {
    private static ProductRepository instance;
    private final MutableLiveData<List<Product>> productList = new MutableLiveData<>();

    private ProductRepository() {
        List<Product> initialProducts = new ArrayList<>();
        initialProducts.add(
                new Product.Builder(1001L)
                        .name("Mì Hảo Hảo vị tôm chua cay gói 75g")
                        .sellingPrice(4400)
                        .purchasePrice(3500)
                        .discountPrice(3933)
                        .brand(new Brand(1, "BR001", "Acecook"))
                        .category(new Category(1, "CG001", "Mì ăn liền"))
                        .status(Product.ProductStatus.IN_STOCK)
                        .build()
        );

        initialProducts.add(
                new Product.Builder(1002L)
                        .name("Nước tương đậu nành Maggi đậm đặc chai 700ml")
                        .sellingPrice(29000)
                        .purchasePrice(24000)
                        .discountPrice(0)
                        .brand(new Brand(2, "BR002", "Maggi (Việt Nam)"))
                        .category(new Category(2, "CG002", "Nước tương"))
                        .status(Product.ProductStatus.OUT_STOCK)
                        .build()
        );

        initialProducts.add(
                new Product.Builder(1003L)
                        .name("Sườn non heo nhập khẩu Đức túi 1kg")
                        .sellingPrice(120000)
                        .purchasePrice(100000)
                        .category(new Category(1, "CG001", "Mì ăn liền"))
                        .status(Product.ProductStatus.OUT_STOCK)
                        .build()
        );

//        initialProducts.add(new Product.Builder(1004L).name("Bắp cải trắng").sellingPrice(344.0).build());
//        initialProducts.add(new Product.Builder(1005L).name("Rau mồng tơi 400g").sellingPrice(871.0).build());
//        initialProducts.add(new Product.Builder(1006L).name("Thùng 48 chai sữa chua uống hương cam Fristi 80ml").sellingPrice(624.0).build());

        productList.setValue(initialProducts);
    }

    public static synchronized ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepository();
        }
        return instance;
    }

    public LiveData<List<Product>> getProducts() {
        return productList;
    }

    public Product getProductById(long id) {
        return Optional.ofNullable(productList.getValue())
                .flatMap(products -> findFirstMatch(products, id))
                .map(this::safeClone)
                .orElse(null);
    }

    public int getPositionById(long id) {
        List<Product> products = productList.getValue();
        if (products == null) return -1;

        return IntStream.range(0, products.size())
                .filter(i -> products.get(i).getId() == id)
                .findFirst()
                .orElse(-1);
    }


    public void addProduct(Product newProduct) {
        List<Product> currentList = productList.getValue() != null
                ? new ArrayList<>(productList.getValue())
                : new ArrayList<>();
        currentList.add(newProduct);
        productList.setValue(currentList);
    }

    public void updateProduct(Product updatedProduct) {
        List<Product> currentList = productList.getValue();
        if (currentList == null) return;

        List<Product> newList = currentList.stream()
                .map(product -> product.getId() == updatedProduct.getId() ? updatedProduct : product)
                .collect(Collectors.toList());

        productList.setValue(newList);
    }

    private Optional<Product> findFirstMatch(List<Product> products, long id) {
        return products.stream()
                .filter(product -> product.getId() == id)
                .findFirst();
    }

    private Product safeClone(Product product) {
        try {
            return (Product) product.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}