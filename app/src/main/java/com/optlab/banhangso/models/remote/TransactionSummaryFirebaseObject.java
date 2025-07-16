package com.optlab.banhangso.models.remote;

import androidx.annotation.Nullable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryFirebaseObject {

  @Nullable private String id;
  @Nullable private String staffName;
  @Nullable private String customerName;
  @Nullable private Double price;
  @Nullable private Date createdAt;
}
