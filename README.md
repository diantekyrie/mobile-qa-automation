# Mobile QA Automation Practice

A small Android app with three layers of automated tests around it:

| Folder | What | Language |
|---|---|---|
| `android/` | The practice app, plus **Espresso** tests in `app/src/androidTest` | Kotlin |
| `appium/` | **Appium + pytest** end-to-end tests, using the page object pattern | Python |
| `ios/` | Plan for **XCUITest** (needs a Mac) | Swift |
| `.github/workflows/` | CI that runs Espresso and Appium on an Android emulator | YAML |

## The app under test
**QA Practice** has two screens:
- **Login**: username and password. The practice login is `tester` / `password123`. Empty fields and wrong credentials show different errors.
- **Task list**: add a task, tap it to mark it done, see the `Tasks: N | Done: N` summary, tap **Sync** (takes about 2 seconds), and log out.

The Sync delay is there on purpose: waiting correctly for async work is the most common reason mobile tests are flaky.

A real bug from building this: on Android 15+ apps draw edge-to-edge, so the action bar covered the Add button and Espresso's taps landed on the bar instead. `Insets.kt` pads each screen for the system bars and keyboard. It's a good one to remember for interviews.

## Run it locally (Windows)

### 1. Start an emulator
Open Android Studio → Device Manager → start a device (for example `Pixel_9_API_36`). Check it's connected:
```bash
adb devices
```
If your phone is plugged in too, tell the tools to use only the emulator, or they'll install onto your phone as well. In PowerShell:
```powershell
$env:ANDROID_SERIAL = "emulator-5554"   # Espresso (Gradle)
$env:DEVICE_UDID = "emulator-5554"      # Appium (conftest.py)
```
For steadier Espresso runs, turn off animations in the emulator's Developer options (window, transition and animator scales).

### 2. Build the app
```bash
cd android
./gradlew assembleDebug
```
If Gradle complains about the Java version, point it at a full JDK first. In PowerShell:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
```

### 3. Espresso
```bash
cd android
./gradlew connectedDebugAndroidTest
```
The HTML report is in `android/app/build/reports/androidTests/connected/`.

### 4. Appium
In one terminal, start the server:
```bash
appium
```
In another:
```bash
cd appium
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
python -m pytest
```
Useful options:
- `python -m pytest -m smoke` runs only the smoke tests.
- `python -m pytest -k sync` runs tests with "sync" in the name.
- Screenshots of failed tests go to `appium/reports/screenshots/`.
- Use **Appium Inspector** to explore element ids while writing new tests.

## Practice plan
**Weeks 1–2: Appium**
- [ ] Get the suite passing locally, then read every page object.
- [ ] Add a test: tapping a done task again marks it not done.
- [ ] Add a test: duplicate task names are both kept.
- [ ] Add a test that rotates the device (`driver.orientation = "LANDSCAPE"`) and checks tasks survive. (They don't yet. Finding that bug is the point.)
- [ ] Add a test that backgrounds the app (`driver.background_app(3)`) mid-sync.

**Weeks 3–4: Espresso**
- [ ] Run the tests from Android Studio (green arrow next to each test).
- [ ] Remove the `IdlingRegistry` lines in `TaskListTest` and watch the sync test fail; put them back.
- [ ] Add a Delete button (long-press a task), then test it in both Espresso and Appium.
- [ ] Swap `ListView` for `RecyclerView` and update the tests using `RecyclerViewActions`.

**After that: XCUITest.** See [`ios/README.md`](ios/README.md).

## CI
`.github/workflows/mobile-tests.yml` runs on every push to `main` and every pull request:
- **espresso**: boots an API 34 emulator and runs `connectedDebugAndroidTest`.
- **appium**: builds the APK, starts an Appium server, boots an emulator and runs pytest.

Reports, failure screenshots and the Appium server log are uploaded as build artifacts.
