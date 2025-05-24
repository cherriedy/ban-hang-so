package com.optlab.banhangso.ui.authentication.viewmodel;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.Store;
import com.optlab.banhangso.data.model.User;
import com.optlab.banhangso.data.model.app.Resource;
import com.optlab.banhangso.data.repository.StoreRepository;
import com.optlab.banhangso.data.repository.UserRepository;
import com.optlab.banhangso.ui.authentication.state.SignUpValidationState;
import com.optlab.banhangso.util.validator.AuthValidator;

import dagger.hilt.android.lifecycle.HiltViewModel;

import timber.log.Timber;

import java.util.List;
import java.util.function.Consumer;

import javax.inject.Inject;

@HiltViewModel
public class SignUpViewModel extends ViewModel {
    private static final String KEY_IS_ADMIN = "is_admin";

    private final SavedStateHandle savedStateHandle;
    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final AuthValidator validator;
    private final MutableLiveData<User> user = new MutableLiveData<>(new User());
    private final MutableLiveData<Store> store = new MutableLiveData<>(new Store());
    private final MutableLiveData<SignUpValidationState> validationState =
            new MutableLiveData<>(new SignUpValidationState());
    private final MutableLiveData<User.Store> userStore =
            new MutableLiveData<>(new User.Store(null));
    private final MutableLiveData<Boolean> isCreating = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> createResult = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> signUpErrorResId = new MutableLiveData<>();

    private Observable.OnPropertyChangedCallback userPropertyChangedCallback;
    private Observable.OnPropertyChangedCallback storePropertyChangedCallback;
    private Observable.OnPropertyChangedCallback staffStorePropertyChangedCallback;

    private String email;
    private String password;

    @Inject
    public SignUpViewModel(
            SavedStateHandle savedStateHandle,
            FirebaseAuth firebaseAuth,
            UserRepository userRepository,
            StoreRepository storeRepository,
            AuthValidator validator) {
        this.savedStateHandle = savedStateHandle;
        this.firebaseAuth = firebaseAuth;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.validator = validator;

        Boolean isAdmin = savedStateHandle.get(KEY_IS_ADMIN);
        savedStateHandle.set(KEY_IS_ADMIN, isAdmin != null ? isAdmin : true);

        observeUserChanges();
        observeStoreChanges();
        observeStaffStoreChanges();
    }

    @Override
    protected void onCleared() {
        User userInstance = user.getValue();
        if (userInstance != null && userPropertyChangedCallback != null) {
            userInstance.removeOnPropertyChangedCallback(userPropertyChangedCallback);
        }
        Store storeInstance = store.getValue();
        if (storeInstance != null && storePropertyChangedCallback != null) {
            storeInstance.removeOnPropertyChangedCallback(storePropertyChangedCallback);
        }
        User.Store staffStoreInstance = userStore.getValue();
        if (staffStoreInstance != null && storePropertyChangedCallback != null) {
            staffStoreInstance.removeOnPropertyChangedCallback(storePropertyChangedCallback);
        }
        super.onCleared();
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void setUser(@NonNull User newUser) {
        user.setValue(newUser);
    }

    public LiveData<Store> getStore() {
        return store;
    }

    public LiveData<SignUpValidationState> getValidationState() {
        return validationState;
    }

    public LiveData<User.Store> getUserStore() {
        return userStore;
    }

    public LiveData<Boolean> getIsAdmin() {
        return savedStateHandle.getLiveData(KEY_IS_ADMIN);
    }

    public void setIsAdmin(boolean isAdmin) {
        savedStateHandle.set(KEY_IS_ADMIN, isAdmin);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LiveData<Boolean> getIsCreating() {
        return isCreating;
    }

    public LiveData<Boolean> getCreateResult() {
        return createResult;
    }

    public LiveData<Integer> getSignUpErrorResId() {
        return signUpErrorResId;
    }

    private void updateValidationState(Consumer<SignUpValidationState> action) {
        SignUpValidationState state = validationState.getValue();
        if (state != null) {
            action.accept(state);
            validationState.setValue(state);
        }
    }

    public void validateContactName() {
        updateValidationState(
                state -> {
                    User user = this.user.getValue();
                    if (user != null) {
                        state.setContactNameError(
                                validator.validateContactName(user.getContactName()));
                    }
                });
    }

    public void validateContactPhone() {
        updateValidationState(
                state -> {
                    User user = this.user.getValue();
                    if (user != null) {
                        state.setContactPhoneError(validator.validatePhoneNumber(user.getPhone()));
                    }
                });
    }

    public void validateStoreName() {
        updateValidationState(
                state -> {
                    Store store = this.store.getValue();
                    if (store != null) {
                        state.setStoreNameError(validator.validateStoreName(store.getName()));
                    }
                });
    }

    public void validateStoreDescription() {
        updateValidationState(
                state -> {
                    Store store = this.store.getValue();
                    if (store != null) {
                        state.setStoreDescriptionError(
                                validator.validateStoreDescription(store.getDescription()));
                    }
                });
    }

    public void validateStoreCode() {
        updateValidationState(
                state -> {
                    User.Store store = this.userStore.getValue();
                    if (store != null) {
                        state.setStoreCodeError(validator.validateStoreCode(store.getId()));
                    }
                });
    }

    public void validateTermsAndConditions(boolean isChecked) {
        updateValidationState(
                state ->
                        state.setTermsAndConditionsError(
                                validator.validateAgreeTermsAndConditions(isChecked)));
    }

    public void updateUserRole(String role) {
        updateValidationState(state -> state.setRole(role));
    }

    private void observeUserChanges() {
        User userInstance = user.getValue();
        if (userInstance != null && userPropertyChangedCallback == null) {
            userPropertyChangedCallback =
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            switch (propertyId) {
                                case BR.name -> validateContactName();
                                case BR.phone -> validateContactPhone();
                            }
                        }
                    };
            userInstance.addOnPropertyChangedCallback(userPropertyChangedCallback);
        }
    }

    private void observeStoreChanges() {
        Store storeInstance = store.getValue();
        if (storeInstance != null && storePropertyChangedCallback == null) {
            storePropertyChangedCallback =
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            switch (propertyId) {
                                case BR.name -> validateStoreName();
                                case BR.description -> validateStoreDescription();
                            }
                        }
                    };
            storeInstance.addOnPropertyChangedCallback(storePropertyChangedCallback);
        }
    }

    private void observeStaffStoreChanges() {
        User.Store storeInstance = userStore.getValue();
        if (storeInstance != null && staffStorePropertyChangedCallback == null) {
            staffStorePropertyChangedCallback =
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            switch (propertyId) {
                                case BR.id -> validateStoreCode();
                            }
                        }
                    };
            storeInstance.addOnPropertyChangedCallback(staffStorePropertyChangedCallback);
        }
    }

    public void onSignUpButtonClick(@NonNull View view) {
        isCreating.setValue(true);
        signUpErrorResId.setValue(null);
        firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(this::onAuthSuccess)
                .addOnFailureListener(this::onAuthFailure);
    }

    /**
     * Handles authentication failure during sign-up and updates the error message.
     *
     * @param e the exception thrown during authentication
     */
    private void onAuthFailure(Exception e) {
        isCreating.setValue(false);
        if (e instanceof FirebaseAuthUserCollisionException) {
            signUpErrorResId.setValue(R.string.error_email_already_in_use);
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            signUpErrorResId.setValue(R.string.error_invalid_email);
        } else if (e instanceof FirebaseNetworkException) {
            signUpErrorResId.setValue(R.string.error_network);
        } else if (e instanceof FirebaseAuthException
                && e.getMessage() != null
                && e.getMessage().contains("blocked all requests")) {
            signUpErrorResId.setValue(R.string.error_too_many_requests);
        } else if (e instanceof FirebaseAuthException
                && e.getMessage() != null
                && e.getMessage().contains("operation is not allowed")) {
            signUpErrorResId.setValue(R.string.error_operation_not_allowed);
        } else {
            signUpErrorResId.setValue(R.string.error_auth_failed);
        }
        createResult.setValue(false);
    }

    /**
     * Sets the result of user creation and updates the UI accordingly.
     *
     * <p>This method is called after the user is created in the database. It updates the isCreating
     * LiveData to false and sets the createResult LiveData to the result of the user creation
     * operation.
     *
     * @param result the result of user creation
     */
    private void setUserCreationResult(Boolean result) {
        Timber.d("User creation result: %s", result);
        isCreating.setValue(false);
        createResult.setValue(result);
    }

    /**
     * Handles the creation of a store and updates the user instance with the created store.
     *
     * <p>This method is called after the store is created in the database. It updates the user
     * instance with the created store and saves it to the user repository.
     *
     * @param userInstance the user instance to update
     * @param createdStore the created store
     */
    private void onStoreCreated(String uuid, User userInstance, Store createdStore) {
        if (createdStore != null) {
            userInstance.setStores(List.of(new User.Store(createdStore.getId())));
            userRepository.createUser(uuid, userInstance, this::setUserCreationResult);
        } else {
            Timber.e("Store creation failed");
            setUserCreationResult(false);
        }
    }

    /**
     * Handles successful authentication and updates the user and store information.
     *
     * <p>This method is called when the user is successfully authenticated with Firebase. It
     * updates the user ID and, if the user is an admin, saves the store information. If the user is
     * not an admin, it sets the user's store to the provided store.
     *
     * @param authResult the result of the authentication
     */
    @SuppressLint("CheckResult")
    private void onAuthSuccess(AuthResult authResult) {
        if (authResult == null) {
            Timber.e("AuthResult is null");
            setUserCreationResult(false);
            return;
        }

        User pendingUser = user.getValue();
        Store pendingStore = store.getValue();
        FirebaseUser firebaseUser = authResult.getUser();
        if (firebaseUser == null || pendingUser == null) {
            Timber.e("User or FirebaseUser is null");
            setUserCreationResult(false);
            return;
        }

        String uuid = firebaseUser.getUid();
        if (Boolean.TRUE.equals(getIsAdmin().getValue())) {
            if (pendingStore == null) {
                Timber.e("Store instance is null");
                setUserCreationResult(false);
                return;
            }

            storeRepository
                    .saveStore(pendingStore)
                    .filter(resource -> resource.status != Resource.Status.LOADING)
                    .firstElement()
                    .subscribe(
                            storeResource -> {
                                if (storeResource.status == Resource.Status.SUCCESS
                                        && storeResource.data != null) {
                                    Store savedStore = storeResource.data;
                                    Timber.d(
                                            "Store saved successfully: %s with ID: %s",
                                            savedStore.getName(), savedStore.getId());
                                    onStoreCreated(uuid, pendingUser, savedStore);
                                } else if (storeResource.status == Resource.Status.ERROR) {
                                    String errorMsg =
                                            storeResource.message != null
                                                    ? storeResource.message
                                                    : "Unknown error";
                                    Timber.e("Failed to save store: %s", errorMsg);
                                    setUserCreationResult(false);
                                }
                            },
                            error -> {
                                Timber.e(error, "Error saving store: %s", error.getMessage());
                                setUserCreationResult(false);
                            });
        } else {
            User.Store userStore = this.userStore.getValue();
            userStore.setRole(User.Store.STAFF);
            if (userStore != null) {
                pendingUser.setStores(List.of(userStore));
                userRepository.createUser(uuid, pendingUser, this::setUserCreationResult);
            } else {
                Timber.e("Staff store is null");
                setUserCreationResult(false);
            }
        }
    }
}
