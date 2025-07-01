package com.optlab.banhangso.models.remote.responses.base;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pagination<T> {
  @SerializedName("items")
  private final List<T> items;

  @SerializedName("total")
  private final int total;

  @SerializedName("page")
  private final int page;

  @SerializedName("size")
  private final int size;

  @SerializedName("pages")
  private final int pages;
}
