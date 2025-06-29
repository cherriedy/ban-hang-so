package com.optlab.banhangso.models.remote.render_api;

import com.google.gson.annotations.SerializedName;

public record ResponseObject<T>(
    @SerializedName("message") String message,
    @SerializedName("status") String status,
    @SerializedName("code") Integer code,
    @SerializedName("data") T data) {
  public boolean isError() {
    return "error".equals(status);
  }

  public boolean isSuccess() {
    return "success".equals(status);
  }

  public boolean isFailure() {
    return "fail".equals(status);
  }
}
