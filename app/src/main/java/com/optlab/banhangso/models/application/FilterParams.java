package com.optlab.banhangso.models.application;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterParams {
  @Builder.Default private String payment = "";

  @Builder.Default private String startDate = "";

  @Builder.Default private String endDate = "";

  @Builder.Default private Double priceFrom = 0.0;

  @Builder.Default private Double priceTo = 0.0;

  /**
   * Converts this FilterParams object to a Map<String, Object>
   *
   * @return Map containing all field names as keys and their values as objects
   */
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    if (!payment.isBlank()) {
      map.put("payment_method", this.payment);
    }

    if (!startDate.isBlank()) {
      map.put("start_date", this.startDate);
    }

    if (!endDate.isBlank()) {
      map.put("end_date", this.endDate);
    }

    if (priceFrom != 0.0) {
      map.put("min_amount", this.priceFrom);
    }

    if (priceTo != 0.0) {
      map.put("max_amount", this.priceTo);
    }
    return map;
  }
}
