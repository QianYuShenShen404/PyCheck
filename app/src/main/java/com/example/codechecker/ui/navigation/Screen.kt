package com.example.codechecker.ui.navigation

/**
 * Screen routes for navigation
 */
object Screen {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val SPLASH = "splash"
    const val HOME_STUDENT = "home_student"
    const val HOME_TEACHER = "home_teacher"
    const val MAIN_STUDENT = "main_student"
    const val MAIN_TEACHER = "main_teacher"
    const val PROFILE_ACCOUNT = "profile_account"
    const val PROFILE_NOTIFICATIONS = "profile_notifications"
    const val PROFILE_SECURITY = "profile_security"
    const val PROFILE_GUIDE = "profile_guide"
    const val ASSIGNMENT_LIST = "assignment_list"
    const val ASSIGNMENT_DETAIL = "assignment_detail"
    const val ASSIGNMENT_CREATE = "assignment_create"
    const val SUBMISSION = "submission"
    const val SUBMISSIONS_LIST = "submissions_list"
    const val PLAGIARISM_REPORT = "plagiarism_report"
    const val PLAGIARISM_REPORT_LIST = "plagiarism_report_list"
    const val CODE_COMPARISON = "code_comparison"
}

/**
 * Navigation arguments
 */
object NavArguments {
    const val ASSIGNMENT_ID = "assignmentId"
    const val REPORT_ID = "reportId"
    const val SUBMISSION_ID = "submissionId"
    const val SIMILARITY_ID = "similarityId"
}

/**
 * Navigation helper functions
 */
fun createAssignmentRoute(assignmentId: Long? = null): String {
    return if (assignmentId != null) {
        "${Screen.ASSIGNMENT_CREATE}?${NavArguments.ASSIGNMENT_ID}=$assignmentId"
    } else {
        Screen.ASSIGNMENT_CREATE
    }
}

fun createAssignmentDetailRoute(assignmentId: Long): String {
    return "${Screen.ASSIGNMENT_DETAIL}?${NavArguments.ASSIGNMENT_ID}=$assignmentId"
}

fun createSubmissionRoute(assignmentId: Long): String {
    return "${Screen.SUBMISSION}?${NavArguments.ASSIGNMENT_ID}=$assignmentId"
}

fun createPlagiarismReportRoute(reportId: Long): String {
    return "${Screen.PLAGIARISM_REPORT}?${NavArguments.REPORT_ID}=$reportId"
}

fun createCodeComparisonRoute(similarityId: Long): String {
    return "${Screen.CODE_COMPARISON}?${NavArguments.SIMILARITY_ID}=$similarityId"
}

fun createReportListRoute(assignmentId: Long): String {
    return "${Screen.PLAGIARISM_REPORT_LIST}?${NavArguments.ASSIGNMENT_ID}=$assignmentId"
}
