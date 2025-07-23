package com.optlab.banhangso.features.main.report.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.optlab.banhangso.databinding.FragmentCustomerReportBinding;

public class CustomerReportFragment extends Fragment {

  private FragmentCustomerReportBinding binding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentCustomerReportBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }
}
