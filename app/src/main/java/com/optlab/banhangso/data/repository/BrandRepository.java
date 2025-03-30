package com.optlab.banhangso.data.repository;

import androidx.lifecycle.LiveData;

import com.optlab.banhangso.data.model.Brand;

import java.util.List;

public interface BrandRepository {
    LiveData<List<Brand>> getBrands();

    Brand getBrandById(String id);

    Brand getBrandByPosition(int position);

    int getPositionById(String id);
}
