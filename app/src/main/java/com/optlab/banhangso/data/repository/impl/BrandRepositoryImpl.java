package com.optlab.banhangso.data.repository.impl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.repository.BrandRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import hilt_aggregated_deps._com_optlab_banhangso_di_RepositoryModule;
import timber.log.Timber;

@Singleton
public class BrandRepositoryImpl implements BrandRepository {
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<Brand>> brands = new MutableLiveData<>();

    @Inject
    public BrandRepositoryImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
        retrieveData();
    }

    private void retrieveData() {
        firestore.collection("brands")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Timber.e("Failed getting documents: %s", error.getMessage());
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        List<Brand> brandList = snapshots.getDocuments().stream()
                                .map(doc -> doc.toObject(Brand.class))
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
                .flatMap(brandList -> brandList.stream()
                        .filter(brand -> brand.getId().equals(id))
                        .findFirst()
                )
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

    private Brand safeClone(Brand brand) {
        try {
            return (Brand) brand.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Not an instance of Brand");
        }
    }
}
