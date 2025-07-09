package com.optlab.banhangso.features.shared.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public abstract class RxViewModel extends ViewModel {

  protected final CompositeDisposable disposables = new CompositeDisposable();
  protected final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
  protected final MutableLiveData<Integer> messageResId = new MutableLiveData<>();

  public LiveData<Boolean> isLoading() {
    return isLoading;
  }

  public LiveData<Integer> getMessageResId() {
    return messageResId;
  }

  @Override
  protected void onCleared() {
    disposables.clear();
    super.onCleared();
  }
}
