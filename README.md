# POPPOP - Android Dating App

POPPOP is a fun and engaging dating app designed to connect people based on shared interests and preferences. This repository contains the source code for the Android application.

## Features

- **Profile Creation:** Users can create detailed profiles with photos, personal information, interests, and preferences.
- **Swipe & Match:** The core feature of the app, allowing users to swipe left (reject) or right (like) other profiles. A match occurs when two users swipe right on each other.
- **Chatting:** Once a match is made, users can engage in real-time chat within the app.
- **Payment Integration:** Users can make payments securely within the app using Stripe.

## Technologies Used

- **Kotlin:** Primary programming language for Android development.
- **Android SDK:** For building and deploying the Android application.
- **AndroidX Libraries:** For modern Android development, including:
  - `androidx.appcompat` for backward compatibility.
  - `androidx.lifecycle` for managing UI-related data in a lifecycle-conscious way.
  - `androidx.recyclerview` for displaying lists of data.
  - `androidx.constraintlayout` for flexible UI designs.
- **Firebase:** For user authentication, real-time database, cloud storage, and messaging.
  - `firebase-auth` for user authentication.
  - `firebase-firestore` for cloud database.
  - `firebase-messaging` for push notifications.
- **Google Play Services:** For location services, maps, and authentication.
- **Networking Libraries:**
  - **OkHttp:** For making network requests.
  - **Volley:** For handling asynchronous requests.
- **Image Loading:**
  - **Glide:** For loading and caching images efficiently.
- **UI Components:**
  - **Material Design Components:** For implementing Material Design in the app.
  - **CardStackView:** For swipeable card UI.
- **Payment Integration:**
  - **Stripe:** For handling payments.
- **Testing Libraries:**
  - **JUnit:** For unit testing.
  - **Espresso:** For UI testing.

## Setup and Installation

1. **Clone the repository:**

   ```bash
   git clone https://github.com/PopPop-DatingApp/PopPop.git
   ```

2. **Open the project in Android Studio:**

   - Launch Android Studio.
   - Select "Open an existing Android Studio project."
   - Navigate to the cloned repository and select the root directory.

3. **Configure the project:**

   - **(If using Firebase/Backend):**
     - Set up a Firebase project in the [Firebase Console](https://console.firebase.google.com/).
     - Download the `google-services.json` file and place it in the `app/` directory of your project.
   - **(If using API Keys):**
     - Add any necessary API keys to the `local.properties` file or appropriate configuration files.

4. **Sync the project:**

   - Click on "Sync Now" in the notification bar that appears in Android Studio to sync the project with the Gradle files.

5. **Build and run the app:**

   - Connect an Android device or use an emulator.
   - Click the "Run" button in Android Studio or select "Run" from the menu.

6. **Proguard Configuration (Optional):**

   - If you plan to use Proguard for code obfuscation, ensure that your `proguard-rules.pro` file is configured according to your needs.

7. **Gradle Wrapper:**
   - The project includes a Gradle wrapper. You can run Gradle commands using the `gradlew` script (or `gradlew.bat` on Windows) in the terminal.
