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
                    val dest = if (currentUser?.role == Role.TEACHER) Screen.MAIN_TEACHER else Screen.MAIN_STUDENT
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

        // Assignment screens
        composable(Screen.ASSIGNMENT_LIST) {
            AssignmentListScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAssignmentDetail = { assignmentId ->
                    navController.navigate(createAssignmentDetailRoute(assignmentId))
                }
            )
        }

        composable(
            route = "${Screen.ASSIGNMENT_DETAIL}?${NavArguments.ASSIGNMENT_ID}={${NavArguments.ASSIGNMENT_ID}}",
            arguments = listOf(
                navArgument(NavArguments.ASSIGNMENT_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getLong(NavArguments.ASSIGNMENT_ID)
                ?: throw IllegalArgumentException("Assignment ID is required")
            AssignmentDetailScreen(
                assignmentId = assignmentId,
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
            SubmissionListScreen(
                assignmentId = assignmentId,
                onNavigateBack = { navController.popBackStack() }
            )
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
            SubmitCodeScreen(
                assignmentId = assignmentId,
                onNavigateBack = { navController.popBackStack() },
                onSubmissionSuccess = {
                    navController.navigate(createAssignmentDetailRoute(assignmentId)) {
                        popUpTo(createAssignmentDetailRoute(assignmentId))
                    }
                }
            )
        }

        composable(Screen.SUBMISSIONS_LIST) {
            SubmissionHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
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
            ReportListScreen(
                assignmentId = assignmentId,
                onNavigateBack = { navController.popBackStack() },
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
            ReportDetailScreen(
                reportId = reportId,
                onNavigateBack = { navController.popBackStack() },
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
