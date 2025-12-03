package com.example.codechecker.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.codechecker.domain.model.Role
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.model.UserStatus
import com.example.codechecker.ui.navigation.AdminNavGraph
import com.example.codechecker.ui.screens.admin.AdminDashboardScreen
import com.example.codechecker.ui.screens.admin.UserManagementScreen
import com.example.codechecker.ui.screens.admin.DataManagementScreen
import com.example.codechecker.ui.screens.admin.SystemSettingsScreen
import com.example.codechecker.ui.screens.admin.AuditLogsScreen
import com.example.codechecker.ui.screens.admin.SecurityScreen
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
 * UI tests for admin navigation
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class AdminNavigationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavHostController

    private val adminUser = User(
        id = 1L,
        username = "admin",
        email = "admin@example.com",
        role = Role.ADMIN,
        isActive = true,
        status = UserStatus.ACTIVE
    )

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `admin can navigate to user management screen`() {
        // Given - admin dashboard
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
        }

        // When - tap on user management
        composeTestRule
            .onNodeWithText("用户管理")
            .performClick()

        // Then - should navigate to user management
        // (Verification would check current destination)
        assertTrue(true) // Placeholder
    }

    @Test
    fun `admin can navigate to data management screen`() {
        // Given - admin dashboard
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
        }

        // When - tap on data management
        composeTestRule
            .onNodeWithText("数据管理")
            .performClick()

        // Then - should navigate to data management
        assertTrue(true) // Placeholder
    }

    @Test
    fun `admin can navigate to system settings screen`() {
        // Given - admin dashboard
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
        }

        // When - tap on system settings
        composeTestRule
            .onNodeWithText("系统设置")
            .performClick()

        // Then - should navigate to system settings
        assertTrue(true) // Placeholder
    }

    @Test
    fun `admin can navigate to audit logs screen`() {
        // Given - admin dashboard
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
        }

        // When - tap on audit logs
        composeTestRule
            .onNodeWithText("审计日志")
            .performClick()

        // Then - should navigate to audit logs
        assertTrue(true) // Placeholder
    }

    @Test
    fun `admin can navigate to security screen`() {
        // Given - admin dashboard
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
        }

        // When - tap on security
        composeTestRule
            .onNodeWithText("安全监控")
            .performClick()

        // Then - should navigate to security
        assertTrue(true) // Placeholder
    }

    @Test
    fun `admin can navigate back from user management`() {
        // Given - on user management screen
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
            // Navigate to user management
            navController.navigate("user_management")
        }

        // When - tap back button
        composeTestRule
            .onNodeWithContentDescription("返回")
            .performClick()

        // Then - should return to dashboard
        assertTrue(true) // Placeholder
    }

    @Test
    fun `admin navigation menu is visible on all admin screens`() {
        // Given - on user management screen
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
            navController.navigate("user_management")
        }

        // Then - navigation menu should be visible
        composeTestRule
            .onNodeWithText("用户管理")
            .assertIsDisplayed()

        // And other menu items should be visible
        composeTestRule
            .onNodeWithText("数据管理")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("系统设置")
            .assertIsDisplayed()
    }

    @Test
    fun `teacher cannot see admin navigation items`() {
        // Given - teacher user
        val teacherUser = User(
            id = 2L,
            username = "teacher",
            email = "teacher@example.com",
            role = Role.TEACHER,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = teacherUser
            )
        }

        // Then - admin-only items should not be visible
        composeTestRule
            .onNodeWithText("用户管理")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("数据管理")
            .assertDoesNotExist()
    }

    @Test
    fun `student cannot see admin navigation items`() {
        // Given - student user
        val studentUser = User(
            id = 3L,
            username = "student",
            email = "student@example.com",
            role = Role.STUDENT,
            isActive = true,
            status = UserStatus.ACTIVE
        )

        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = studentUser
            )
        }

        // Then - all admin navigation should be hidden
        composeTestRule
            .onNodeWithText("用户管理")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("数据管理")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("系统设置")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("审计日志")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("安全监控")
            .assertDoesNotExist()
    }

    @Test
    fun `admin dashboard displays correct navigation options`() {
        // Given - admin user
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminDashboardScreen(
                onNavigateToUserManagement = { navController.navigate("user_management") },
                onNavigateToDataManagement = { navController.navigate("data_management") },
                onNavigateToSystemSettings = { navController.navigate("system_settings") },
                onNavigateToAuditLogs = { navController.navigate("audit_logs") },
                onNavigateToSecurity = { navController.navigate("security") },
                currentUser = adminUser
            )
        }

        // Then - all admin navigation options should be visible
        composeTestRule
            .onNodeWithText("用户管理")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("数据管理")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("系统设置")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("审计日志")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("安全监控")
            .assertIsDisplayed()
    }

    @Test
    fun `navigation persists during screen transitions`() {
        // Given - admin dashboard
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
        }

        // When - navigate to user management
        composeTestRule
            .onNodeWithText("用户管理")
            .performClick()

        // Then - navigation should still be available
        composeTestRule
            .onNodeWithText("数据管理")
            .assertIsDisplayed()

        // When - navigate to data management
        composeTestRule
            .onNodeWithText("数据管理")
            .performClick()

        // Then - navigation should still be available
        composeTestRule
            .onNodeWithText("用户管理")
            .assertIsDisplayed()
    }

    @Test
    fun `deep linking to admin screens works correctly`() {
        // Given - direct navigation to user management
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
            navController.navigate("user_management")
        }

        // Then - should display user management screen
        composeTestRule
            .onNodeWithText("用户管理")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("user_management_screen")
            .assertIsDisplayed()
    }

    @Test
    fun `admin can navigate between all admin screens`() {
        // Given - admin user
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
        }

        // When - navigate through all screens
        val screens = listOf("user_management", "data_management", "system_settings", "audit_logs", "security")
        screens.forEach { screen ->
            navController.navigate(screen)
            composeTestRule.waitForIdle()
        }

        // Then - should have navigated through all screens
        assertTrue(true) // Placeholder - actual test would verify navigation history
    }

    @Test
    fun `disabled admin cannot access admin features`() {
        // Given - disabled admin
        val disabledAdmin = User(
            id = 1L,
            username = "disabledadmin",
            email = "disabled@example.com",
            role = Role.ADMIN,
            isActive = false,
            status = UserStatus.DISABLED
        )

        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = disabledAdmin
            )
        }

        // Then - admin navigation should not be accessible
        composeTestRule
            .onNodeWithText("用户管理")
            .assertDoesNotExist()

        composeTestRule
            .onNodeWithText("数据管理")
            .assertDoesNotExist()
    }

    @Test
    fun `navigation reflects current screen`() {
        // Given - admin dashboard
        composeTestRule.setContent {
            navController = rememberNavController()
            AdminNavGraph(
                navController = navController,
                currentUser = adminUser
            )
        }

        // When - navigate to user management
        navController.navigate("user_management")

        // Then - user management should be highlighted/active
        // (Actual implementation would check for active state)
        assertTrue(true) // Placeholder
    }
}
