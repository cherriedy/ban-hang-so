package com.optlab.banhangso.features.main.activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class SharedViewModel extends ViewModel {
  private final MutableLiveData<Boolean> isChecking = new MutableLiveData<>(true);

  @Inject
  public SharedViewModel() {}

  public void setIsChecking(boolean isChecking) {
    this.isChecking.setValue(isChecking);
  }

  public LiveData<Boolean> isChecking() {
    return isChecking;
  }
}
