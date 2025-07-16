package com.optlab.banhangso.features.main.sale.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.sale.models.CartUiModel;
import com.optlab.banhangso.models.domain.Cart;
import com.optlab.banhangso.models.domain.ProductSale;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CartItemUiModelMapper {

  @NonNull public static CartUiModel.Item fromProduct(@NonNull ProductSale productSale) {
    return new CartUiModel.Item(
        productSale.getId(),
        productSale.getName(),
        productSale.getThumbnailUrl(),
        productSale.getSellingPrice(),
        productSale.getDiscountPrice(),
        productSale.getPurchasePrice(),
        productSale.getStatus(),
        0 // Default quantity
        );
  }

  @NonNull public static Cart.Item toDomain(@NonNull CartUiModel.Item cartItemUiModel) {
    Cart.Item item = new Cart.Item();
    item.setId(cartItemUiModel.getId());
    item.setQuantity(cartItemUiModel.getQuantity());
    return item;
  }

  @NonNull public static List<Cart.Item> toDomains(@NonNull List<CartUiModel.Item> cartItemUiModels) {
    return cartItemUiModels.stream()
        .map(CartItemUiModelMapper::toDomain)
        .collect(Collectors.toList());
  }
}
