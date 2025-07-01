package com.optlab.banhangso.models.remote;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class CategoryFirebaseObject {

  private String id;
  private String storeId;
  private String name;
  @ServerTimestamp private Date createdAt;
  @ServerTimestamp private Date updatedAt;

  @Exclude
  public String getId() {
    return id;
  }

  @Exclude
  public String getStoreId() {
    return storeId;
  }
}
