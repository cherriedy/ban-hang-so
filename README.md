## Ban Hang So

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Java](https://img.shields.io/badge/Java-17-orange.svg?logo=java)](https://www.java.com)
[![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%26%20Auth-orange?logo=firebase)](https://firebase.google.com/)
[![Hilt](https://img.shields.io/badge/Hilt-Dependency%20Injection-blue?logo=dagger)](https://dagger.dev/hilt/)
[![RxJava3](https://img.shields.io/badge/RxJava-3-purple.svg)](https://github.com/ReactiveX/RxJava)
[![MIT License](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This repository contains the source code for **Ban Hang So**, an Android application engineered to serve as a digital grocery. The application is architected using the Model-View-ViewModel (MVVM) pattern and is developed with a modern, reactive technology stack.

## Project Overview

"Ban Hang So" is designed to provide a comprehensive mobile e-commerce platform. Its architecture is built for scalability and maintainability, facilitating a clear separation of concerns between the data, business logic, and UI layers. The project heavily utilizes reactive programming with RxJava3 and modern Android Jetpack libraries to create a responsive and robust user experience.

## Technical Stack and Architecture

The project is built upon a foundation of modern, industry-standard tools and libraries.

*   **Architectural Pattern:**
    *   **MVVM (Model-View-ViewModel):** The core architectural pattern, separating UI logic from business logic.
    *   **Repository Pattern:** Used to abstract data sources (network, local database).

*   **Android Jetpack:**
    *   **UI Layer:** `Data Binding`, `ViewModel`, `LiveData`, `Navigation Component` (with Safe Args), `Paging 3`, `SplashScreen`.
    *   **Data Layer:** `DataStore` (with RxJava3 support) for persistent key-value storage.
    *   **Core:** `AppCompat`, `Core-KTX`, `Activity-KTX`.

*   **Asynchronous & Reactive Programming:**
    *   **RxJava3 & RxAndroid:** Extensively used for managing asynchronous operations, data streams, and composing complex logic.
    *   **AutoDispose:** For automatically handling RxJava subscription lifecycles, preventing memory leaks.

*   **Dependency Injection:**
    *   **Hilt:** For managing dependencies throughout the application, integrated with Jetpack's Navigation Component. KSP is used for faster annotation processing.

*   **Networking:**
    *   **Retrofit 2:** For type-safe HTTP requests to RESTful APIs.
    *   **OkHttp 3:** As the underlying HTTP client, with a logging interceptor for debugging.
    *   **Gson:** For JSON serialization and deserialization.

*   **Backend & Cloud Services (Firebase):**
    *   **Firebase Authentication:** For user sign-in and identity management.
    *   **Cloud Firestore:** As the primary NoSQL cloud database for real-time data synchronization.
    *   **Firebase Crashlytics:** For real-time crash reporting and analysis.
    *   **Google Identity Services:** For authentication using Google accounts.

*   **User Interface & Utils:**
    *   **Material Design Components:** For a consistent and modern UI.
    *   **Glide:** For efficient image loading and caching.
    *   **Lottie:** For rendering rich animations.
    *   **MPAndroidChart:** For displaying charts and graphs.
    *   **Shimmer:** For creating loading animations.
    *   **ZXing (Embedded):** For QR code scanning functionality.
    *   **Timber:** For intelligent and extensible logging.

*   **Build & Tooling:**
    *   **Gradle:** For dependency management and build automation.
    *   **Protocol Buffers (Protobuf):** Used with DataStore for efficient data serialization.
    *   **Lombok:** To reduce boilerplate code in Java classes.

## Getting Started

To compile and run the project, follow these steps.

### Prerequisites

*   Android Studio Iguana | 2023.2.1 or later
*   Java Development Kit (JDK) 17
*   Android SDK Platform 35

### Installation

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/cherriedy/ban-hang-so.git
    ```
2.  **Open in Android Studio:**
    *   Launch Android Studio.
    *   Select `File > Open` and navigate to the cloned project directory.
3.  **Sync and Build:**
    *   Allow Android Studio to download dependencies and sync the project with its Gradle configuration.
    *   Execute the `Run 'app'` command to build and deploy the application to an Android Emulator or a physical device.

## Contributing

Contributions are welcomed and greatly appreciated. Please follow these steps to contribute:

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/NewFeature`)
3.  Commit your Changes (`git commit -m 'Add: NewFeature'`)
4.  Push to the Branch (`git push origin feature/NewFeature`)
5.  Open a Pull Request

## License

This project is distributed under the MIT License. See the `LICENSE.txt` file for more details.
