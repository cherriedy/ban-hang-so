package com.optlab.banhangso.ui.stock.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.optlab.banhangso.databinding.FragmentStockListBinding;

public class StockListFragment extends Fragment {

    private FragmentStockListBinding binding;

    public StockListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStockListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavigationUI.setupWithNavController(binding.toolBar, NavHostFragment.findNavController(this));

//        NavHostFragment.findNavController(this).addOnDestinationChangedListener((controller, destination, argument) -> {
//            if (destination.getId() == R.id.productManagementFragment) {
//                binding.toolBar.setTitle("vcl");
//            }
//        });
    }
}