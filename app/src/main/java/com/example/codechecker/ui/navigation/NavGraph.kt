package com.example.codechecker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.EntryPointAccessors
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.model.Role
import com.example.codechecker.ui.screens.auth.LoginScreen
import com.example.codechecker.ui.screens.auth.RegisterScreen
import com.example.codechecker.ui.screens.home.StudentHomeScreen
import com.example.codechecker.ui.screens.home.TeacherHomeScreen
import com.example.codechecker.ui.screens.assignment.AssignmentDetailScreen
import com.example.codechecker.ui.screens.assignment.CreateAssignmentScreen
import com.example.codechecker.ui.screens.assignment.AssignmentListScreen
import com.example.codechecker.ui.screens.assignment.SubmissionListScreen
import com.example.codechecker.ui.screens.submission.SubmitCodeScreen
import com.example.codechecker.ui.screens.submission.SubmissionHistoryScreen
import com.example.codechecker.ui.screens.submission.SubmissionDetailScreen
import com.example.codechecker.ui.screens.plagiarism.ReportListScreen
import com.example.codechecker.ui.screens.plagiarism.ReportDetailScreen
import com.example.codechecker.ui.screens.plagiarism.CompareCodeScreen

/**
 * Navigation graph for the app with authentication flow
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier,
    startDestination: String = Screen.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.SPLASH) {
            com.example.codechecker.ui.screens.splash.SplashScreen { dest ->
                navController.navigate(dest) {
                    popUpTo(Screen.SPLASH) { inclusive = true }
                }
            }
        }

        composable(Screen.LOGIN) {
            val context = LocalContext.current
            val userSessionManager = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.example.codechecker.di.UtilityModuleEntryPoint::class.java
                ).userSessionManager()
            }
            val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)

            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.REGISTER) },
                onLoginSuccess = {
                    val role = currentUser?.role
                    val dest = when (role) {
                        Role.ADMIN -> Screen.MAIN_ADMIN
                        Role.TEACHER -> Screen.MAIN_TEACHER
                        Role.STUDENT -> Screen.MAIN_STUDENT
                        else -> Screen.MAIN_STUDENT
                    }
                    navController.navigate(dest) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.REGISTER) {
            RegisterScreen(
                onNavigateToLogin = { navController.popBackStack() },
                onRegistrationSuccess = { navController.popBackStack() }
            )
        }

        // Home screens
        composable(Screen.MAIN_STUDENT) {
            com.example.codechecker.ui.screens.main.MainStudentScreen(
                onNavigateToSubmissionHistory = { navController.navigate(Screen.SUBMISSIONS_LIST) },
                onNavigateToAssignmentDetail = { assignmentId ->
                    navController.navigate(createAssignmentDetailRoute(assignmentId))
                },
                onLogout = { navController.navigate(Screen.LOGIN) },
                onSwitchAccount = { navController.navigate(Screen.LOGIN) },
                onNavigateToAccount = { navController.navigate(Screen.PROFILE_ACCOUNT) },
                onNavigateToNotifications = { navController.navigate(Screen.PROFILE_NOTIFICATIONS) },
                onNavigateToSecurity = { navController.navigate(Screen.PROFILE_SECURITY) },
                onNavigateToGuide = { navController.navigate(Screen.PROFILE_GUIDE) }
            )
        }

        composable(Screen.MAIN_TEACHER) {
            com.example.codechecker.ui.screens.main.MainTeacherScreen(
                onNavigateToCreateAssignment = { navController.navigate(Screen.ASSIGNMENT_CREATE) },
                onNavigateToAssignmentList = { navController.navigate(Screen.ASSIGNMENT_LIST) },
                onNavigateToAssignmentDetail = { assignmentId ->
                    navController.navigate(createAssignmentDetailRoute(assignmentId))
                },
                onLogout = { navController.navigate(Screen.LOGIN) },
                onSwitchAccount = { navController.navigate(Screen.LOGIN) },
                onNavigateToAccount = { navController.navigate(Screen.PROFILE_ACCOUNT) },
                onNavigateToNotifications = { navController.navigate(Screen.PROFILE_NOTIFICATIONS) },
                onNavigateToSecurity = { navController.navigate(Screen.PROFILE_SECURITY) },
                onNavigateToGuide = { navController.navigate(Screen.PROFILE_GUIDE) }
            )
        }

        composable(Screen.MAIN_ADMIN) {
            com.example.codechecker.ui.screens.admin.AdminDashboardScreen(
                onNavigateToUserManagement = { navController.navigate(Screen.ADMIN_USER_MANAGEMENT) },
                onNavigateToDataManagement = { navController.navigate(Screen.ADMIN_DATA_MANAGEMENT) },
                onNavigateToSystemSettings = { navController.navigate(Screen.ADMIN_SYSTEM_SETTINGS) },
                onNavigateToAuditLogs = { navController.navigate(Screen.ADMIN_AUDIT_LOGS) },
                onNavigateToSecurity = { navController.navigate(Screen.ADMIN_SECURITY) },
                onLogout = { navController.navigate(Screen.LOGIN) }
            )
        }

        // Admin screens
        composable(Screen.ADMIN_USER_MANAGEMENT) {
            com.example.codechecker.ui.screens.admin.UserManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ADMIN_DATA_MANAGEMENT) {
            com.example.codechecker.ui.screens.admin.DataManagementScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ADMIN_SYSTEM_SETTINGS) {
            com.example.codechecker.ui.screens.admin.SystemSettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ADMIN_AUDIT_LOGS) {
            com.example.codechecker.ui.screens.admin.AuditLogsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ADMIN_SECURITY) {
            com.example.codechecker.ui.screens.admin.SecurityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Assignment screens
        composable(Screen.ASSIGNMENT_LIST) {
            val context = LocalContext.current
            val userSessionManager = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.example.codechecker.di.UtilityModuleEntryPoint::class.java
                ).userSessionManager()
            }
            val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
            AssignmentListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    val dest = when (currentUser?.role) {
                        Role.ADMIN -> Screen.MAIN_ADMIN
                        Role.TEACHER -> Screen.MAIN_TEACHER
                        Role.STUDENT -> Screen.MAIN_STUDENT
                        else -> Screen.MAIN_STUDENT
                    }
                    navController.navigate(dest)
                },
                onNavigateToAssignmentDetail = { assignmentId ->
                    navController.navigate(createAssignmentDetailRoute(assignmentId))
                }
            )
        }

        composable(
            route = "${Screen.ASSIGNMENT_DETAIL}?${NavArguments.ASSIGNMENT_ID}={${NavArguments.ASSIGNMENT_ID}}&fromSubmission={fromSubmission}",
            arguments = listOf(
                navArgument(NavArguments.ASSIGNMENT_ID) { type = NavType.LongType },
                navArgument("fromSubmission") { type = NavType.BoolType; defaultValue = false }
            )
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getLong(NavArguments.ASSIGNMENT_ID)
                ?: throw IllegalArgumentException("Assignment ID is required")
            val fromSubmission = backStackEntry.arguments?.getBoolean("fromSubmission") ?: false
            val context = LocalContext.current
            val userSessionManager = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.example.codechecker.di.UtilityModuleEntryPoint::class.java
                ).userSessionManager()
            }
            val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
            AssignmentDetailScreen(
                assignmentId = assignmentId,
                fromSubmission = fromSubmission,
                onNavigateHome = {
                    val dest = when (currentUser?.role) {
                        Role.ADMIN -> Screen.MAIN_ADMIN
                        Role.TEACHER -> Screen.MAIN_TEACHER
                        Role.STUDENT -> Screen.MAIN_STUDENT
                        else -> Screen.MAIN_STUDENT
                    }
                    navController.navigate(dest)
                },
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSubmission = { assignmentId ->
                    navController.navigate(createSubmissionRoute(assignmentId))
                },
                onNavigateToSubmissionList = { assignmentId ->
                    navController.navigate("submission_list?assignmentId=$assignmentId")
                },
                onNavigateToReportList = { assignmentId ->
                    navController.navigate(createReportListRoute(assignmentId))
                },
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(createPlagiarismReportRoute(reportId))
                }
            )
        }

        composable(
            route = "${Screen.ASSIGNMENT_CREATE}?${NavArguments.ASSIGNMENT_ID}={${NavArguments.ASSIGNMENT_ID}}"
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getLong(NavArguments.ASSIGNMENT_ID)
            CreateAssignmentScreen(
                onNavigateBack = { navController.popBackStack() },
                onAssignmentCreated = { createdId ->
                    navController.navigate(createAssignmentDetailRoute(createdId)) {
                        popUpTo(Screen.ASSIGNMENT_CREATE) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "submission_list?assignmentId={assignmentId}",
            arguments = listOf(
                navArgument("assignmentId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getLong("assignmentId")
                ?: throw IllegalArgumentException("Assignment ID is required")
            val context = LocalContext.current
            val userSessionManager = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.example.codechecker.di.UtilityModuleEntryPoint::class.java
                ).userSessionManager()
            }
            val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
            val role = currentUser?.role
            if (role == Role.TEACHER || role == Role.ADMIN) {
                SubmissionListScreen(
                    assignmentId = assignmentId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateHome = {
                        val dest = when (currentUser?.role) {
                            Role.ADMIN -> Screen.MAIN_ADMIN
                            Role.TEACHER -> Screen.MAIN_TEACHER
                            Role.STUDENT -> Screen.MAIN_STUDENT
                            else -> Screen.MAIN_STUDENT
                        }
                        navController.navigate(dest)
                    },
                    onNavigateToSubmissionDetail = { sid ->
                        navController.navigate(createSubmissionDetailRoute(sid))
                    }
                )
            } else {
                SubmissionHistoryScreen(
                    assignmentId = assignmentId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateHome = {
                        val dest = when (currentUser?.role) {
                            Role.ADMIN -> Screen.MAIN_ADMIN
                            Role.TEACHER -> Screen.MAIN_TEACHER
                            Role.STUDENT -> Screen.MAIN_STUDENT
                            else -> Screen.MAIN_STUDENT
                        }
                        navController.navigate(dest)
                    },
                    onNavigateToSubmissionDetail = { sid ->
                        navController.navigate(createSubmissionDetailRoute(sid))
                    }
                )
            }
        }

        // Submission screens
        composable(
            route = "${Screen.SUBMISSION}?${NavArguments.ASSIGNMENT_ID}={${NavArguments.ASSIGNMENT_ID}}",
            arguments = listOf(
                navArgument(NavArguments.ASSIGNMENT_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getLong(NavArguments.ASSIGNMENT_ID)
                ?: throw IllegalArgumentException("Assignment ID is required")
            val context = LocalContext.current
            val userSessionManager = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.example.codechecker.di.UtilityModuleEntryPoint::class.java
                ).userSessionManager()
            }
            val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
            SubmitCodeScreen(
                assignmentId = assignmentId,
                onNavigateBack = { navController.popBackStack() },
                onSubmissionSuccess = {
                    navController.navigate(createAssignmentDetailRoute(assignmentId, fromSubmission = true)) {
                        popUpTo(createAssignmentDetailRoute(assignmentId, fromSubmission = true))
                    }
                },
                onNavigateHome = {
                    val dest = when (currentUser?.role) {
                        Role.ADMIN -> Screen.MAIN_ADMIN
                        Role.TEACHER -> Screen.MAIN_TEACHER
                        Role.STUDENT -> Screen.MAIN_STUDENT
                        else -> Screen.MAIN_STUDENT
                    }
                    navController.navigate(dest)
                }
            )
        }

        composable(Screen.SUBMISSIONS_LIST) {
            val context = LocalContext.current
            val userSessionManager = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.example.codechecker.di.UtilityModuleEntryPoint::class.java
                ).userSessionManager()
            }
            val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
            SubmissionHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    val dest = when (currentUser?.role) {
                        Role.ADMIN -> Screen.MAIN_ADMIN
                        Role.TEACHER -> Screen.MAIN_TEACHER
                        Role.STUDENT -> Screen.MAIN_STUDENT
                        else -> Screen.MAIN_STUDENT
                    }
                    navController.navigate(dest)
                },
                onNavigateToSubmissionDetail = { sid ->
                    navController.navigate(createSubmissionDetailRoute(sid))
                }
            )
        }

        composable(
            route = "${Screen.SUBMISSION_DETAIL}?${NavArguments.SUBMISSION_ID}={${NavArguments.SUBMISSION_ID}}",
            arguments = listOf(
                navArgument(NavArguments.SUBMISSION_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val submissionId = backStackEntry.arguments?.getLong(NavArguments.SUBMISSION_ID)
                ?: throw IllegalArgumentException("Submission ID is required")
            val context = LocalContext.current
            val userSessionManager = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.example.codechecker.di.UtilityModuleEntryPoint::class.java
                ).userSessionManager()
            }
            val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
            SubmissionDetailScreen(
                submissionId = submissionId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    val dest = when (currentUser?.role) {
                        Role.ADMIN -> Screen.MAIN_ADMIN
                        Role.TEACHER -> Screen.MAIN_TEACHER
                        Role.STUDENT -> Screen.MAIN_STUDENT
                        else -> Screen.MAIN_STUDENT
                    }
                    navController.navigate(dest)
                }
            )
        }

        // Report screens
        composable(
            route = "${Screen.PLAGIARISM_REPORT_LIST}?${NavArguments.ASSIGNMENT_ID}={${NavArguments.ASSIGNMENT_ID}}",
            arguments = listOf(
                navArgument(NavArguments.ASSIGNMENT_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getLong(NavArguments.ASSIGNMENT_ID)
            val context = LocalContext.current
            val userSessionManager = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.example.codechecker.di.UtilityModuleEntryPoint::class.java
                ).userSessionManager()
            }
            val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
            ReportListScreen(
                assignmentId = assignmentId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    val dest = when (currentUser?.role) {
                        Role.ADMIN -> Screen.MAIN_ADMIN
                        Role.TEACHER -> Screen.MAIN_TEACHER
                        Role.STUDENT -> Screen.MAIN_STUDENT
                        else -> Screen.MAIN_STUDENT
                    }
                    navController.navigate(dest)
                },
                onNavigateToReportDetail = { reportId ->
                    navController.navigate(createPlagiarismReportRoute(reportId))
                }
            )
        }
        composable(
            route = "${Screen.PLAGIARISM_REPORT}?${NavArguments.REPORT_ID}={${NavArguments.REPORT_ID}}",
            arguments = listOf(
                navArgument(NavArguments.REPORT_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getLong(NavArguments.REPORT_ID)
                ?: throw IllegalArgumentException("Report ID is required")
            val context = LocalContext.current
            val userSessionManager = remember {
                EntryPointAccessors.fromApplication(
                    context.applicationContext,
                    com.example.codechecker.di.UtilityModuleEntryPoint::class.java
                ).userSessionManager()
            }
            val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
            ReportDetailScreen(
                reportId = reportId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    val dest = when (currentUser?.role) {
                        Role.ADMIN -> Screen.MAIN_ADMIN
                        Role.TEACHER -> Screen.MAIN_TEACHER
                        Role.STUDENT -> Screen.MAIN_STUDENT
                        else -> Screen.MAIN_STUDENT
                    }
                    navController.navigate(dest)
                },
                onNavigateToCodeComparison = { similarityId ->
                    navController.navigate(createCodeComparisonRoute(similarityId))
                }
            )
        }

        // Profile sub-pages
        composable(Screen.PROFILE_ACCOUNT) {
            com.example.codechecker.ui.screens.profile.AccountInfoScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PROFILE_NOTIFICATIONS) {
            com.example.codechecker.ui.screens.profile.NotificationsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PROFILE_SECURITY) {
            com.example.codechecker.ui.screens.profile.SecurityScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.PROFILE_GUIDE) {
            com.example.codechecker.ui.screens.profile.GuideScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Screen.CODE_COMPARISON}?${NavArguments.SIMILARITY_ID}={${NavArguments.SIMILARITY_ID}}",
            arguments = listOf(
                navArgument(NavArguments.SIMILARITY_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val similarityId = backStackEntry.arguments?.getLong(NavArguments.SIMILARITY_ID)
                ?: throw IllegalArgumentException("Similarity ID is required")
            CompareCodeScreen(
                similarityId = similarityId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
