package com.optlab.banhangso.features.main.authentication.viewmodel;

import static com.optlab.banhangso.internal.utilities.Constants.Auth.KEY_STORE_CODE;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.KEY_STORE_DESCRIPTION;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.KEY_STORE_NAME;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.KEY_USER_NAME;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.KEY_USER_PHONE;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableMap;
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
import com.optlab.banhangso.features.main.authentication.state.SignUpValidationState;
import com.optlab.banhangso.internal.utilities.Constants;
import com.optlab.banhangso.internal.validators.AuthValidator;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.Store;
import com.optlab.banhangso.repositories.interfaces.PreferenceRepository;
import com.optlab.banhangso.repositories.interfaces.StoreRepository;
import com.optlab.banhangso.repositories.interfaces.UserRepository;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import timber.log.Timber;

/** @noinspection LombokGetterMayBeUsed, LombokSetterMayBeUsed */
@HiltViewModel
public class RegistrationViewModel extends ViewModel {

    private final SavedStateHandle savedStateHandle;
    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final PreferenceRepository preferenceRepository;
    private final AuthValidator validator;
    private final ObservableArrayMap<String, String> inputFields = new ObservableArrayMap<>();

    private final MutableLiveData<SignUpValidationState> validationState =
            new MutableLiveData<>(new SignUpValidationState());
    private final MutableLiveData<Boolean> isCreating = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> createResult = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> signUpErrorResId = new MutableLiveData<>();

    private String email;
    private String password;

    // Map change callback for input fields
    private final ObservableMap.OnMapChangedCallback<ObservableMap<String, String>, String, String>
            inputFieldsCallback =
                    new ObservableMap.OnMapChangedCallback<>() {
                        @Override
                        public void onMapChanged(ObservableMap<String, String> sender, String key) {
                            switch (key) {
                                case KEY_USER_NAME -> validateContactName();
                                case KEY_USER_PHONE -> validateContactPhone();
                                case KEY_STORE_NAME -> validateStoreName();
                                case KEY_STORE_DESCRIPTION -> validateStoreDescription();
                                case KEY_STORE_CODE -> validateStoreCode();
                            }
                        }
                    };

    @Inject
    public RegistrationViewModel(
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

        Boolean isAdmin = savedStateHandle.get(Constants.Auth.KEY_IS_ADMIN);
        savedStateHandle.set(Constants.Auth.KEY_IS_ADMIN, isAdmin != null ? isAdmin : true);

        // Initialize ObservableMap with empty values
        inputFields.put(KEY_USER_NAME, "");
        inputFields.put(KEY_USER_PHONE, "");
        inputFields.put(KEY_STORE_NAME, "");
        inputFields.put(KEY_STORE_DESCRIPTION, "");
        inputFields.put(KEY_STORE_CODE, "");

        // Initialize map callback
        inputFields.addOnMapChangedCallback(inputFieldsCallback);
    }

    @Override
    protected void onCleared() {
        inputFields.clear();
        validationState.setValue(null);
        super.onCleared();
    }

    public LiveData<SignUpValidationState> getValidationState() {
        return validationState;
    }

    /**
     * Returns the observable map containing user and store input fields. This map is used for
     * two-way data binding in the UI.
     *
     * @return The ObservableMap instance containing input data
     */
    public ObservableArrayMap<String, String> getInputFields() {
        return inputFields;
    }

    public LiveData<Boolean> getIsAdmin() {
        return savedStateHandle.getLiveData(Constants.Auth.KEY_IS_ADMIN);
    }

    public void setIsAdmin(boolean isAdmin) {
        savedStateHandle.set(Constants.Auth.KEY_IS_ADMIN, isAdmin);
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
                    String name = inputFields.get(KEY_USER_NAME);
                    if (name != null) {
                        state.setContactNameError(validator.validateContactName(name));
                    }
                });
    }

    public void validateContactPhone() {
        updateValidationState(
                state -> {
                    String phone = inputFields.get(KEY_USER_PHONE);
                    if (phone != null) {
                        state.setContactPhoneError(validator.validatePhoneNumber(phone));
                    }
                });
    }

    public void validateStoreName() {
        updateValidationState(
                state -> {
                    String name = inputFields.get(KEY_STORE_NAME);
                    if (name != null) {
                        state.setStoreNameError(validator.validateStoreName(name));
                    }
                });
    }

    public void validateStoreDescription() {
        updateValidationState(
                state -> {
                    String description = inputFields.get(KEY_STORE_DESCRIPTION);
                    if (description != null) {
                        state.setStoreDescriptionError(
                                validator.validateStoreDescription(description));
                        //        userRepository
                        //                .saveUserRemote(uuid, pendingUser)
                        //                .observeOn(AndroidSchedulers.mainThread())
                        //                .subscribeOn(Schedulers.io())
                        //                .filter(Result::isLoaded)
                        //                .subscribe(
                        //                        this::handleUserCreationResult,
                        //                        e -> handleRepositoryError(e, "Error saving
                        // user"));
                    }
                });
    }

    public void validateStoreCode() {
        updateValidationState(
                state -> {
                    String code = inputFields.get(KEY_STORE_CODE);
                    if (code != null) {
                        state.setStoreCodeError(validator.validateStoreCode(code));
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
        // TODO: Handle store creation result
        //        if (createdStore != null) {
        //            userInstance.setStores(List.of(new User.Store(createdStore.getId())));
        //            userRepository
        //                    .saveUserRemote(uuid, userInstance)
        //                    .observeOn(AndroidSchedulers.mainThread())
        //                    .subscribeOn(Schedulers.io())
        //                    .filter(Result::isLoaded)
        //                    .subscribe(
        //                            resource -> {
        //                                if (resource.status == Result.Status.SUCCESS) {
        //                                    setUserCreationResult(true);
        //
        // preferenceRepository.setAuthenticatedUser(resource.data);
        //                                } else {
        //                                    Timber.e("User creation failed: %s",
        // resource.message);
        //                                    setUserCreationResult(false);
        //                                }
        //                            },
        //                            error -> {
        //                                Timber.e(error, "Error saving user: %s",
        // error.getMessage());
        //                                setUserCreationResult(false);
        //                            });
        //        } else {
        //            Timber.e("Store creation failed");
        //            setUserCreationResult(false);
        //        }
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

        FirebaseUser firebaseUser = authResult.getUser();
        if (firebaseUser == null) {
            Timber.e("FirebaseUser is null");
            setUserCreationResult(false);
            return;
        }

        // Create User object from the ObservableMap inputs
        User pendingUser = new User();
        pendingUser.setName(inputFields.get(KEY_USER_NAME));
        pendingUser.setPhone(inputFields.get(KEY_USER_PHONE));
        pendingUser.setEmail(firebaseUser.getEmail());

        String uuid = firebaseUser.getUid();
        Boolean isAdmin = getIsAdmin().getValue();

        if (Boolean.TRUE.equals(isAdmin)) {
            // Create Store object from the ObservableMap inputs for admin
            Store pendingStore = new Store();
            pendingStore.setName(inputFields.get(KEY_STORE_NAME));
            pendingStore.setDescription(inputFields.get(KEY_STORE_DESCRIPTION));

            processAdminRegistration(uuid, pendingUser, pendingStore);
        } else {
            // Create User.Store object with store code for staff
            //            User.Store userStore = new User.Store(inputFields.get(KEY_STORE_CODE));
            //            processStaffRegistration(uuid, pendingUser, userStore);
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
     * @param pendingStore the store being created
     */
    @SuppressLint("CheckResult")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void processAdminRegistration(String uuid, User pendingUser, Store pendingStore) {
        // TODO: Handle store creation result
        //        storeRepository
        //                .setStore(pendingStore)
        //                .filter(Result::isLoaded)
        //                .firstElement()
        //                .subscribe(
        //                        resource -> handleStoreCreationResult(uuid, pendingUser,
        // resource),
        //                        e -> handleRepositoryError(e, "Error saving store"));
    }

    /**
     * Handles the result of store creation.
     *
     * @param uuid the user ID
     * @param pendingUser the user being registered
     * @param result the result of the store creation
     */
    private void handleStoreCreationResult(String uuid, User pendingUser, Result<Store> result) {
        // TODO: Handle store creation result
        //        if (result.status == Result.Status.SUCCESS && result.data != null) {
        //            Store savedStore = result.data;
        //            Timber.d(
        //                    "Store saved successfully: %s with ID: %s",
        //                    savedStore.getName(), savedStore.getId());
        //            preferenceRepository.setSelectedStore(savedStore);
        //            onStoreCreated(uuid, pendingUser, savedStore);
        //        } else if (result.status == Result.Status.ERROR) {
        //            String errorMsg = result.message != null ? result.message : "Unknown error";
        //            Timber.e("Failed to save store: %s", errorMsg);
        //            setUserCreationResult(false);
        //        }
    }

    /**
     * Processes the registration for a staff user by linking them to an existing store.
     *
     * @param uuid the user ID
     * @param pendingUser the user being registered
     * @param userStore the store associated with the staff user
     */
    @SuppressLint("CheckResult")
    private void processStaffRegistration(String uuid, User pendingUser, User.Store userStore) {
        // TODO: Handle staff registration result
        //        userStore.setRole(User.Store.STAFF);
        //        pendingUser.setStores(List.of(userStore));
        //
        //        saveUserToRemoteToRepository(uuid, pendingUser);
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
        // TODO: Handle user creation result
        //        userRepository
        //                .saveUserRemote(uuid, pendingUser)
        //                .observeOn(AndroidSchedulers.mainThread())
        //                .subscribeOn(Schedulers.io())
        //                .filter(Result::isLoaded)
        //                .subscribe(
        //                        this::handleUserCreationResult,
        //                        e -> handleRepositoryError(e, "Error saving user"));
    }

    /**
     * Handles the result of user creation.
     *
     * @param userResult the result of the user creation
     */
    private void handleUserCreationResult(Result<User> userResult) {
        // TODO: Handle user creation result
        //        if (userResult.status == Result.Status.SUCCESS) {
        //            setUserCreationResult(true);
        //        } else {
        //            Timber.e("User creation failed: %s", userResult.message);
        //            setUserCreationResult(false);
        //        }
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
