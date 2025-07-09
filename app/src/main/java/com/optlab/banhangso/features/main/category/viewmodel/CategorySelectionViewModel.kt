package com.optlab.banhangso.features.main.category.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.cachedIn
import com.optlab.banhangso.features.main.category.models.CategoryUiModel
import com.optlab.banhangso.features.main.category.models.mappers.CategoryUiModelMapper
import com.optlab.banhangso.repositories.interfaces.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

@HiltViewModel
class CategorySelectionViewModel
@Inject
constructor(private val categoryRepository: CategoryRepository) : ViewModel() {

  private val _categories: Flowable<PagingData<CategoryUiModel>>
  val categories: Flowable<PagingData<CategoryUiModel>>
    get() = _categories

  init {
    @Suppress("OPT_IN_USAGE")
    _categories =
      categoryRepository.categories
        .map { pagingData -> pagingData.map(CategoryUiModelMapper::fromDomain) }
        .cachedIn(viewModelScope)
  }
}
