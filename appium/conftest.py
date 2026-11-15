import os
from pathlib import Path

import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options

from pages.base_page import APP_PACKAGE

HERE = Path(__file__).resolve().parent
DEFAULT_APK = HERE.parent / "android" / "app" / "build" / "outputs" / "apk" / "debug" / "app-debug.apk"
APPIUM_SERVER = os.environ.get("APPIUM_SERVER", "http://127.0.0.1:4723")
SCREENSHOT_DIR = HERE / "reports" / "screenshots"


@pytest.fixture
def driver():
    apk = Path(os.environ.get("APP_PATH", DEFAULT_APK))
    if not apk.exists():
        pytest.exit(f"APK not found at {apk}. Build it first: cd android, then gradlew assembleDebug", returncode=1)

    options = UiAutomator2Options()
    options.app = str(apk)
    options.app_package = APP_PACKAGE
    options.app_activity = ".LoginActivity"
    # Clears app data between sessions, so every test starts logged out with no tasks.
    options.no_reset = False
    options.new_command_timeout = 120
    # Optional: target one device when several are connected (see `adb devices`).
    if os.environ.get("DEVICE_UDID"):
        options.udid = os.environ["DEVICE_UDID"]

    drv = webdriver.Remote(APPIUM_SERVER, options=options)
    # Explicit waits only (see pages/base_page.py); implicit waits hide timing bugs.
    drv.implicitly_wait(0)
    yield drv
    drv.quit()


@pytest.hookimpl(hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Save a screenshot whenever a test fails, so CI failures can be debugged."""
    outcome = yield
    report = outcome.get_result()
    if report.when == "call" and report.failed:
        drv = item.funcargs.get("driver")
        if drv is not None:
            SCREENSHOT_DIR.mkdir(parents=True, exist_ok=True)
            drv.get_screenshot_as_file(str(SCREENSHOT_DIR / f"{item.name}.png"))
