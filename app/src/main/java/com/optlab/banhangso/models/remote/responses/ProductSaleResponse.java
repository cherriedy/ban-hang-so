package com.optlab.banhangso.models.remote.responses;

import com.optlab.banhangso.models.remote.ProductSaleFirebaseObject;
import com.optlab.banhangso.models.remote.responses.base.Pagination;
import java.util.List;

public class ProductSaleResponse {

  private ProductSaleResponse() {}

  public static class Collection extends Pagination<ProductSaleFirebaseObject> {

    public Collection(
        List<ProductSaleFirebaseObject> items, int total, int page, int size, int pages) {
      super(items, total, page, size, pages);
    }
  }
}
