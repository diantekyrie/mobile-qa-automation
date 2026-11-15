import pytest

from pages.login_page import VALID_PASSWORD, VALID_USERNAME, LoginPage


@pytest.mark.smoke
def test_valid_login_opens_task_list(driver):
    tasks = LoginPage(driver).log_in()

    assert tasks.welcome_text() == "Welcome, tester"


@pytest.mark.parametrize(
    ("username", "password", "expected_error"),
    [
        ("", "", "Username and password are required"),
        (VALID_USERNAME, "", "Username and password are required"),
        (VALID_USERNAME, "wrong-password", "Invalid username or password"),
        ("nobody", VALID_PASSWORD, "Invalid username or password"),
    ],
    ids=["both-empty", "no-password", "wrong-password", "unknown-user"],
)
def test_invalid_login_shows_error(driver, username, password, expected_error):
    login = LoginPage(driver)

    login.submit(username, password)

    assert login.error_message() == expected_error
    assert login.is_displayed()
