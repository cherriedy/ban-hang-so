package com.optlab.banhangso.internal.network;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {

  /**
   * This interface provides a method to receive updates on the number of bytes written, the total
   * number of bytes, and the percentage of completion.
   */
  @FunctionalInterface
  public interface ProgressListener {
    void onProgress(long bytesWritten, long totalBytes, int percentage);
  }

  @NonNull private final RequestBody requestBody;
  @NonNull private final ProgressListener listener;

  public ProgressRequestBody(@NonNull RequestBody requestBody, @NonNull ProgressListener listener) {
    this.requestBody = requestBody;
    this.listener = listener;
  }

  /**
   * Returns the content type of the request body. This method is used to specify the media type of
   * the request body, which is typically used by the server to understand how to process the
   * request. It is important to note that this method does not return the content type of the
   * entire request, but rather just the content type of the request body.
   *
   * @return the content type of the request body
   */
  @Override
  public MediaType contentType() {
    return requestBody.contentType();
  }

  /**
   * Returns the length of the content in bytes. This method is used to determine the size of the
   * request body, which is useful for the server to allocate resources and for the client to
   * display progress. It is important to note that this method may return -1 if the length is
   * unknown, which can happen for certain types of request bodies such as streaming or chunked
   * transfers.
   *
   * @return the length of the content in bytes, or -1 if unknown
   * @throws IOException if an I/O error occurs while determining the content length
   */
  @Override
  public long contentLength() throws IOException {
    return requestBody.contentLength();
  }

  /**
   * Writes the content of the request body to the specified sink. This method is used to transfer
   * the data from the request body to the sink, which is typically a network socket or file output
   * stream. It is important to note that this method does not handle the progress updates; instead,
   * it uses a custom `Sink` that tracks the number of bytes written and notifies the listener about
   * the progress. This allows the client to monitor the upload progress in real-time.
   *
   * @param sink the sink to which the content of the request body will be written
   * @throws IOException if an I/O error occurs while writing to the sink
   */
  @Override
  public void writeTo(@NonNull BufferedSink sink) throws IOException {
    // Wrap the provided sink with a ProgressSink to track progress
    BufferedSink progressSink = Okio.buffer(new ProgressSink(sink));
    // Write the content of the request body to the progress sink
    requestBody.writeTo(progressSink);
    // Flush the progress sink to ensure all data is written
    progressSink.flush();
  }

  /**
   * Returns a string representation of the request body. This method is used for debugging and
   * logging purposes, providing a human-readable description of the request body.
   */
  private class ProgressSink extends ForwardingSink {

    private long contentLength;
    private long bytesWritten = 0L;
    private int lastPercentage = -1;

    public ProgressSink(Sink delegate) {
      super(delegate);
      try {
        contentLength = contentLength();
      } catch (IOException e) {
        contentLength = -1;
      }
    }

    /**
     * Writes the specified number of bytes from the source buffer to the sink. This method is used
     * to transfer data from the source buffer to the sink, while also tracking the number of bytes
     * written. It updates the `bytesWritten` field and notifies the listener about the progress of
     * the upload. The percentage of completion is calculated based on the total number of bytes
     * written and the total content length. If the content length is unknown (i.e., -1), it will
     * not update the percentage but will still notify the listener about the bytes written.
     *
     * @param source the buffer containing the data to be written
     * @param byteCount the number of bytes to write from the source buffer
     * @throws IOException if an I/O error occurs while writing to the sink
     */
    @Override
    public void write(@NonNull Buffer source, long byteCount) throws IOException {
      super.write(source, byteCount);
      bytesWritten += byteCount; // Update the number of bytes written

      if (contentLength > 0) {
        int percentage = (int) ((bytesWritten * 100) / contentLength);
        percentage = Math.min(percentage, 100);
        if (percentage != lastPercentage) {
          lastPercentage = percentage;
          listener.onProgress(bytesWritten, contentLength, percentage);
        }
      }
    }
  }
}
