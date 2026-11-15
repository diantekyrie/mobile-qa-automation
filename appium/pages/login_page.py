from pages.base_page import BasePage, by_id
from pages.task_list_page import TaskListPage

VALID_USERNAME = "tester"
VALID_PASSWORD = "password123"


class LoginPage(BasePage):
    USERNAME = by_id("username")
    PASSWORD = by_id("password")
    LOGIN_BUTTON = by_id("login_button")
    ERROR = by_id("login_error")

    def submit(self, username, password):
        self.type(self.USERNAME, username)
        self.type(self.PASSWORD, password)
        self.hide_keyboard()
        self.tap(self.LOGIN_BUTTON)

    def log_in(self, username=VALID_USERNAME, password=VALID_PASSWORD):
        self.submit(username, password)
        return TaskListPage(self.driver)

    def error_message(self):
        return self.text_of(self.ERROR)

    def is_displayed(self):
        return self.is_visible(self.LOGIN_BUTTON)
