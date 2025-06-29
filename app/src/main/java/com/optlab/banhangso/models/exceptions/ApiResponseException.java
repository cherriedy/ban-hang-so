package com.optlab.banhangso.models.exceptions;

/** Exception thrown when the API returns an unsuccessful response. */
public class ApiResponseException extends RuntimeException {
  private final int code;

  public ApiResponseException(String message, int code) {
    super(message);
    this.code = code;
  }

  public ApiResponseException(String message) {
    this(message, 0);
  }

  /**
   * Get the HTTP status code or custom error code from the API
   *
   * @return error code
   */
  public int getCode() {
    return code;
  }
}
