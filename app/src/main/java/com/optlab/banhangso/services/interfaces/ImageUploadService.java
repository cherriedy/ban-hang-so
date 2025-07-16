package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.responses.UploadResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import retrofit2.http.Part;

public interface ImageUploadService {
  Single<Response<UploadResponse>> uploadImage(@Part MultipartBody.Part file);
}
