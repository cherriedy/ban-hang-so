package com.optlab.banhangso.domain.repository;

import androidx.lifecycle.LiveData;

import com.optlab.banhangso.domain.model.Brand;

import java.util.List;
import java.util.function.Consumer;

public interface BrandRepository {
    LiveData<List<Brand>> getBrands();

    Brand getBrandById(String id);

    Brand getBrandByPosition(int position);

    int getPositionById(String id);

    void updateBrand(Brand brand, Consumer<Boolean> callback);

    void deleteBrand(Brand brand, Consumer<Boolean> callback);

    void createBrand(Brand brand, Consumer<Boolean> callback);
}
