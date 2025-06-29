package com.optlab.banhangso.features.shared.binding;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;

public class SearchViewBindingAdapter {
  @BindingAdapter("query")
  public static void setQuery(
      @NonNull SearchView view, @NonNull ObservableField<String> observableField) {
    view.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
            observableField.set(query);
            return true;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
            observableField.set(newText);
            return true;
          }
        });
  }
}
