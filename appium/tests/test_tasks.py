import pytest

from pages.login_page import LoginPage


@pytest.fixture
def tasks(driver):
    return LoginPage(driver).log_in()


@pytest.mark.smoke
def test_added_task_appears_in_list(tasks):
    tasks.add_task("Write test plan")

    assert tasks.task_names() == ["Write test plan"]
    assert tasks.summary() == "Tasks: 1 | Done: 0"


def test_empty_task_is_not_added(tasks):
    tasks.add_task("")

    assert tasks.task_names() == []
    assert tasks.summary() == "Tasks: 0 | Done: 0"


def test_tapping_task_marks_it_done(tasks):
    tasks.add_task("File bug report")

    tasks.toggle_task("File bug report")

    assert tasks.is_done("File bug report")
    assert tasks.summary() == "Tasks: 1 | Done: 1"


def test_sync_reports_task_count(tasks):
    tasks.add_task("Run regression")
    tasks.add_task("Triage crashes")

    # Sync takes about 2 seconds; sync() uses an explicit wait, never time.sleep().
    assert tasks.sync() == "Synced 2 tasks"


def test_log_out_returns_to_login(driver, tasks):
    tasks.log_out()

    assert LoginPage(driver).is_displayed()
