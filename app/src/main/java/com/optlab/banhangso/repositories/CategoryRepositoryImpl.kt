package com.optlab.banhangso.repositories

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.Category
import com.optlab.banhangso.models.exceptions.ApiResponseException
import com.optlab.banhangso.models.remote.mappers.CategoryFirebaseObjectMapper
import com.optlab.banhangso.paging.category.CategoryPagingSource
import com.optlab.banhangso.paging.category.CategorySearchPagingSource
import com.optlab.banhangso.repositories.interfaces.CategoryRepository
import com.optlab.banhangso.repositories.interfaces.PaginationRepository
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository
import com.optlab.banhangso.services.interfaces.CategoryService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CategoryRepositoryImpl
@Inject
constructor(
  private val preferencesRepository: PreferencesRepository,
  private val categoryService: CategoryService,
  private val errorHandler: ErrorHandler,
) : CategoryRepository, PaginationRepository {

  /**
   * Retrieves a paginated list of all categories for the current store.
   *
   * @return A Flowable stream of PagingData containing Category domain objects
   */
  override fun getCategories(): Flowable<PagingData<Category>> =
    Pager(pagingConfig) { CategoryPagingSource(preferencesRepository, categoryService) }
      .flowable
      .map { pagingData -> pagingData.map(CategoryFirebaseObjectMapper::toDomain) }

  /**
   * Retrieves a specific category by its ID.
   *
   * @param categoryId The unique identifier of the category to retrieve
   * @return A Single containing a Result with either the Category object or an error
   */
  override fun getCategory(categoryId: String): Single<Result<Category>> {
    return storeId
      .flatMap { storeId -> categoryService.getCategory(categoryId, storeId) }
      .map { response ->
        if (response.isSuccess) {
          response.data.item.let { categoryFirebaseObject ->
            CategoryFirebaseObjectMapper.toDomain(categoryFirebaseObject).let { category ->
              Result.Success(category)
            }
          }
        } else {
          ApiResponseException(response.message, response.code).let {
            Result.Failure(errorHandler.getError(it))
          }
        }
      }
      .onErrorReturn { Result.Failure(errorHandler.getError(it)) }
  }

  /**
   * Searches for categories based on a query string with pagination support.
   *
   * @param query The search term to filter categories
   * @return A Flowable stream of PagingData containing matching Category domain objects
   */
  override fun searchCategories(query: String): Flowable<PagingData<Category>> =
    Pager(pagingConfig) {
        CategorySearchPagingSource(preferencesRepository, categoryService, query)
      }
      .flowable
      .map { pagingData -> pagingData.map(CategoryFirebaseObjectMapper::toDomain) }

  /**
   * Updates an existing category with new information.
   *
   * @param category The Category object containing updated data
   * @return A Single containing a Result indicating success or failure of the update operation
   */
  override fun updateCategory(category: Category): Single<Result<Void>> =
    storeId.flatMap { storeId ->
      CategoryFirebaseObjectMapper.fromDomain(category)
        .let { categoryFirebaseObject ->
          categoryService.updateCategory(category.id, storeId, categoryFirebaseObject)
        }
        .map { response ->
          if (response.isSuccess) {
            Result.Success<Void>(null) as Result<Void>
          } else {
            ApiResponseException(response.message, response.code).let {
              Result.Failure(errorHandler.getError(it))
            }
          }
        }
        .onErrorReturn { Result.Failure(errorHandler.getError(it)) }
    }

  /**
   * Creates a new category in the store.
   *
   * @param category The Category object to be created
   * @return A Single containing a Result indicating success or failure of the creation operation
   */
  override fun createCategory(category: Category): Single<Result<Void>> =
    storeId.flatMap { storeId ->
      CategoryFirebaseObjectMapper.fromDomain(category)
        .let { categoryFirebaseObject ->
          categoryService.createCategory(storeId, categoryFirebaseObject)
        }
        .map { response ->
          if (response.isSuccess) {
            Result.Success<Void>(null) as Result<Void>
          } else {
            ApiResponseException(response.message, response.code).let {
              Result.Failure<Void>(errorHandler.getError(it))
            }
          }
        }
        .onErrorReturn { Result.Failure(errorHandler.getError(it)) }
    }

  /**
   * Deletes a category by its ID from the store.
   *
   * @param categoryId The unique identifier of the category to delete
   * @return A Single containing a Result indicating success or failure of the deletion operation
   */
  override fun deleteCategory(categoryId: String): Single<Result<Void>> =
    storeId
      .flatMap { storeId -> categoryService.deleteCategory(categoryId, storeId) }
      .map { response ->
        if (response.isSuccess) {
          Result.Success<Void>(null) as Result<Void>
        } else {
          ApiResponseException(response.message, response.code).let {
            Result.Failure<Void>(errorHandler.getError(it))
          }
        }
      }
      .onErrorReturn { Result.Failure(errorHandler.getError(it)) }

  /**
   * Provides access to the preferences repository instance.
   *
   * @return The PreferencesRepository instance used by this repository
   */
  override fun getPreferencesRepository(): PreferencesRepository = preferencesRepository
}
