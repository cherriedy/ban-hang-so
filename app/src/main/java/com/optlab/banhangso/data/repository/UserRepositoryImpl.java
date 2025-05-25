package com.optlab.banhangso.data.repository;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.optlab.banhangso.data.local.dao.UserDao;
import com.optlab.banhangso.data.local.entity.UserEntity;
import com.optlab.banhangso.data.remote.dto.UserDto;
import com.optlab.banhangso.data.remote.service.FirebaseUserService;
import com.optlab.banhangso.domain.mapper.UserMapper;
import com.optlab.banhangso.domain.model.User;
import com.optlab.banhangso.domain.repository.UserRepository;
import com.optlab.banhangso.domain.util.Resource;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import timber.log.Timber;

public class UserRepositoryImpl implements UserRepository {
    private final UserDao userDao;
    private final FirebaseUserService firebaseUserService;

    public UserRepositoryImpl(UserDao userDao, FirebaseUserService firebaseUserService) {
        this.userDao = userDao;
        this.firebaseUserService = firebaseUserService;
    }

    @Override
    @SuppressLint("CheckResult")
    public Single<Resource<User>> saveUserRemote(@NonNull String uuid, @NonNull User domainUser) {
        UserDto userDto = UserMapper.domainToDto(domainUser);
        return firebaseUserService
                .saveUser(uuid, userDto)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(d -> Timber.d("Starting Firebase user save"))
                .doOnSuccess(s -> Timber.d("Firebase save succeeded: %s", s))
                .map(this::mapDtoToDomainResource)
                .onErrorReturn(
                        e -> {
                            Timber.e(e, "Error saving user: %s", e.getMessage());
                            return Resource.error(e.getMessage(), null);
                        });
    }

    @Override
    public Maybe<Resource<User>> getUserById(@NonNull String uuid) {
        return firebaseUserService
                .getUserById(uuid)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(unused -> Timber.d("Starting Firebase user search"))
                .doOnSuccess(dto -> Timber.d("Firebase search succeeded: %s", dto))
                .map(this::mapDtoToDomainResource)
                .onErrorReturn(e -> Resource.error(e.getMessage(), null));
    }

    private Resource<User> mapDtoToDomainResource(UserDto userDto) {
        return Resource.success(UserMapper.dtoToDomain(userDto));
    }

    @SuppressLint("CheckResult")
    @Override
    public Flowable<Resource<User>> saveUser(@NonNull String uuid, @NonNull User domainUser) {
        UserDto userDto = UserMapper.domainToDto(domainUser);

        Flowable<Resource<User>> initialLoadingState = Flowable.just(Resource.loading(domainUser));
        Flowable<Resource<User>> saveOperation =
                firebaseUserService
                        .saveUser(uuid, userDto)
                        .doOnSubscribe(unused -> Timber.d("Starting Firebase user save"))
                        .doOnSuccess(dto -> Timber.d("Firebase save succeeded: %s", dto))
                        .flatMapPublisher(this::saveToDatabase)
                        .map(this::mapToDomainResource)
                        .onErrorReturn(e -> Resource.error(e.getMessage(), null));

        return Flowable.defer(
                        () ->
                                initialLoadingState
                                        .subscribeOn(Schedulers.io())
                                        .concatWith(saveOperation))
                .doOnError(e -> Timber.e(e, "Unexpected error in saveUser flow"));
    }

    private Resource<User> mapToDomainResource(Resource<UserEntity> resource) {
        if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
            return Resource.success(UserMapper.entityToDomain(resource.data));
        } else if (resource.status == Resource.Status.LOADING && resource.data != null) {
            return Resource.loading(UserMapper.entityToDomain(resource.data));
        } else {
            return Resource.error(resource.message, null);
        }
    }

    private Flowable<Resource<UserEntity>> saveToDatabase(UserDto userDto) {
        UserEntity entity = UserMapper.dtoToEntity(userDto);
        Timber.d("Starting local DB save for user: %s", userDto.getId());

        return userDao.insertUser(entity)
                .andThen(
                        userDao.getUserById(entity.getId())
                                .toFlowable()
                                .map(
                                        savedEntity -> {
                                            Timber.d(
                                                    "Successfully read back user from DB: %s",
                                                    savedEntity.getId());
                                            return Resource.success(savedEntity);
                                        }))
                .onErrorReturn(e -> Resource.error(e.getMessage(), null))
                .doOnComplete(
                        () -> Timber.d("Local DB save completed for user: %s", userDto.getId()));
    }
}
