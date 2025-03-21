package com.optlab.banhangso.ui.home.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.QuickShortcut;

import java.util.List;

public class QuickShortcutViewModel extends ViewModel {

    private final MutableLiveData<List<QuickShortcut>> shortcuts;
    private final MutableLiveData<Integer> clickedPosition;

    public QuickShortcutViewModel() {
        shortcuts = new MutableLiveData<>();
        clickedPosition = new MutableLiveData<>();
    }

    public LiveData<Integer> getClickedPosition() {
        return clickedPosition;
    }

    public void setClickedPosition(int position) {
        clickedPosition.setValue(position);
    }

    public LiveData<List<QuickShortcut>> getShortcuts() {
        return shortcuts;
    }

    public void setShortcutList(List<QuickShortcut> shortcutList) {
        shortcuts.setValue(shortcutList);
    }
}
