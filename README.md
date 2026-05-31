# BookWorm

BookWorm is a Kotlin + Jetpack Compose Android app for tracking books, collecting vocabulary, training definitions, and earning XP.

## How to run in Android Studio

1. Install the latest stable Android Studio with Android SDK Platform 35.
2. Open the repository root folder, not the `app` folder. The root is the folder that contains `settings.gradle.kts`.
3. Wait for Gradle sync to finish. Android Studio must have internet access to download the Android Gradle Plugin and dependencies from Google Maven, Maven Central, and Gradle Plugin Portal.
4. If prompted, use JDK 17 for Gradle: **Settings/Preferences → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK → JDK 17**.
5. Select the `app` run configuration.
6. Start an emulator or connect a physical Android device with USB debugging enabled.
7. Click **Run ▶**. The debug build installs the `com.example.bookworm` application.

## Project facts

- Minimum SDK: 24.
- Compile SDK: 35.
- App module: `:app`.
- Application ID: `com.example.bookworm`.
- Main activity: `com.example.bookworm.MainActivity`.
