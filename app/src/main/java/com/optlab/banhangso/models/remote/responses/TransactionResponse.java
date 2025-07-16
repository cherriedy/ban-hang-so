package com.optlab.banhangso.models.remote.responses;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.TransactionRecordFirebaseObject;
import com.optlab.banhangso.models.remote.TransactionSummaryFirebaseObject;
import com.optlab.banhangso.models.remote.responses.base.Pagination;
import java.util.List;

public class TransactionResponse {

  public static class Collection extends Pagination<TransactionSummaryFirebaseObject> {
    public Collection(
        List<TransactionSummaryFirebaseObject> items, int total, int page, int size, int pages) {
      super(items, total, page, size, pages);
    }
  }

  public record Item(@SerializedName("item") TransactionRecordFirebaseObject item) {}
}
