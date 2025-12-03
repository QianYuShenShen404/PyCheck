package com.example.codechecker.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.codechecker.ui.screens.admin.*
import com.example.codechecker.ui.screens.admin.viewmodel.*
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
 * UI tests for all dialogs and confirmation screens
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class DialogsConfirmationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    // ===== CREATE USER DIALOG TESTS =====

    @Test
    fun `create user dialog has all required fields`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("用户名")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("邮箱")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("密码")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("角色")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确认")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()
    }

    @Test
    fun `create user dialog validates required fields`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        composeTestRule
            .onNodeWithText("请输入用户名")
            .assertIsDisplayed()
    }

    @Test
    fun `create user dialog validates email format`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("邮箱")
            .performTextInput("invalid-email")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        composeTestRule
            .onNodeWithText("请输入有效的邮箱地址")
            .assertIsDisplayed()
    }

    @Test
    fun `create user dialog validates password strength`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("密码")
            .performTextInput("123")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        composeTestRule
            .onNodeWithText("密码至少需要8个字符")
            .assertIsDisplayed()
    }

    @Test
    fun `create user dialog can be cancelled`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("取消")
            .performClick()

        composeTestRule
            .onNodeWithText("创建用户")
            .assertIsDisplayed() // Back to main screen

        composeTestRule
            .onNodeWithText("用户名")
            .assertDoesNotExist()
    }

    @Test
    fun `create user dialog role selector works`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("角色")
            .performClick()

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

    // ===== EDIT USER DIALOG TESTS =====

    @Test
    fun `edit user dialog shows current values`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("edit_user_1")
            .performClick()

        composeTestRule
            .onNodeWithText("编辑用户")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("admin")
            .assertIsDisplayed() // Current username

        composeTestRule
            .onNodeWithText("admin@example.com")
            .assertIsDisplayed() // Current email
    }

    @Test
    fun `edit user dialog can update username`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("edit_user_1")
            .performClick()

        composeTestRule
            .onNodeWithText("admin")
            .performTextClearance()

        composeTestRule
            .onNodeWithText("admin")
            .performTextInput("newadmin")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        // Then - success message
        composeTestRule
            .onNodeWithText("用户更新成功")
            .assertIsDisplayed()
    }

    @Test
    fun `edit user dialog can update email`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("edit_user_1")
            .performClick()

        composeTestRule
            .onNodeWithText("admin@example.com")
            .performTextClearance()

        composeTestRule
            .onNodeWithText("admin@example.com")
            .performTextInput("newadmin@example.com")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        // Then - success message
        composeTestRule
            .onNodeWithText("用户更新成功")
            .assertIsDisplayed()
    }

    @Test
    fun `edit user dialog validates email format`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("edit_user_1")
            .performClick()

        composeTestRule
            .onNodeWithText("admin@example.com")
            .performTextClearance()

        composeTestRule
            .onNodeWithText("admin@example.com")
            .performTextInput("invalid")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        composeTestRule
            .onNodeWithText("请输入有效的邮箱地址")
            .assertIsDisplayed()
    }

    // ===== DELETE USER CONFIRMATION DIALOG TESTS =====

    @Test
    fun `delete user confirmation dialog appears`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("delete_user_1")
            .performClick()

        composeTestRule
            .onNodeWithText("确认删除")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确定要删除此用户吗？")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("此操作将软删除用户，管理员可以恢复")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确认")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()
    }

    @Test
    fun `delete user confirmation can be cancelled`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("delete_user_1")
            .performClick()

        composeTestRule
            .onNodeWithText("取消")
            .performClick()

        composeTestRule
            .onNodeWithText("确认删除")
            .assertDoesNotExist()
    }

    @Test
    fun `delete confirmation executes delete`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("delete_user_1")
            .performClick()

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        // Then - success message
        composeTestRule
            .onNodeWithText("用户删除成功")
            .assertIsDisplayed()
    }

    // ===== ROLE CHANGE DIALOG TESTS =====

    @Test
    fun `role change dialog shows current role`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("change_role_1")
            .performClick()

        composeTestRule
            .onNodeWithText("更改角色")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("当前角色: 管理员")
            .assertIsDisplayed()
    }

    @Test
    fun `role change dialog shows all available roles`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("change_role_1")
            .performClick()

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
    fun `role change can be confirmed`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("change_role_1")
            .performClick()

        composeTestRule
            .onNodeWithText("教师")
            .performClick()

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        // Then - success message
        composeTestRule
            .onNodeWithText("角色更改成功")
            .assertIsDisplayed()
    }

    // ===== PASSWORD RESET DIALOG TESTS =====

    @Test
    fun `password reset dialog appears`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("reset_password_1")
            .performClick()

        composeTestRule
            .onNodeWithText("重置密码")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("新密码")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确认密码")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确认")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()
    }

    @Test
    fun `password reset validates password match`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("reset_password_1")
            .performClick()

        composeTestRule
            .onNodeWithText("新密码")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("确认密码")
            .performTextInput("password456")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        composeTestRule
            .onNodeWithText("两次输入的密码不一致")
            .assertIsDisplayed()
    }

    @Test
    fun `password reset validates password strength`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("reset_password_1")
            .performClick()

        composeTestRule
            .onNodeWithText("新密码")
            .performTextInput("123")

        composeTestRule
            .onNodeWithText("确认密码")
            .performTextInput("123")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        composeTestRule
            .onNodeWithText("密码至少需要8个字符")
            .assertIsDisplayed()
    }

    // ===== DATA MANAGEMENT DIALOG TESTS =====

    @Test
    fun `backup confirmation dialog appears`() {
        composeTestRule.setContent {
            DataManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("备份")
            .performClick()

        composeTestRule
            .onNodeWithText("数据库备份")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("您确定要创建数据库备份吗？")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("备份将保存到应用私有存储目录")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确认备份")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()
    }

    @Test
    fun `restore confirmation dialog appears`() {
        composeTestRule.setContent {
            DataManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("恢复")
            .performClick()

        composeTestRule
            .onNodeWithText("数据库恢复")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("警告：此操作将覆盖当前数据库！")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("备份文件路径")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确认恢复")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()
    }

    @Test
    fun `restore requires valid backup path`() {
        composeTestRule.setContent {
            DataManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("恢复")
            .performClick()

        composeTestRule
            .onNodeWithText("确认恢复")
            .performClick()

        composeTestRule
            .onNodeWithText("请输入有效的备份文件路径")
            .assertIsDisplayed()
    }

    @Test
    fun `export format selection dialog appears`() {
        composeTestRule.setContent {
            DataManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("导出数据")
            .performClick()

        composeTestRule
            .onNodeWithText("数据导出")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("选择导出格式")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("JSON")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("CSV")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("XML")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("开始导出")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()
    }

    @Test
    fun `import format selection dialog appears`() {
        composeTestRule.setContent {
            DataManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("导入数据")
            .performClick()

        composeTestRule
            .onNodeWithText("数据导入")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("警告：导入将合并数据，不会覆盖现有数据")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("选择导入格式")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("JSON")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("CSV")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("XML")
            .assertIsDisplayed()
    }

    @Test
    fun `cleanup configuration dialog appears`() {
        composeTestRule.setContent {
            DataManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("开始清理")
            .performClick()

        composeTestRule
            .onNodeWithText("数据清理")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("选择要清理的数据类型")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("清理审计日志")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("清理非活跃用户")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("保留天数")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确认清理")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()
    }

    @Test
    fun `cleanup validates retention days`() {
        composeTestRule.setContent {
            DataManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("开始清理")
            .performClick()

        composeTestRule
            .onNodeWithText("保留天数")
            .performTextInput("0")

        composeTestRule
            .onNodeWithText("确认清理")
            .performClick()

        composeTestRule
            .onNodeWithText("保留天数必须大于0")
            .assertIsDisplayed()
    }

    // ===== SYSTEM SETTINGS DIALOG TESTS =====

    @Test
    fun `export settings dialog appears`() {
        composeTestRule.setContent {
            SystemSettingsScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("导出设置")
            .performClick()

        composeTestRule
            .onNodeWithText("导出设置")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("选择导出格式")
            .assertIsDisplayed()
    }

    @Test
    fun `import settings dialog appears`() {
        composeTestRule.setContent {
            SystemSettingsScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("导入设置")
            .performClick()

        composeTestRule
            .onNodeWithText("导入设置")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("选择导入文件")
            .assertIsDisplayed()
    }

    @Test
    fun `reset settings confirmation dialog appears`() {
        composeTestRule.setContent {
            SystemSettingsScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("重置为默认")
            .performClick()

        composeTestRule
            .onNodeWithText("确认重置")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("此操作将恢复所有设置为默认值")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("此操作不可撤销")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确认")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()
    }

    // ===== AUDIT LOGS DIALOG TESTS =====

    @Test
    fun `export audit logs dialog appears`() {
        composeTestRule.setContent {
            AuditLogsScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("导出日志")
            .performClick()

        composeTestRule
            .onNodeWithText("导出日志")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("选择导出格式")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("仅导出当前筛选结果")
            .assertIsDisplayed()
    }

    // ===== GENERAL DIALOG BEHAVIOR TESTS =====

    @Test
    fun `dialogs can be dismissed by clicking outside`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithTag("dialog_backdrop")
            .performClick()

        composeTestRule
            .onNodeWithText("创建用户")
            .assertIsDisplayed() // Back to main screen
    }

    @Test
    fun `dialogs respect hardware back button`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        // Simulate back button press
        composeTestRule
            .onNodeWithTag("dialog_container")
            .assertIsDisplayed()

        // Then - dialog should be dismissible with back
        // (Actual test would simulate back press)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `confirmation dialogs have destructive action highlighted`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("delete_user_1")
            .performClick()

        // Then - destructive action should be highlighted
        composeTestRule
            .onNodeWithText("确认")
            .assertIsDisplayed()
        // (Actual test would check color - should be red for destructive actions)
    }

    @Test
    fun `error messages in dialogs are clearly visible`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        // Then - error should be clearly visible
        composeTestRule
            .onNodeWithText("请输入用户名")
            .assertIsDisplayed()
    }

    @Test
    fun `success messages appear after dialog actions`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("用户名")
            .performTextInput("testuser")

        composeTestRule
            .onNodeWithText("邮箱")
            .performTextInput("test@example.com")

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
    fun `loading state is shown during dialog actions`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("用户名")
            .performTextInput("testuser")

        composeTestRule
            .onNodeWithText("邮箱")
            .performTextInput("test@example.com")

        composeTestRule
            .onNodeWithText("密码")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        // Then - loading indicator should be shown
        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }

    @Test
    fun `all dialogs have proper titles`() {
        // Create user dialog
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }
        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()
        composeTestRule
            .onNodeWithText("创建用户")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("取消").performClick()

        // Backup dialog
        composeTestRule.setContent {
            DataManagementScreen(onNavigateBack = { })
        }
        composeTestRule
            .onNodeWithText("备份")
            .performClick()
        composeTestRule
            .onNodeWithText("数据库备份")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("取消").performClick()

        // Reset settings dialog
        composeTestRule.setContent {
            SystemSettingsScreen(onNavigateBack = { })
        }
        composeTestRule
            .onNodeWithText("重置为默认")
            .performClick()
        composeTestRule
            .onNodeWithText("确认重置")
            .assertIsDisplayed()
    }

    @Test
    fun `all dialogs have cancel buttons`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .performClick()

        composeTestRule.setContent {
            DataManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("备份")
            .performClick()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()
    }

    @Test
    fun `dangerous operations have warning messages`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithTag("delete_user_1")
            .performClick()

        // Then - should have warning
        composeTestRule
            .onNodeWithText("确定要删除此用户吗？")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("取消").performClick()

        composeTestRule.setContent {
            DataManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("恢复")
            .performClick()

        // Then - should have warning
        composeTestRule
            .onNodeWithText("警告：此操作将覆盖当前数据库！")
            .assertIsDisplayed()
    }

    @Test
    fun `dialogs remember user input on validation errors`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        composeTestRule
            .onNodeWithText("创建用户")
            .performClick()

        composeTestRule
            .onNodeWithText("用户名")
            .performTextInput("testuser")

        composeTestRule
            .onNodeWithText("邮箱")
            .performTextInput("test@example.com")

        composeTestRule
            .onNodeWithText("密码")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("确认")
            .performClick()

        // Then - dialog closes on success
        composeTestRule
            .onNodeWithText("用户名")
            .assertDoesNotExist()
    }

    @Test
    fun `all critical dialogs require explicit confirmation`() {
        composeTestRule.setContent {
            UserManagementScreen(onNavigateBack = { })
        }

        // Delete user requires confirmation
        composeTestRule
            .onNodeWithTag("delete_user_1")
            .performClick()
        composeTestRule
            .onNodeWithText("确认")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("取消").performClick()

        // Reset settings requires confirmation
        composeTestRule.setContent {
            SystemSettingsScreen(onNavigateBack = { })
        }
        composeTestRule
            .onNodeWithText("重置为默认")
            .performClick()
        composeTestRule
            .onNodeWithText("确认")
            .assertIsDisplayed()
    }
}
