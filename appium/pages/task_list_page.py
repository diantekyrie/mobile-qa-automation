from appium.webdriver.common.appiumby import AppiumBy

from pages.base_page import BasePage, by_id


class TaskListPage(BasePage):
    WELCOME = by_id("welcome_text")
    NEW_TASK = by_id("new_task_input")
    ADD_BUTTON = by_id("add_task_button")
    SUMMARY = by_id("task_summary")
    SYNC_BUTTON = by_id("sync_button")
    SYNC_STATUS = by_id("sync_status")
    LOGOUT_BUTTON = by_id("logout_button")
    # Rows use Android's built-in list item layout, so their id is android:id/text1.
    TASK_ROWS = (AppiumBy.ID, "android:id/text1")

    @staticmethod
    def task(name):
        """Locator for one task row, using a UiAutomator selector."""
        return (
            AppiumBy.ANDROID_UIAUTOMATOR,
            f'new UiSelector().resourceId("android:id/text1").text("{name}")',
        )

    def welcome_text(self):
        return self.text_of(self.WELCOME)

    def add_task(self, name):
        self.type(self.NEW_TASK, name)
        self.hide_keyboard()
        self.tap(self.ADD_BUTTON)

    def task_names(self):
        return [row.text for row in self.driver.find_elements(*self.TASK_ROWS)]

    def toggle_task(self, name):
        self.tap(self.task(name))

    def is_done(self, name):
        return self.find(self.task(name)).get_attribute("checked") == "true"

    def summary(self):
        return self.text_of(self.SUMMARY)

    def sync(self, timeout=10):
        """Tap Sync and wait for it to finish; returns the final status text."""
        self.tap(self.SYNC_BUTTON)
        return self.wait_for_text(self.SYNC_STATUS, "Synced", timeout)

    def log_out(self):
        self.tap(self.LOGOUT_BUTTON)
