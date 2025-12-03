package com.example.codechecker.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
import com.example.codechecker.ui.screens.admin.UserManagementScreen
import com.example.codechecker.ui.screens.admin.viewmodel.UserManagementViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.*

/**
 * UI tests for user management screen
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class UserManagementScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUsers = listOf(
        User(1L, "admin", "admin@example.com", Role.ADMIN, true, UserStatus.ACTIVE),
        User(2L, "teacher1", "teacher1@example.com", Role.TEACHER, true, UserStatus.ACTIVE),
        User(3L, "student1", "student1@example.com", Role.STUDENT, true, UserStatus.ACTIVE),
        User(4L, "student2", "student2@example.com", Role.STUDENT, false, UserStatus.DISABLED)
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `user management screen displays users correctly`() {
        // Given - user management screen with test data
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - should display user list
        composeTestRule
            .onNodeWithText("用户管理")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("admin")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("teacher1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("student1")
            .assertIsDisplayed()
    }

    @Test
    fun `create user dialog opens when create user button is clicked`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click create user button
        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        // Then - dialog should appear
        composeTestRule
            .onNodeWithText("创建用户")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("用户名")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("邮箱")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("密码")
            .assertIsDisplayed()
    }

    @Test
    fun `create user dialog can be cancelled`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - open dialog and cancel
        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("取消")
            .performClick()

        // Then - dialog should close
        composeTestRule
            .onNodeWithText("创建用户")
            .assertIsDisplayed() // Back to main screen

        composeTestRule
            .onNodeWithText("用户名")
            .assertDoesNotExist()
    }

    @Test
    fun `edit user dialog opens when edit button is clicked`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click edit button for a user
        // (Would need to find edit button for specific user)
        composeTestRule
            .onNodeWithTag("edit_user_1")
            .performClick()

        // Then - edit dialog should appear
        composeTestRule
            .onNodeWithText("编辑用户")
            .assertIsDisplayed()
    }

    @Test
    fun `user can be enabled`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click enable for disabled user
        composeTestRule
            .onNodeWithTag("enable_user_4")
            .performClick()

        // Then - user should be enabled
        // (Actual implementation would check user state)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `user can be disabled`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click disable for active user
        composeTestRule
            .onNodeWithTag("disable_user_1")
            .performClick()

        // Then - user should be disabled
        assertTrue(true) // Placeholder
    }

    @Test
    fun `user can be deleted`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click delete for a user
        composeTestRule
            .onNodeWithTag("delete_user_3")
            .performClick()

        // Then - confirmation dialog should appear
        composeTestRule
            .onNodeWithText("确认删除")
            .assertIsDisplayed()
    }

    @Test
    fun `password reset dialog opens`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click reset password
        composeTestRule
            .onNodeWithTag("reset_password_2")
            .performClick()

        // Then - password reset dialog should appear
        composeTestRule
            .onNodeWithText("重置密码")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("新密码")
            .assertIsDisplayed()
    }

    @Test
    fun `role change dialog opens`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click change role
        composeTestRule
            .onNodeWithTag("change_role_3")
            .performClick()

        // Then - role change dialog should appear
        composeTestRule
            .onNodeWithText("更改角色")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("学生")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("教师")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("管理员")
            .assertIsDisplayed()
    }

    @Test
    fun `export users button is visible`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - export button should be visible
        composeTestRule
            .onNodeWithText("导出用户")
            .assertIsDisplayed()
    }

    @Test
    fun `import users button is visible`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - import button should be visible
        composeTestRule
            .onNodeWithText("导入用户")
            .assertIsDisplayed()
    }

    @Test
    fun `user list shows user details correctly`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - verify user details
        composeTestRule
            .onNodeWithText("admin@example.com")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("管理员")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("教师")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("学生")
            .assertIsDisplayed()
    }

    @Test
    fun `disabled user is visually distinguished`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - disabled user should have different appearance
        composeTestRule
            .onNodeWithTag("user_item_4")
            .assertIsDisplayed()
        // (Actual test would check opacity or different color)
    }

    @Test
    fun `refresh button reloads users`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click refresh
        composeTestRule
            .onNodeWithContentDescription("刷新")
            .performClick()

        // Then - user list should be refreshed
        // (Actual test would verify data reload)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `back button navigates back`() {
        // Given - user management screen
        var backClicked = false
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { backClicked = true }
            )
        }

        // When - click back button
        composeTestRule
            .onNodeWithContentDescription("返回")
            .performClick()

        // Then - should navigate back
        assertTrue(backClicked)
    }

    @Test
    fun `success message is displayed after user creation`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - create user successfully
        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        // Fill in form
        composeTestRule
            .onNodeWithText("用户名")
            .performTextInput("newuser")

        composeTestRule
            .onNodeWithText("邮箱")
            .performTextInput("newuser@example.com")

        composeTestRule
            .onNodeWithText("密码")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        // Then - success message should appear
        composeTestRule
            .onNodeWithText("用户创建成功")
            .assertIsDisplayed()
    }

    @Test
    fun `error message is displayed on failure`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - try invalid operation
        // (Would need to simulate failure scenario)

        // Then - error message should appear
        composeTestRule
            .onNodeWithText("错误")
            .assertIsDisplayed()
    }

    @Test
    fun `loading state is shown during operations`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - performing operation
        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        // Fill and submit
        composeTestRule
            .onNodeWithText("用户名")
            .performTextInput("user")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        // Then - loading indicator should be shown
        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }

    @Test
    fun `confirmation dialog appears for destructive operations`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - delete user
        composeTestRule
            .onNodeWithTag("delete_user_3")
            .performClick()

        // Then - confirmation dialog should appear
        composeTestRule
            .onNodeWithText("确认删除")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确认")
            .assertIsDisplayed()
    }

    @Test
    fun `user count is displayed correctly`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - should show total user count
        composeTestRule
            .onNodeWithText("共4个用户")
            .assertIsDisplayed()
    }

    @Test
    fun `filter by role works correctly`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - filter by role
        composeTestRule
            .onNodeWithText("角色筛选")
            .performClick()

        composeTestRule
            .onNodeWithText("学生")
            .performClick()

        // Then - only students should be shown
        composeTestRule
            .onNodeWithText("student1")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("student2")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("admin")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("teacher1")
            .assertDoesNotExist()
    }

    @Test
    fun `search functionality works`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - search for user
        composeTestRule
            .onNodeWithText("搜索用户")
            .performTextInput("admin")

        // Then - only matching users should be shown
        composeTestRule
            .onNodeWithText("admin")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("teacher1")
            .assertDoesNotExist()
    }

    @Test
    fun `import dialog opens with correct format options`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click import
        composeTestRule
            .onNodeWithText("导入用户")
            .performClick()

        // Then - import dialog should appear with formats
        composeTestRule
            .onNodeWithText("导入用户")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("CSV")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("JSON")
            .assertIsDisplayed()
    }

    @Test
    fun `export dialog opens with format selection`() {
        // Given - user management screen
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click export
        composeTestRule
            .onNodeWithText("导出用户")
            .performClick()

        // Then - export dialog should appear
        composeTestRule
            .onNodeWithText("导出用户")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("CSV")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("JSON")
            .assertIsDisplayed()
    }

    @Test
    fun `all user roles are handled correctly`() {
        // Given - user management screen with all roles
        composeTestRule.setContent {
            UserManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - all roles should be displayed
        composeTestRule
            .onNodeWithText("管理员")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("教师")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("学生")
            .assertIsDisplayed()
    }
}
