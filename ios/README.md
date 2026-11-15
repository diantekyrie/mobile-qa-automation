# iOS / XCUITest (coming next)

XCUITest only runs on macOS with Xcode, so this folder is a plan until there is a Mac to work on.

## Getting a Mac
- **Own one:** a used Apple Silicon (M1 or newer) Mac mini is enough for Xcode and the iOS Simulator.
- **Rent one:** a cloud Mac service, billed hourly or monthly.
- **CI only:** GitHub Actions has macOS runners (free for public repos). Tests can run there, but writing and debugging them is much easier with Xcode open locally.

## Plan
1. In Xcode, create a SwiftUI app named `QAPractice` with **Include Tests** checked.
2. Rebuild the same two screens as the Android app: a login screen (`tester` / `password123`) and a task list with Add, tap-to-complete, and a 2-second Sync.
3. Give every control an `accessibilityIdentifier` matching the Android ids (`username`, `password`, `login_button`, `login_error`, `new_task_input`, `add_task_button`, `task_summary`, `sync_button`, `sync_status`, `logout_button`).
4. Port the Espresso tests to XCUITest in Swift. The Sync check is a good place to learn `waitForExistence(timeout:)`.
5. Point the existing Appium suite at the iOS app with the `xcuitest` driver (already installed on this PC), so one set of Python tests covers both platforms.
6. Add a `macos-latest` job to `.github/workflows/mobile-tests.yml` that runs `xcodebuild test`.

## Learn
- Apple's documentation: "Testing in Xcode" and the `XCTest` / `XCUIApplication` reference.
- Run the test target with **Cmd+U**; use **Record UI Test** to see the queries Xcode generates, then clean them up by hand.
