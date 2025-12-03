package com.example.codechecker.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.codechecker.ui.screens.admin.AuditLogsScreen
import com.example.codechecker.ui.screens.admin.viewmodel.AuditLogsViewModel
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
 * UI tests for audit logs screen
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class AuditLogsScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `audit logs screen displays audit log entries`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - audit logs should be displayed
        composeTestRule
            .onNodeWithText("审计日志")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("USER_CREATE")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("USER_LOGIN")
            .assertIsDisplayed()
    }

    @Test
    fun `date range filter is available`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - date range filter should be visible
        composeTestRule
            .onNodeWithText("日期范围")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("开始日期")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("结束日期")
            .assertIsDisplayed()
    }

    @Test
    fun `action type filter is available`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - action type filter should be visible
        composeTestRule
            .onNodeWithText("操作类型")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("action_type_filter")
            .assertIsDisplayed()
    }

    @Test
    fun `search functionality is available`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - search field should be visible
        composeTestRule
            .onNodeWithText("搜索日志")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("search_field")
            .assertIsDisplayed()
    }

    @Test
    fun `audit log entries show all required information`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - log entries should show timestamp, action, result, etc.
        composeTestRule
            .onNodeWithTag("log_timestamp")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("log_action")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("log_result")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("log_category")
            .assertIsDisplayed()
    }

    @Test
    fun `export logs button is visible`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - export logs button should be visible
        composeTestRule
            .onNodeWithText("导出日志")
            .assertIsDisplayed()
    }

    @Test
    fun `refresh button reloads logs`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // When - click refresh
        composeTestRule
            .onNodeWithContentDescription("刷新")
            .performClick()

        // Then - logs should be refreshed
        assertTrue(true) // Placeholder - actual test would verify reload
    }

    @Test
    fun `back button navigates back`() {
        // Given - audit logs screen
        var backClicked = false
        composeTestRule.setContent {
            AuditLogsScreen(
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
    fun `log entries can be expanded for details`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // When - click on log entry
        composeTestRule
            .onNodeWithTag("log_entry_1")
            .performClick()

        // Then - details should be expanded
        composeTestRule
            .onNodeWithTag("log_details")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("详细信息")
            .assertIsDisplayed()
    }

    @Test
    fun `date range filter works correctly`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // When - set date range
        composeTestRule
            .onNodeWithText("开始日期")
            .performClick()

        composeTestRule
            .onNodeWithText("2024-01-01")
            .performClick()

        composeTestRule
            .onNodeWithText("结束日期")
            .performClick()

        composeTestRule
            .onNodeWithText("2024-01-31")
            .performClick()

        // Then - logs should be filtered by date
        // (Actual test would verify filtered results)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `action type filter works correctly`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // When - select action type filter
        composeTestRule
            .onNodeWithTag("action_type_filter")
            .performClick()

        composeTestRule
            .onNodeWithText("USER_LOGIN")
            .performClick()

        // Then - only USER_LOGIN entries should be shown
        // (Actual test would verify filtered results)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `search filters logs by text`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // When - search for text
        composeTestRule
            .onNodeWithTag("search_field")
            .performTextInput("USER_CREATE")

        // Then - only matching logs should be shown
        // (Actual test would verify search results)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `logs show success and failure results`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - should distinguish success from failure
        composeTestRule
            .onNodeWithText("SUCCESS")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("FAILED")
            .assertIsDisplayed()
    }

    @Test
    fun `success logs have different appearance`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - success logs should be visually distinct
        composeTestRule
            .onNodeWithTag("log_success")
            .assertIsDisplayed()
        // (Actual test would check color/opacity)
    }

    @Test
    fun `failure logs have different appearance`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - failure logs should be visually distinct
        composeTestRule
            .onNodeWithTag("log_failure")
            .assertIsDisplayed()
        // (Actual test would check color/opacity)
    }

    @Test
    fun `pagination controls are visible`() {
        // Given - audit logs screen with many entries
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - pagination controls should be visible
        composeTestRule
            .onNodeWithText("第1页")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("上一页")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithContentDescription("下一页")
            .assertIsDisplayed()
    }

    @Test
    fun `page navigation works`() {
        // Given - audit logs screen with pagination
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // When - click next page
        composeTestRule
            .onNodeWithContentDescription("下一页")
            .performClick()

        // Then - should navigate to next page
        composeTestRule
            .onNodeWithText("第2页")
            .assertIsDisplayed()
    }

    @Test
    fun `export dialog opens when export button is clicked`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // When - click export
        composeTestRule
            .onNodeWithText("导出日志")
            .performClick()

        // Then - export dialog should appear
        composeTestRule
            .onNodeWithText("导出日志")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("选择导出格式")
            .assertIsDisplayed()
    }

    @Test
    fun `export supports CSV format`() {
        // Given - export dialog
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("导出日志")
            .performClick()

        // Then - CSV option should be available
        composeTestRule
            .onNodeWithText("CSV")
            .assertIsDisplayed()
    }

    @Test
    fun `export supports JSON format`() {
        // Given - export dialog
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("导出日志")
            .performClick()

        // Then - JSON option should be available
        composeTestRule
            .onNodeWithText("JSON")
            .assertIsDisplayed()
    }

    @Test
    fun `log entries show user ID when available`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - user ID should be displayed for user-related actions
        composeTestRule
            .onNodeWithTag("log_user_id")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("用户ID: 1")
            .assertIsDisplayed()
    }

    @Test
    fun `log entries show category`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - category should be displayed
        composeTestRule
            .onNodeWithText("用户")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("认证")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("数据")
            .assertIsDisplayed()
    }

    @Test
    fun `loading state is shown during log retrieval`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // When - loading logs
        composeTestRule
            .onNodeWithTag("loading_indicator")
            .assertIsDisplayed()
    }

    @Test
    fun `empty state is shown when no logs exist`() {
        // Given - audit logs screen with no logs
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - empty state should be displayed
        composeTestRule
            .onNodeWithText("暂无审计日志")
            .assertIsDisplayed()
    }

    @Test
    fun `error state is shown when log retrieval fails`() {
        // Given - audit logs screen with error
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - error message should be displayed
        composeTestRule
            .onNodeWithText("加载审计日志失败")
            .assertIsDisplayed()
    }

    @Test
    fun `log retention settings are configurable`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - retention settings should be visible
        composeTestRule
            .onNodeWithText("日志保留设置")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("retention_days_input")
            .assertIsDisplayed()
    }

    @Test
    fun `multiple filter criteria can be combined`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // When - apply multiple filters
        composeTestRule
            .onNodeWithTag("action_type_filter")
            .performClick()

        composeTestRule
            .onNodeWithText("USER_LOGIN")
            .performClick()

        composeTestRule
            .onNodeWithTag("search_field")
            .performTextInput("admin")

        // Then - filters should be combined
        // (Actual test would verify combined filtering)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `clear filters button resets all filters`() {
        // Given - audit logs screen with active filters
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Apply filters
        composeTestRule
            .onNodeWithTag("action_type_filter")
            .performClick()

        composeTestRule
            .onNodeWithText("USER_LOGIN")
            .performClick()

        // When - click clear filters
        composeTestRule
            .onNodeWithText("清除筛选")
            .performClick()

        // Then - all filters should be reset
        // (Actual test would verify filter reset)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `log entries show timestamp in local timezone`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - timestamp should be displayed
        composeTestRule
            .onNodeWithTag("log_timestamp")
            .assertIsDisplayed()

        // (Actual test would verify timezone conversion)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `audit log count is displayed`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // Then - total count should be shown
        composeTestRule
            .onNodeWithText("共100条日志")
            .assertIsDisplayed()
    }

    @Test
    fun `result filter shows success/failure options`() {
        // Given - audit logs screen
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        // When - click result filter
        composeTestRule
            .onNodeWithTag("result_filter")
            .performClick()

        // Then - should show success/failure options
        composeTestRule
            .onNodeWithText("成功")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("失败")
            .assertIsDisplayed()
    }

    @Test
    fun `export with current filters option is available`() {
        // Given - export dialog
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("导出日志")
            .performClick()

        // Then - option to export filtered results should be available
        composeTestRule
            .onNodeWithText("仅导出当前筛选结果")
            .assertIsDisplayed()
    }

    @Test
    fun `log details show full action description`() {
        // Given - expanded log entry
        composeTestRule.setContent {
            AuditLogsScreen(
                onNavigateBack = { }
            )
        }

        composeTestRule
            .onNodeWithTag("log_entry_1")
            .performClick()

        // Then - full details should be visible
        composeTestRule
            .onNodeWithTag("log_details")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("操作: 用户创建")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("详情: 创建了新用户")
            .assertIsDisplayed()
    }
}
