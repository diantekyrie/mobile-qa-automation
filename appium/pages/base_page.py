from appium.webdriver.common.appiumby import AppiumBy
from selenium.common.exceptions import TimeoutException, WebDriverException
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

APP_PACKAGE = "com.qapractice.app"


def by_id(name):
    """Locator for a view by its Android resource id, e.g. by_id("login_button")."""
    return (AppiumBy.ID, f"{APP_PACKAGE}:id/{name}")


class BasePage:
    def __init__(self, driver, timeout=10):
        self.driver = driver
        self.wait = WebDriverWait(driver, timeout)

    def find(self, locator):
        return self.wait.until(EC.visibility_of_element_located(locator))

    def tap(self, locator):
        self.wait.until(EC.element_to_be_clickable(locator)).click()

    def type(self, locator, text):
        field = self.find(locator)
        field.clear()
        if text:
            field.send_keys(text)

    def text_of(self, locator):
        return self.find(locator).text

    def is_visible(self, locator, timeout=2):
        try:
            WebDriverWait(self.driver, timeout).until(EC.visibility_of_element_located(locator))
            return True
        except TimeoutException:
            return False

    def wait_for_text(self, locator, expected, timeout=10):
        WebDriverWait(self.driver, timeout).until(EC.text_to_be_present_in_element(locator, expected))
        return self.text_of(locator)

    def hide_keyboard(self):
        try:
            self.driver.hide_keyboard()
        except WebDriverException:
            pass  # Keyboard was not showing.
