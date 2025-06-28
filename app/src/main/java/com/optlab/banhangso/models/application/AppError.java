package com.optlab.banhangso.models.application;

public sealed class AppError {
    public static final class NetServiceError extends AppError {}

    public static final class InvalidArgument extends AppError {}

    public static final class NotFoundError extends AppError {}

    public static final class UnknownError extends AppError {}
}
