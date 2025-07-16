package com.optlab.banhangso.features.main.sale.viewmodels

import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.cachedIn
import com.google.gson.Gson
import com.optlab.banhangso.R
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel
import com.optlab.banhangso.features.main.sale.models.CartUiModel
import com.optlab.banhangso.features.main.sale.models.ReceiptUiModel
import com.optlab.banhangso.features.main.sale.models.mappers.CartItemUiModelMapper
import com.optlab.banhangso.features.main.sale.models.mappers.CartUiModelMapper
import com.optlab.banhangso.features.main.sale.models.mappers.ReceiptUiModelMapper
import com.optlab.banhangso.features.shared.viewmodels.RxViewModel
import com.optlab.banhangso.models.application.AppError
import com.optlab.banhangso.models.application.Payment
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.ProductSale
import com.optlab.banhangso.models.domain.TransactionRecord
import com.optlab.banhangso.repositories.interfaces.ProductSaleRepository
import com.optlab.banhangso.repositories.interfaces.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SaleViewModel
@Inject
constructor(
    private val productSaleRepository: ProductSaleRepository,
    private val transactionRepository: TransactionRepository,
) : RxViewModel() {

    private val _items: Flowable<PagingData<CartUiModel.Item>>
    val items: Flowable<PagingData<CartUiModel.Item>>
        get() = _items

    private val _searchQuery: ObservableField<String> = ObservableField<String>()
    val searchQuery: ObservableField<String>
        get() = _searchQuery

    private val _searchProcessor: BehaviorProcessor<String> = BehaviorProcessor.createDefault("")

    private val _cart: MutableLiveData<CartUiModel> = MutableLiveData(CartUiModel())
    val cart: LiveData<CartUiModel>
        get() = _cart

    private val _canSubmit: LiveData<Boolean> = cart.map { it.totalItems > 0 }
    val canSubmit: LiveData<Boolean>
        get() = _canSubmit

    private val _paymentResult: MutableLiveData<Boolean> = MutableLiveData()
    val paymentResult: LiveData<Boolean>
        get() = _paymentResult

    private val _receipt: MutableLiveData<ReceiptUiModel> = MutableLiveData()
    val receipt: LiveData<ReceiptUiModel>
        get() = _receipt

    init {
        @Suppress("OPT_IN_USAGE")
        _items =
            _searchProcessor
                .distinctUntilChanged()
                .doOnNext { Timber.d("Search query updated: $it") }
                .switchMap { query ->
                    if (query.isBlank()) {
                        productSaleRepository.productSales
                    } else {
                        productSaleRepository.searchProductSales(query)
                    }
                }
                .map { pagingData -> pagingData.map(this::toCartUiModelItem) }
                .cachedIn(viewModelScope)
                .replay(1)
                .refCount()

        _searchQuery.addOnPropertyChangedCallback(
            object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    @Suppress("UNCHECKED_CAST")
                    val query: String? = (sender as ObservableField<String>).get()
                    _searchProcessor.onNext(query ?: "")
                }
            },
        )
    }

    /** Map ProductSale to CartUiModel.Item with current cart quantity */
    private fun toCartUiModelItem(productSale: ProductSale): CartUiModel.Item {
        return CartItemUiModelMapper.fromProduct(productSale).apply {
            // Map quantity from cart for consistency
            this.quantity = _cart.value!!.items[this.id]?.quantity ?: 0
        }
    }

    fun updateQuantity(item: CartUiModel.Item) {
        item.id?.let { key ->
            _cart.value!!.let { cart ->
                cart.items
                    .computeIfAbsent(key) { item.copy() }
                    .apply { this.quantity = item.quantity }
                    .also {
                        cart.refreshCart()
                        _cart.value = cart
                        Timber.d("Updated quantity for ${item.name}: ${item.quantity}")
                    }
            }
        }
    }

    fun removeItem(item: CartUiModel.Item) {
        item.id?.let { key ->
            _cart.value?.let { cartModel ->
                cartModel.items.remove(key)
                cartModel.refreshCart()
                _cart.value = cartModel
            }

            Timber.d("Removed item from cart: ${item.name}")
        }
    }

    fun setCustomer(customer: CustomerUiModel) {
        _cart.value = _cart.value?.apply { this.customer = customer }
    }

    fun setPaymentMethod(paymentType: Payment.Method ) {
        _cart.value = _cart.value?.apply { this.paymentMethod = paymentType }
    }

    fun onPay(@Suppress("UNUSED_PARAMETER") view: View) {
        val transactionCart = CartUiModelMapper.toDomain(cart.value!!)
        Timber.d("Cart in payment: ${Gson().toJson(transactionCart)}")

        val disposable =
            transactionRepository
                .setTransaction(transactionCart)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { isLoading.postValue(true) }
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { isLoading.value = false }
                .subscribe(this::onPaySuccess, this::onPayError)

        disposables.add(disposable)
    }

    private fun onPaySuccess(result: Result<TransactionRecord>) {
        messageResId.value =
            when (result) {
                is Result.Success -> {
                    // Get the receipt from the transaction result.
                    _receipt.value = ReceiptUiModelMapper.fromTransaction(result.data!!)

                    Timber.d("Transaction created successfully: ${Gson().toJson(_receipt.value)}")

                    // Set the result of the payment operation.
                    _paymentResult.value = true

                    // Return success message resource.
                    R.string.notify_transaction_create_success
                }
                is Result.Failure -> {
                    // Set the result of the payment operation.
                    _paymentResult.value = false

                    // Return error message resource.
                    if (result.error is AppError.NetServiceError) {
                        R.string.error_network
                    } else {
                        R.string.error_unknown
                    }
                }
            }
    }

    private fun onPayError(throwable: Throwable) {
        messageResId.value = R.string.error_unknown
        Timber.e(throwable, "There was an error processing the payment: ${throwable.message}")
    }
}
