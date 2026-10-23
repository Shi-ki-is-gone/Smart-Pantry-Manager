# Smart Pantry Manager

Smart Pantry Manager is an Android app for keeping track of pantry ingredients, quantities, and expiry dates. It suggests recipes based on what is available, highlights ingredients that are close to expiring, and updates pantry quantities when you cook a recipe.
Written Report can be found in a separate branch
## Database

The app uses **SQLite**, Android's built-in relational database, through `SQLiteOpenHelper`. Pantry items, recipes, and recipe ingredients are stored on the device, so the app works offline without a server or separate database installation. SQLite transactions keep pantry updates consistent when ingredients are used, and indexes help with common name and expiry lookups.

## Requirements

- Android Studio with Android SDK Platform 34 installed
- JDK 17 (recommended for Android Gradle Plugin 8.5.2)
- An Android emulator or device running Android 7.0 (API 24) or later

## Setup and run

1. Clone or download this repository.
2. In Android Studio, choose **Open** and select the project root directory containing `settings.gradle`.
3. Allow Gradle sync to complete. If prompted, install Android SDK Platform 34 using **Tools > SDK Manager**.
4. Create or select an emulator in **Tools > Device Manager**, or connect an Android device with USB debugging enabled.
5. Select the `app` run configuration and your device, then click **Run**. Android Studio will build and install the app.

### Build from the command line

From the project root, run the Gradle wrapper:

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is created at `app\build\outputs\apk\debug\app-debug.apk`. To install it on a connected device or running emulator:

```powershell
.\gradlew.bat installDebug
```

On macOS or Linux, use `./gradlew` instead of `.\gradlew.bat`.
