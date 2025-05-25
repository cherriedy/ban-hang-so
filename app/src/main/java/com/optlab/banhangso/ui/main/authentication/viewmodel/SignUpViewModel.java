package com.optlab.banhangso.ui.main.authentication.viewmodel;

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
import com.optlab.banhangso.domain.model.Store;
import com.optlab.banhangso.domain.model.User;
import com.optlab.banhangso.domain.repository.PreferenceRepository;
import com.optlab.banhangso.domain.repository.StoreRepository;
import com.optlab.banhangso.domain.repository.UserRepository;
import com.optlab.banhangso.domain.util.Resource;
import com.optlab.banhangso.ui.main.authentication.state.SignUpValidationState;
import com.optlab.banhangso.util.validator.AuthValidator;

import dagger.hilt.android.lifecycle.HiltViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

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
    private final PreferenceRepository preferenceRepository;
    private final AuthValidator validator;
    private final MutableLiveData<User> user = new MutableLiveData<>(new User());
    private final MutableLiveData<Store> store = new MutableLiveData<>(new Store());
    private final MutableLiveData<SignUpValidationState> validationState =
            new MutableLiveData<>(new SignUpValidationState());
    private final MutableLiveData<User.Store> nestedStore =
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
            PreferenceRepository preferenceRepository,
            AuthValidator validator) {
        this.savedStateHandle = savedStateHandle;
        this.firebaseAuth = firebaseAuth;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.preferenceRepository = preferenceRepository;
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
        User.Store staffStoreInstance = nestedStore.getValue();
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

    public LiveData<User.Store> getNestedStore() {
        return nestedStore;
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
                    User.Store store = this.nestedStore.getValue();
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
        User.Store storeInstance = nestedStore.getValue();
        if (storeInstance != null && staffStorePropertyChangedCallback == null) {
            staffStorePropertyChangedCallback =
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            if (propertyId == BR.id) {
                                validateStoreCode();
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
    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void onStoreCreated(String uuid, User userInstance, Store createdStore) {
        if (createdStore != null) {
            userInstance.setStores(List.of(new User.Store(createdStore.getId())));
            userRepository
                    .saveUserRemote(uuid, userInstance)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .filter(Resource::isLoaded)
                    .subscribe(
                            resource -> {
                                if (resource.status == Resource.Status.SUCCESS) {
                                    setUserCreationResult(true);
                                    preferenceRepository.setAuthenticatedUser(resource.data);
                                } else {
                                    Timber.e("User creation failed: %s", resource.message);
                                    setUserCreationResult(false);
                                }
                            },
                            error -> {
                                Timber.e(error, "Error saving user: %s", error.getMessage());
                                setUserCreationResult(false);
                            });
        } else {
            Timber.e("Store creation failed");
            setUserCreationResult(false);
        }
    }

    /**
     * Handles successful authentication and updates the user and store information.
     *
     * <p>This method is called when the user is successfully authenticated with Firebase. It
     * processes the user registration based on their role (admin or staff).
     *
     * @param authResult the result of the authentication
     */
    @SuppressLint("CheckResult")
    private void onAuthSuccess(AuthResult authResult) {
        if (!validateAuthResult(authResult)) {
            return;
        }

        User pendingUser = user.getValue();
        FirebaseUser firebaseUser = authResult.getUser();
        if (firebaseUser == null || pendingUser == null) {
            Timber.e("User or FirebaseUser is null");
            setUserCreationResult(false);
            return;
        }

        String uuid = firebaseUser.getUid();
        Boolean isAdmin = getIsAdmin().getValue();

        pendingUser.setEmail(firebaseUser.getEmail());

        if (Boolean.TRUE.equals(isAdmin)) {
            processAdminRegistration(uuid, pendingUser);
        } else {
            processStaffRegistration(uuid, pendingUser);
        }
    }

    /**
     * Validates the authentication result.
     *
     * @param authResult the result of the authentication
     * @return true if the authentication result is valid, false otherwise
     */
    private boolean validateAuthResult(AuthResult authResult) {
        if (authResult == null) {
            Timber.e("AuthResult is null");
            setUserCreationResult(false);
            return false;
        }
        return true;
    }

    /**
     * Processes the registration for an admin user by creating a store and linking it to the user.
     *
     * @param uuid the user ID
     * @param pendingUser the user being registered
     */
    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void processAdminRegistration(String uuid, User pendingUser) {
        Store pendingStore = store.getValue();
        if (pendingStore == null) {
            Timber.e("Store instance is null");
            setUserCreationResult(false);
            return;
        }

        storeRepository
                .saveStore(pendingStore)
                .filter(Resource::isLoaded)
                .firstElement()
                .subscribe(
                        resource -> handleStoreCreationResult(uuid, pendingUser, resource),
                        error -> handleRepositoryError(error, "Error saving store"));
    }

    /**
     * Handles the result of store creation.
     *
     * @param uuid the user ID
     * @param pendingUser the user being registered
     * @param resource the result of the store creation
     */
    private void handleStoreCreationResult(
            String uuid, User pendingUser, Resource<Store> resource) {
        if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
            Store savedStore = resource.data;
            Timber.d(
                    "Store saved successfully: %s with ID: %s",
                    savedStore.getName(), savedStore.getId());
            onStoreCreated(uuid, pendingUser, savedStore);
        } else if (resource.status == Resource.Status.ERROR) {
            String errorMsg = resource.message != null ? resource.message : "Unknown error";
            Timber.e("Failed to save store: %s", errorMsg);
            setUserCreationResult(false);
        }
    }

    /**
     * Processes the registration for a staff user by linking them to an existing store.
     *
     * @param uuid the user ID
     * @param pendingUser the user being registered
     */
    @SuppressLint("CheckResult")
    private void processStaffRegistration(String uuid, User pendingUser) {
        User.Store userStoreInstance = nestedStore.getValue();
        if (userStoreInstance == null) {
            Timber.e("Staff store is null");
            setUserCreationResult(false);
            return;
        }

        userStoreInstance.setRole(User.Store.STAFF);
        pendingUser.setStores(List.of(userStoreInstance));

        saveUserToRemoteToRepository(uuid, pendingUser);
    }

    /**
     * Saves the user to the repository.
     *
     * @param uuid the user ID
     * @param pendingUser the user to save
     */
    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveUserToRemoteToRepository(String uuid, User pendingUser) {
        userRepository
                .saveUserRemote(uuid, pendingUser)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .filter(Resource::isLoaded)
                .subscribe(
                        this::handleUserCreationResult,
                        error -> handleRepositoryError(error, "Error saving user"));
    }

    /**
     * Handles the result of user creation.
     *
     * @param userResource the result of the user creation
     */
    private void handleUserCreationResult(Resource<User> userResource) {
        if (userResource.status == Resource.Status.SUCCESS) {
            setUserCreationResult(true);
        } else {
            Timber.e("User creation failed: %s", userResource.message);
            setUserCreationResult(false);
        }
    }

    /**
     * Handles repository errors.
     *
     * @param error the error that occurred
     * @param errorMessage the error message to log
     */
    private void handleRepositoryError(Throwable error, String errorMessage) {
        Timber.e(error, "%s: %s", errorMessage, error.getMessage());
        setUserCreationResult(false);
    }
}
