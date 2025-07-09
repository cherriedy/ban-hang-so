package com.optlab.banhangso.models.remote.responses;

import com.google.gson.annotations.SerializedName;

public record UploadResponse(@SerializedName("imageUrl") String imageUrl) {}
