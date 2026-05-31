# BookWorm

BookWorm is a Kotlin + Jetpack Compose Android app for tracking books, collecting vocabulary, training definitions, and earning XP.

## How to run in Android Studio

1. Install the latest stable Android Studio with Android SDK Platform 35.
2. Open the repository root folder, not the `app` folder. The root is the folder that contains `settings.gradle.kts`, `build.gradle.kts`, and the `app/` directory.
3. Wait for Gradle sync to finish. Android Studio must have internet access to download the Android Gradle Plugin and dependencies from Google Maven, Maven Central, and Gradle Plugin Portal.
4. If prompted, use JDK 17 for Gradle: **Settings/Preferences → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK → JDK 17**.
5. Select the `app` run configuration.
6. Start an emulator or connect a physical Android device with USB debugging enabled.
7. Click **Run ▶**. The debug build installs the `com.example.bookworm` application.

## If Android Studio says "Code insight unavailable (related Gradle project not linked)"

That message means Android Studio opened the files as plain text instead of linking/importing the Gradle project. The **Run** button stays gray until Gradle sync creates the Android app run configuration.

1. Click the blue **Link Gradle project** action shown by Android Studio.
2. When Android Studio asks for the Gradle project, select this repository root folder: the folder with `settings.gradle.kts`, `build.gradle.kts`, and the `app/` directory.
3. If Android Studio asks for Gradle, choose the Gradle version bundled with Android Studio or any installed Gradle compatible with Android Gradle Plugin 8.7.3. The binary Gradle wrapper JAR is intentionally not committed so GitHub/web PR flows that reject binary files can create the PR cleanly.
4. Confirm Android Studio is not in **Offline mode**: **Settings/Preferences → Build, Execution, Deployment → Gradle** and disable offline work if it is enabled.
5. Run **File → Sync Project with Gradle Files**.
6. After sync succeeds, choose the `app` configuration and an emulator/device. The **Run ▶** button should become enabled.

If sync still fails, open **Build** or **Sync** output and check the first error. The most common causes are missing SDK Platform 35, Gradle JDK not set to 17, corporate proxy/VPN blocking repositories, or opening the `app/` subfolder instead of the repository root.

## Why there is no Gradle wrapper JAR

Some GitHub/web PR creation flows reject binary files and show **"Binary files are not supported"**. A standard Gradle wrapper includes `gradle/wrapper/gradle-wrapper.jar`, which is a binary file. To keep the PR text-only and easy to create in those flows, this repository does not commit the wrapper JAR. Android Studio can still link the project using its bundled/installed Gradle.

If you are working in a normal Git workflow and want a wrapper locally, you can generate it with:

```bash
gradle wrapper --gradle-version 8.9 --distribution-type bin
```

If your PR tool rejects binaries, do not commit `gradle/wrapper/gradle-wrapper.jar`.

## How a friend can install the app

For quick testing, build a debug APK in Android Studio and send that APK to your friend:

1. In Android Studio, open this project and wait for Gradle sync.
2. Select **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
3. When the build finishes, click **locate** in the Android Studio notification or open `app/build/outputs/apk/debug/app-debug.apk`.
4. Send `app-debug.apk` to your friend by a trusted channel.
5. On your friend's Android phone, enable installing apps from that source if Android asks, then open the APK to install it.

For public distribution, do not share the debug APK. Create a signed release build with **Build → Generate Signed Bundle / APK**, then upload an Android App Bundle (`.aab`) to Google Play or share a signed release APK through your chosen distribution channel.

## Project facts

- Minimum SDK: 24.
- Compile SDK: 35.
- App module: `:app`.
- Application ID: `com.example.bookworm`.
- Main activity: `com.example.bookworm.MainActivity`.
