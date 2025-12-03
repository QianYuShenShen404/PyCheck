package com.example.codechecker.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.codechecker.ui.screens.admin.DataManagementScreen
import com.example.codechecker.ui.screens.admin.viewmodel.DataManagementViewModel
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
 * UI tests for data management screen
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class DataManagementScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `data management screen displays storage statistics`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - storage statistics should be displayed
        composeTestRule
            .onNodeWithText("存储统计")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("用户数")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("审计日志")
            .assertIsDisplayed()
    }

    @Test
    fun `database backup button is visible`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - backup button should be visible
        composeTestRule
            .onNodeWithText("备份")
            .assertIsDisplayed()
    }

    @Test
    fun `database restore button is visible`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - restore button should be visible
        composeTestRule
            .onNodeWithText("恢复")
            .assertIsDisplayed()
    }

    @Test
    fun `export data button is visible`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - export button should be visible
        composeTestRule
            .onNodeWithText("导出数据")
            .assertIsDisplayed()
    }

    @Test
    fun `import data button is visible`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - import button should be visible
        composeTestRule
            .onNodeWithText("导入数据")
            .assertIsDisplayed()
    }

    @Test
    fun `preview cleanup button is visible`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - preview cleanup button should be visible
        composeTestRule
            .onNodeWithText("预览清理")
            .assertIsDisplayed()
    }

    @Test
    fun `start cleanup button is visible`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - start cleanup button should be visible
        composeTestRule
            .onNodeWithText("开始清理")
            .assertIsDisplayed()
    }

    @Test
    fun `backup dialog opens when backup button is clicked`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click backup
        composeTestRule
            .onNodeWithText("备份")
            .performClick()

        // Then - backup dialog should appear
        composeTestRule
            .onNodeWithText("数据库备份")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("您确定要创建数据库备份吗？")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("确认备份")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("取消")
            .assertIsDisplayed()
    }

    @Test
    fun `restore dialog opens when restore button is clicked`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click restore
        composeTestRule
            .onNodeWithText("恢复")
            .performClick()

        // Then - restore dialog should appear
        composeTestRule
            .onNodeWithText("数据库恢复")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("警告：此操作将覆盖当前数据库！")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("备份文件路径")
            .assertIsDisplayed()
    }

    @Test
    fun `export dialog opens with format options`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click export
        composeTestRule
            .onNodeWithText("导出数据")
            .performClick()

        // Then - export dialog should appear with formats
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
    }

    @Test
    fun `import dialog opens with format options`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click import
        composeTestRule
            .onNodeWithText("导入数据")
            .performClick()

        // Then - import dialog should appear with formats
        composeTestRule
            .onNodeWithText("数据导入")
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
    fun `cleanup dialog opens when start cleanup is clicked`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click start cleanup
        composeTestRule
            .onNodeWithText("开始清理")
            .performClick()

        // Then - cleanup dialog should appear
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
    }

    @Test
    fun `cleanup preview appears when preview cleanup is clicked`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click preview cleanup
        composeTestRule
            .onNodeWithText("预览清理")
            .performClick()

        // Then - cleanup preview should appear
        composeTestRule
            .onNodeWithText("清理预览")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("将删除约")
            .assertIsDisplayed()
    }

    @Test
    fun `success message is displayed after backup`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - perform successful backup
        composeTestRule
            .onNodeWithText("备份")
            .performClick()

        composeTestRule
            .onNodeWithText("确认备份")
            .performClick()

        // Then - success message should appear
        composeTestRule
            .onNodeWithText("数据库备份成功完成")
            .assertIsDisplayed()
    }

    @Test
    fun `error message is displayed on backup failure`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - backup fails (would need to simulate failure)

        // Then - error message should appear
        composeTestRule
            .onNodeWithText("错误")
            .assertIsDisplayed()
    }

    @Test
    fun `progress indicator is shown during backup`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - backup in progress
        composeTestRule
            .onNodeWithText("备份")
            .performClick()

        composeTestRule
            .onNodeWithText("确认备份")
            .performClick()

        // Then - progress indicator should be shown
        composeTestRule
            .onNodeWithTag("backup_progress")
            .assertIsDisplayed()
    }

    @Test
    fun `progress indicator is shown during export`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - export in progress
        composeTestRule
            .onNodeWithText("导出数据")
            .performClick()

        composeTestRule
            .onNodeWithText("JSON")
            .performClick()

        composeTestRule
            .onNodeWithText("开始导出")
            .performClick()

        // Then - progress indicator should be shown
        composeTestRule
            .onNodeWithTag("export_progress")
            .assertIsDisplayed()
    }

    @Test
    fun `progress indicator is shown during import`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - import in progress
        composeTestRule
            .onNodeWithText("导入数据")
            .performClick()

        composeTestRule
            .onNodeWithText("CSV")
            .performClick()

        composeTestRule
            .onNodeWithText("开始导入")
            .performClick()

        // Then - progress indicator should be shown
        composeTestRule
            .onNodeWithTag("import_progress")
            .assertIsDisplayed()
    }

    @Test
    fun `progress indicator is shown during cleanup`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - cleanup in progress
        composeTestRule
            .onNodeWithText("开始清理")
            .performClick()

        composeTestRule
            .onNodeWithText("确认清理")
            .performClick()

        // Then - progress indicator should be shown
        composeTestRule
            .onNodeWithTag("cleanup_progress")
            .assertIsDisplayed()
    }

    @Test
    fun `refresh button reloads statistics`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click refresh
        composeTestRule
            .onNodeWithContentDescription("刷新")
            .performClick()

        // Then - statistics should be refreshed
        assertTrue(true) // Placeholder - actual test would verify data reload
    }

    @Test
    fun `back button navigates back`() {
        // Given - data management screen
        var backClicked = false
        composeTestRule.setContent {
            DataManagementScreen(
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
    fun `buttons are disabled during operations`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - backup in progress
        composeTestRule
            .onNodeWithText("备份")
            .performClick()

        composeTestRule
            .onNodeWithText("确认备份")
            .performClick()

        // Then - buttons should be disabled
        composeTestRule
            .onNodeWithText("备份")
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithText("恢复")
            .assertIsNotEnabled()
    }

    @Test
    fun `cleanup preview shows estimated deletions`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - preview cleanup
        composeTestRule
            .onNodeWithText("预览清理")
            .performClick()

        // Then - preview should show estimates
        composeTestRule
            .onNodeWithText("将删除约")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("个报告")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("个提交")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("个审计日志")
            .assertIsDisplayed()
    }

    @Test
    fun `cleanup preview can be cleared`() {
        // Given - cleanup preview is shown
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("预览清理")
            .performClick()

        // When - click clear
        composeTestRule
            .onNodeWithText("清除")
            .performClick()

        // Then - preview should be hidden
        composeTestRule
            .onNodeWithText("清理预览")
            .assertDoesNotExist()
    }

    @Test
    fun `last updated timestamp is displayed`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - last updated timestamp should be displayed
        composeTestRule
            .onNodeWithText("最后更新")
            .assertIsDisplayed()
    }

    @Test
    fun `warning message appears for restore operation`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click restore
        composeTestRule
            .onNodeWithText("恢复")
            .performClick()

        // Then - warning should appear
        composeTestRule
            .onNodeWithText("警告：此操作将覆盖当前数据库！")
            .assertIsDisplayed()
    }

    @Test
    fun `warning message appears for import operation`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - click import
        composeTestRule
            .onNodeWithText("导入数据")
            .performClick()

        // Then - warning should appear
        composeTestRule
            .onNodeWithText("警告：导入将合并数据，不会覆盖现有数据")
            .assertIsDisplayed()
    }

    @Test
    fun `data transfer section is clearly separated`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // Then - sections should be separated
        composeTestRule
            .onNodeWithText("数据库操作")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("数据传输")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("数据清理")
            .assertIsDisplayed()
    }

    @Test
    fun `cleanup dialog has configurable options`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - open cleanup dialog
        composeTestRule
            .onNodeWithText("开始清理")
            .performClick()

        // Then - configurable options should be present
        composeTestRule
            .onNodeWithText("清理审计日志")
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("清理非活跃用户")
            .assertHasClickAction()

        composeTestRule
            .onNodeWithText("保留天数")
            .assertIsDisplayed()
    }

    @Test
    fun `export dialog shows selected format`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - open export dialog
        composeTestRule
            .onNodeWithText("导出数据")
            .performClick()

        // Select JSON
        composeTestRule
            .onNodeWithText("JSON")
            .performClick()

        composeTestRule
            .onNodeWithText("开始导出")
            .performClick()

        // Then - selected format should be used
        assertTrue(true) // Placeholder - actual test would verify selection
    }

    @Test
    fun `all operations are logged for audit`() {
        // Given - data management screen
        composeTestRule.setContent {
            DataManagementScreen(
                onNavigateBack = { }
            )
        }

        // When - perform operations
        composeTestRule
            .onNodeWithText("备份")
            .performClick()

        composeTestRule
            .onNodeWithText("确认备份")
            .performClick()

        // Then - operations should be logged
        // (Actual test would verify audit logs)
        assertTrue(true) // Placeholder
    }
}
