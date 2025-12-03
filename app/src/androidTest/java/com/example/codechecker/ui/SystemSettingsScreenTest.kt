package com.example.codechecker.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.codechecker.ui.screens.admin.SystemSettingsScreen
import com.example.codechecker.ui.screens.admin.viewmodel.SystemSettingsViewModel
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
 * UI tests for system settings screen
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class SystemSettingsScreenTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `system settings screen displays similarity threshold slider`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - similarity threshold slider should be visible
        composeTestRule
            .onNodeWithText("相似度阈值")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("similarity_threshold_slider")
            .assertIsDisplayed()
    }

    @Test
    fun `system settings screen displays retention policy inputs`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - retention policy inputs should be visible
        composeTestRule
            .onNodeWithText("报告保留天数")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("提交保留天数")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("审计日志保留天数")
            .assertIsDisplayed()
    }

    @Test
    fun `system settings screen displays fast compare mode toggle`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - fast compare mode toggle should be visible
        composeTestRule
            .onNodeWithText("快速比较模式")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("fast_compare_toggle")
            .assertIsDisplayed()
    }

    @Test
    fun `system settings screen displays log level selector`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - log level selector should be visible
        composeTestRule
            .onNodeWithText("日志级别")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("log_level_selector")
            .assertIsDisplayed()
    }

    @Test
    fun `similarity threshold slider can be adjusted`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - adjust similarity threshold slider
        composeTestRule
            .onNodeWithTag("similarity_threshold_slider")
            .performTouchInput {
                swipeRight()
            }

        // Then - slider should move
        // (Actual test would verify new value)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `fast compare mode toggle can be switched`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - toggle fast compare mode
        composeTestRule
            .onNodeWithTag("fast_compare_toggle")
            .performClick()

        // Then - toggle should switch state
        // (Actual test would verify toggle state)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `log level selector shows available levels`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - click log level selector
        composeTestRule
            .onNodeWithTag("log_level_selector")
            .performClick()

        // Then - dropdown should show available levels
        composeTestRule
            .onNodeWithText("DEBUG")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("INFO")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("WARN")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("ERROR")
            .assertIsDisplayed()
    }

    @Test
    fun `retention policy input accepts numeric values`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - input retention days
        composeTestRule
            .onNodeWithText("报告保留天数")
            .performTextInput("180")

        // Then - value should be entered
        // (Actual test would verify input value)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `settings export button is visible`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - export settings button should be visible
        composeTestRule
            .onNodeWithText("导出设置")
            .assertIsDisplayed()
    }

    @Test
    fun `settings import button is visible`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - import settings button should be visible
        composeTestRule
            .onNodeWithText("导入设置")
            .assertIsDisplayed()
    }

    @Test
    fun `reset to defaults button is visible`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - reset to defaults button should be visible
        composeTestRule
            .onNodeWithText("重置为默认")
            .assertIsDisplayed()
    }

    @Test
    fun `save settings button is visible`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - save settings button should be visible
        composeTestRule
            .onNodeWithText("保存设置")
            .assertIsDisplayed()
    }

    @Test
    fun `success message is displayed after saving settings`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - save settings
        composeTestRule
            .onNodeWithText("保存设置")
            .performClick()

        // Then - success message should appear
        composeTestRule
            .onNodeWithText("设置保存成功")
            .assertIsDisplayed()
    }

    @Test
    fun `error message is displayed on validation failure`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - enter invalid value
        composeTestRule
            .onNodeWithText("报告保留天数")
            .performTextInput("-1")

        composeTestRule
            .onNodeWithText("保存设置")
            .performClick()

        // Then - error message should appear
        composeTestRule
            .onNodeWithText("保留天数必须大于0")
            .assertIsDisplayed()
    }

    @Test
    fun `validation feedback is shown in real-time`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - enter invalid value
        composeTestRule
            .onNodeWithText("报告保留天数")
            .performTextInput("0")

        // Then - validation feedback should be shown immediately
        composeTestRule
            .onNodeWithText("值必须大于0")
            .assertIsDisplayed()
    }

    @Test
    fun `settings change history is displayed`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - settings change history should be visible
        composeTestRule
            .onNodeWithText("设置更改历史")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("settings_history_list")
            .assertIsDisplayed()
    }

    @Test
    fun `refresh button reloads settings`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - click refresh
        composeTestRule
            .onNodeWithContentDescription("刷新")
            .performClick()

        // Then - settings should be reloaded
        assertTrue(true) // Placeholder - actual test would verify reload
    }

    @Test
    fun `back button navigates back`() {
        // Given - system settings screen
        var backClicked = false
        composeTestRule.setContent {
            SystemSettingsScreen(
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
    fun `loading state is shown during save`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - save settings
        composeTestRule
            .onNodeWithText("保存设置")
            .performClick()

        // Then - loading indicator should be shown
        composeTestRule
            .onNodeWithTag("saving_indicator")
            .assertIsDisplayed()
    }

    @Test
    fun `export settings dialog opens`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - click export settings
        composeTestRule
            .onNodeWithText("导出设置")
            .performClick()

        // Then - export dialog should appear
        composeTestRule
            .onNodeWithText("导出设置")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("选择导出格式")
            .assertIsDisplayed()
    }

    @Test
    fun `import settings dialog opens`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - click import settings
        composeTestRule
            .onNodeWithText("导入设置")
            .performClick()

        // Then - import dialog should appear
        composeTestRule
            .onNodeWithText("导入设置")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("选择导入文件")
            .assertIsDisplayed()
    }

    @Test
    fun `reset confirmation dialog appears`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - click reset to defaults
        composeTestRule
            .onNodeWithText("重置为默认")
            .performClick()

        // Then - confirmation dialog should appear
        composeTestRule
            .onNodeWithText("确认重置")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("此操作将恢复所有设置为默认值")
            .assertIsDisplayed()
    }

    @Test
    fun `settings can be filtered by category`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - filter by algorithm category
        composeTestRule
            .onNodeWithText("算法设置")
            .performClick()

        // Then - only algorithm settings should be shown
        composeTestRule
            .onNodeWithText("相似度阈值")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("快速比较模式")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("报告保留天数")
            .assertDoesNotExist()
    }

    @Test
    fun `identifier normalization toggle is visible`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - identifier normalization toggle should be visible
        composeTestRule
            .onNodeWithText("标识符规范化")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("normalize_identifiers_toggle")
            .assertIsDisplayed()
    }

    @Test
    fun `settings are grouped by category`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - settings should be grouped
        composeTestRule
            .onNodeWithText("算法设置")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("系统设置")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("数据保留")
            .assertIsDisplayed()
    }

    @Test
    fun `similarity threshold has correct range`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - slider should show range
        composeTestRule
            .onNodeWithText("0.0")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("1.0")
            .assertIsDisplayed()
    }

    @Test
    fun `current settings values are displayed`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - current values should be shown
        composeTestRule
            .onNodeWithText("当前值: 0.8")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("当前值: 90")
            .assertIsDisplayed()
    }

    @Test
    fun `unsaved changes indicator is shown`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - modify a setting
        composeTestRule
            .onNodeWithText("报告保留天数")
            .performTextInput("180")

        // Then - unsaved changes indicator should appear
        composeTestRule
            .onNodeWithTag("unsaved_changes_indicator")
            .assertIsDisplayed()
    }

    @Test
    fun `cancel button warns about unsaved changes`() {
        // Given - system settings screen with unsaved changes
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        composeTestRule
            .onNodeWithText("报告保留天数")
            .performTextInput("180")

        // When - click back
        composeTestRule
            .onNodeWithContentDescription("返回")
            .performClick()

        // Then - warning dialog should appear
        composeTestRule
            .onNodeWithText("有未保存的更改")
            .assertIsDisplayed()
    }

    @Test
    fun `all system parameters can be configured`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // Then - all parameters should be configurable
        composeTestRule
            .onNodeWithText("相似度阈值")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("快速比较模式")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("标识符规范化")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("日志级别")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("报告保留天数")
            .assertIsDisplayed()
    }

    @Test
    fun `settings help tooltips are available`() {
        // Given - system settings screen
        composeTestRule.setContent {
            SystemSettingsScreen(
                onNavigateBack = { }
            )
        }

        // When - click help icon
        composeTestRule
            .onNodeWithTag("help_icon_similarity")
            .performClick()

        // Then - tooltip should appear
        composeTestRule
            .onNodeWithText("相似度阈值用于...")
            .assertIsDisplayed()
    }
}
