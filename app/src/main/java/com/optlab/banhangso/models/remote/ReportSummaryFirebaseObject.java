package com.optlab.banhangso.models.remote;

import androidx.annotation.Nullable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportSummaryFirebaseObject {
  @Nullable private Double revenue;
  @Nullable private Integer transactions;
  @Nullable private Integer customers;
  @Nullable private Date date;
}
