package com.optlab.banhangso.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.optlab.banhangso.data.model.Brand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class BrandRepository {

    private static BrandRepository instance;
    private MutableLiveData<List<Brand>> brandList = new MutableLiveData<>();

    private BrandRepository() {
        List<Brand> brands = new ArrayList<>();
        brands.add(new Brand(1, "BR001", "Acecook!"));
        brands.add(new Brand(2, "BR002", "RaniGu"));
        brands.add(new Brand(3, "BR003", "AnMoreira"));
        brands.add(new Brand(4, "BR004", "FatmaBegum"));
        brands.add(new Brand(5, "BR005", "GabrielaHuo"));

        brandList.setValue(brands);
    }

    public static synchronized BrandRepository getInstance() {
        return (instance == null) ? new BrandRepository() : instance;
    }

    public LiveData<List<Brand>> getBrands() {
        return brandList;
    }

    public Brand getBrandById(int id) {
        return Optional.ofNullable(brandList.getValue())
                .flatMap(brands -> getFirstMatch(brands, id))
                .map(this::safeClone)
                .orElse(null);
    }

    public Brand getBrandByPosition(int position) {
        return Optional.ofNullable(brandList.getValue())
                .flatMap(brands -> Optional.of(brands.get(position)))
                .map(this::safeClone)
                .orElse(null);
    }

    public int getPositionById(int id) {
        List<Brand> brands = brandList.getValue();
        if (brands == null) return -1;

        return IntStream.range(0, brands.size())
                .filter(i -> brands.get(i).getId() == id)
                .findFirst()
                .orElse(-1);
    }

    private Optional<Brand> getFirstMatch(List<Brand> brands, int id) {
        return brands.stream()
                .filter(brand -> brand.getId() == id)
                .findFirst();
    }

    private Brand safeClone(Brand brand) {
        try {
            return (Brand) brand.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

}
