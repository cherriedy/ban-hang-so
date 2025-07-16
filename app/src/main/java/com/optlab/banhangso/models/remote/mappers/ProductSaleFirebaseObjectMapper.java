package com.optlab.banhangso.models.remote.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.ProductSale;
import com.optlab.banhangso.models.remote.ProductSaleFirebaseObject;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;

@UtilityClass
public class ProductSaleFirebaseObjectMapper {

  @NonNull @Contract("_ -> new")
  public static ProductSale toDomain(@NonNull ProductSaleFirebaseObject productSaleFirebaseObject) {
    return new ProductSale(
        productSaleFirebaseObject.getId(),
        productSaleFirebaseObject.getName(),
        productSaleFirebaseObject.getThumbnailUrl(),
        productSaleFirebaseObject.getSellingPrice(),
        productSaleFirebaseObject.getDiscountPrice(),
        productSaleFirebaseObject.getPurchasePrice(),
        productSaleFirebaseObject.isStatus());
  }
}
