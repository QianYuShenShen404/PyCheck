# Jetpack Compose UI开发指南: CodeChecker Android项目

**Date**: 2025-11-27
**Feature**: CodeChecker Android应用 - Python代码查重助手
**Reference**: tasks.md (T010-T011, T012-T017, T018-T023, T034-T035, T037-T040)
**Purpose**: 提供完整的Jetpack Compose UI开发指南和最佳实践

---

## 概述

Jetpack Compose是Android的现代UI工具包，采用声明式编程模型。本指南提供CodeChecker项目的完整Compose UI实现，包括主题系统、导航、常用组件和页面实现。

### UI项目结构

```
app/src/main/java/com/example/codechecker/ui/
├── theme/                    # 主题系统
│   ├── Color.kt              # 颜色定义
│   ├── Theme.kt              # 主题配置
│   └── Type.kt               # 字体样式
├── components/               # 可复用UI组件
│   ├── CodeHighlightView.kt  # 代码高亮组件
│   ├── SimilarityChart.kt    # 相似度图表组件
│   ├── ProgressIndicator.kt  # 进度指示器
│   └── EmptyState.kt         # 空状态组件
├── screens/                  # 页面
│   ├── auth/                 # 认证相关页面
│   ├── home/                 # 主页
│   ├── assignment/           # 作业相关页面
│   ├── submission/           # 提交相关页面
│   ├── plagiarism/           # 查重相关页面
│   └── settings/             # 设置页面
└── navigation/               # 导航配置
    ├── NavGraph.kt           # 导航图
    └── Screen.kt             # 路由定义
```

---

## 1. 主题系统 (Theme System)

### 1.1 Color.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/theme/Color.kt`

```kotlin
package com.example.codechecker.ui.theme

import androidx.compose.ui.graphics.Color

// =========================
// 品牌色彩 (Brand Colors)
// =========================

val Primary = Color(0xFF2196F3)           // 主色调 - 蓝色
val PrimaryVariant = Color(0xFF1976D2)    // 主色调变体 - 深蓝色
val Secondary = Color(0xFF03DAC6)         // 辅助色 - 青色
val SecondaryVariant = Color(0xFF018786)  // 辅助色变体 - 深青色

// =========================
// 功能色彩 (Functional Colors)
// =========================

val Background = Color(0xFFFAFAFA)        // 背景色 - 浅灰
val Surface = Color(0xFFFFFFFF)           // 表面色 - 白色
val Error = Color(0xFFB00020)             // 错误色 - 红色
val Success = Color(0xFF4CAF50)           // 成功色 - 绿色
val Warning = Color(0xFFFF9800)           // 警告色 - 橙色
val Info = Color(0xFF2196F3)              // 信息色 - 蓝色

// =========================
// 文本色彩 (Text Colors)
// =========================

val OnPrimary = Color(0xFFFFFFFF)         // 主色上的文本 - 白色
val OnSecondary = Color(0xFF000000)       // 辅助色上的文本 - 黑色
val OnBackground = Color(0xFF000000)      // 背景色上的文本 - 黑色
val OnSurface = Color(0xFF000000)         // 表面色上的文本 - 黑色
val OnError = Color(0xFFFFFFFF)           // 错误色上的文本 - 白色

// =========================
// 状态色彩 (Status Colors)
// =========================

// 相似度风险等级色彩
val HighRisk = Color(0xFFD32F2F)          // 高风险 - 深红
val MediumRisk = Color(0xFFFF9800)        // 中风险 - 橙色
val LowRisk = Color(0xFF4CAF50)           // 低风险 - 绿色

// 相似度百分比色彩
val Similarity0to20 = Color(0xFFE3F2FD)   // 0-20% - 浅蓝
val Similarity20to40 = Color(0xFFFFECB3)  // 20-40% - 浅橙
val Similarity40to60 = Color(0xFFFFF9C4)  // 40-60% - 浅黄
val Similarity60to80 = Color(0xFFFFCC80)  // 60-80% - 中橙
val Similarity80to100 = Color(0xFFFF7043) // 80-100% - 深橙

// =========================
// 语义化色彩 (Semantic Colors)
// =========================

val CodeBackground = Color(0xFFF5F5F5)    // 代码背景 - 浅灰
val CodeKeyword = Color(0xFF9C27B0)       // 代码关键字 - 紫色
val CodeString = Color(0xFF43A047)        // 代码字符串 - 绿色
val CodeComment = Color(0xFF757575)       // 代码注释 - 灰色

// =========================
// 材质3色彩映射 (Material 3 Color Scheme)
// =========================

/**
 * 创建浅色主题色彩方案
 */
fun lightColorScheme() = androidx.compose.material3.ColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    onPrimaryContainer = OnPrimary,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryVariant,
    onSecondaryContainer = OnSecondary,
    tertiary = Primary,
    onTertiary = OnPrimary,
    tertiaryContainer = PrimaryVariant,
    onTertiaryContainer = OnPrimary,
    error = Error,
    errorContainer = Color(0xFFFFDAD4),
    onError = OnError,
    onErrorContainer = Color(0xFF410002),
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF424242),
    outline = Color(0xFF757575),
    inverseOnSurface = OnSurface,
    inverseSurface = Color(0xFF303030),
    inversePrimary = OnPrimary,
    surfaceTint = Primary,
    outlineVariant = Color(0xFFBDBDBD),
    scrim = Color(0xFF000000)
)

/**
 * 创建深色主题色彩方案
 */
fun darkColorScheme() = androidx.compose.material3.ColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF003258),
    primaryContainer = Color(0xFF004A77),
    onPrimaryContainer = Color(0xFFE1F5FE),
    secondary = Color(0xFF03DAC6),
    onSecondary = Color(0xFF003A37),
    secondaryContainer = Color(0xFF005046),
    onSecondaryContainer = Color(0xFF78F7EB),
    tertiary = Color(0xFF90CAF9),
    onTertiary = Color(0xFF003258),
    tertiaryContainer = Color(0xFF004A77),
    onTertiaryContainer = Color(0xFFE1F5FE),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE1E1E1),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE1E1E1),
    surfaceVariant = Color(0xFF424242),
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF9E9E9E),
    inverseOnSurface = Color(0xFF121212),
    inverseSurface = Color(0xFFE1E1E1),
    inversePrimary = Primary,
    surfaceTint = Primary,
    outlineVariant = Color(0xFF757575),
    scrim = Color(0xFF000000)
)
```

### 1.2 Type.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/theme/Type.kt`

```kotlin
package com.example.codechecker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 字体样式定义
 */
val Typography = Typography(
    // 标题样式
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),

    // 标题样式
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // 正文样式
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // 按钮样式
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### 1.3 Theme.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/theme/Theme.kt`

```kotlin
package com.example.codechecker.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * 深色主题和浅色主题切换
 */
private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

/**
 * CodeChecker主题
 * @param darkTheme 是否使用深色主题
 */
@Composable
fun CodeCheckerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // 动态颜色（Android 12+）
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColors
        dynamicColor -> {
            // TODO: 实现动态颜色支持
            LightColors
        }
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

---

## 2. 导航配置 (Navigation)

### 2.1 Screen.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/navigation/Screen.kt`

```kotlin
package com.example.codechecker.ui.navigation

/**
 * 屏幕路由定义
 */
object Screen {
    // 认证相关
    const val LOGIN = "login"
    const val REGISTER = "register"

    // 主页相关
    const val HOME = "home"
    const val STUDENT_HOME = "student_home"
    const val TEACHER_HOME = "teacher_home"

    // 作业相关
    const val ASSIGNMENT_LIST = "assignment_list"
    const val ASSIGNMENT_DETAIL = "assignment_detail/{assignmentId}"
    const val CREATE_ASSIGNMENT = "create_assignment"

    // 提交相关
    const val SUBMISSION_LIST = "submission_list"
    const val SUBMISSION_DETAIL = "submission_detail/{submissionId}"
    const val SUBMIT_CODE = "submit_code"

    // 查重相关
    const val REPORT_LIST = "report_list"
    const val REPORT_DETAIL = "report_detail/{reportId}"
    const val COMPARE_CODE = "compare_code/{similarityId}"

    // 设置
    const val SETTINGS = "settings"
}

/**
 * 导航参数类
 */
object NavArgs {
    const val ASSIGNMENT_ID = "assignmentId"
    const val SUBMISSION_ID = "submissionId"
    const val REPORT_ID = "reportId"
    const val SIMILARITY_ID = "similarityId"
}

/**
 * 导航路径生成器
 */
object NavPath {
    fun assignmentDetail(assignmentId: Long) = "assignment_detail/$assignmentId"
    fun submissionDetail(submissionId: Long) = "submission_detail/$submissionId"
    fun reportDetail(reportId: Long) = "report_detail/$reportId"
    fun compareCode(similarityId: Long) = "compare_code/$similarityId"
}
```

### 2.2 NavGraph.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/navigation/NavGraph.kt`

```kotlin
package com.example.codechecker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.codechecker.ui.screens.auth.LoginScreen
import com.example.codechecker.ui.screens.auth.RegisterScreen
import com.example.codechecker.ui.screens.home.StudentHomeScreen
import com.example.codechecker.ui.screens.home.TeacherHomeScreen
import com.example.codechecker.ui.screens.plagiarism.ReportListScreen
import com.example.codechecker.ui.screens.plagiarism.ReportDetailScreen
import com.example.codechecker.ui.screens.plagiarism.CompareCodeScreen
import com.example.codechecker.ui.screens.assignment.AssignmentListScreen
import com.example.codechecker.ui.screens.assignment.AssignmentDetailScreen
import com.example.codechecker.ui.screens.assignment.CreateAssignmentScreen
import com.example.codechecker.ui.screens.submission.SubmissionListScreen
import com.example.codechecker.ui.screens.submission.SubmitCodeScreen

/**
 * 导航图配置
 * @param navController 导航控制器
 * @param startDestination 起始目标屏幕
 */
@Composable
fun CodeCheckerNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // 认证相关
        composable(Screen.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.HOME) {
                        popUpTo(Screen.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.REGISTER)
                }
            )
        }

        composable(Screen.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.LOGIN) {
                        popUpTo(Screen.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // 主页相关
        composable(Screen.HOME) {
            HomeScreen(
                onNavigateToStudentHome = {
                    navController.navigate(Screen.STUDENT_HOME) {
                        popUpTo(Screen.HOME) { inclusive = true }
                    }
                },
                onNavigateToTeacherHome = {
                    navController.navigate(Screen.TEACHER_HOME) {
                        popUpTo(Screen.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.STUDENT_HOME) {
            StudentHomeScreen(
                onNavigateToAssignmentList = {
                    navController.navigate(Screen.ASSIGNMENT_LIST)
                },
                onNavigateToSubmissionList = {
                    navController.navigate(Screen.SUBMISSION_LIST)
                },
                onNavigateToReportList = {
                    navController.navigate(Screen.REPORT_LIST)
                }
            )
        }

        composable(Screen.TEACHER_HOME) {
            TeacherHomeScreen(
                onNavigateToCreateAssignment = {
                    navController.navigate(Screen.CREATE_ASSIGNMENT)
                },
                onNavigateToAssignmentList = {
                    navController.navigate(Screen.ASSIGNMENT_LIST)
                },
                onNavigateToReportList = {
                    navController.navigate(Screen.REPORT_LIST)
                }
            )
        }

        // 作业相关
        composable(Screen.ASSIGNMENT_LIST) {
            AssignmentListScreen(
                onAssignmentClick = { assignmentId ->
                    navController.navigate(NavPath.assignmentDetail(assignmentId))
                }
            )
        }

        composable(
            route = Screen.ASSIGNMENT_DETAIL,
            arguments = listOf(
                androidx.navigation.navArgument(NavArgs.ASSIGNMENT_ID) {
                    type = androidx.navigation.NavType.LongType
                }
            )
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getLong(NavArgs.ASSIGNMENT_ID) ?: 0L
            AssignmentDetailScreen(
                assignmentId = assignmentId,
                onNavigateToSubmitCode = {
                    navController.navigate(Screen.SUBMIT_CODE)
                }
            )
        }

        composable(Screen.CREATE_ASSIGNMENT) {
            CreateAssignmentScreen(
                onAssignmentCreated = {
                    navController.popBackStack()
                }
            )
        }

        // 提交相关
        composable(Screen.SUBMISSION_LIST) {
            SubmissionListScreen()
        }

        composable(Screen.SUBMIT_CODE) {
            SubmitCodeScreen(
                onSubmissionSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // 查重相关
        composable(Screen.REPORT_LIST) {
            ReportListScreen(
                onReportClick = { reportId ->
                    navController.navigate(NavPath.reportDetail(reportId))
                }
            )
        }

        composable(
            route = Screen.REPORT_DETAIL,
            arguments = listOf(
                androidx.navigation.navArgument(NavArgs.REPORT_ID) {
                    type = androidx.navigation.NavType.LongType
                }
            )
        ) { backStackEntry ->
            val reportId = backStackEntry.arguments?.getLong(NavArgs.REPORT_ID) ?: 0L
            ReportDetailScreen(
                reportId = reportId,
                onCompareCodeClick = { similarityId ->
                    navController.navigate(NavPath.compareCode(similarityId))
                }
            )
        }

        composable(
            route = Screen.COMPARE_CODE,
            arguments = listOf(
                androidx.navigation.navArgument(NavArgs.SIMILARITY_ID) {
                    type = androidx.navigation.NavType.LongType
                }
            )
        ) { backStackEntry ->
            val similarityId = backStackEntry.arguments?.getLong(NavArgs.SIMILARITY_ID) ?: 0L
            CompareCodeScreen(
                similarityId = similarityId
            )
        }
    }
}
```

---

## 3. 通用UI组件 (Common Components)

### 3.1 ProgressIndicator.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/components/ProgressIndicator.kt`

```kotlin
package com.example.codechecker.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 自定义圆形进度指示器
 * @param progress 进度值 (0-100)
 * @param modifier 修饰符
 * @param size 指示器大小
 */
@Composable
fun CircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Int = 80,
    text: String? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
            )
    ) {
        // 使用Canvas绘制进度圆环
        val infiniteTransition = rememberInfiniteTransition(label = "infinite")
        val angle by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ), label = "angle"
        )

        // 进度圆环
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .size((size * 0.7).dp)
                .rotate(angle)
        ) {
            // TODO: 绘制圆环进度
        }

        if (text != null) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * 线性进度条
 * @param progress 进度值 (0-100)
 * @param modifier 修饰符
 * @param height 进度条高度
 */
@Composable
fun LinearProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Int = 8
) {
    Box(
        modifier = modifier
            .height(height.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = androidx.compose.material3.MaterialTheme.shapes.small
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress / 100f)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = androidx.compose.material3.MaterialTheme.shapes.small
                )
        )
    }
}

/**
 * 查重进度组件
 * @param current 当前进度
 * @param total 总进度
 * @param modifier 修饰符
 */
@Composable
fun PlagiarismProgressIndicator(
    current: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (total > 0) {
        (current.toFloat() / total * 100f)
    } else {
        0f
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "查重进度: $current / $total",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = String.format("%.1f%%", progress),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.End)
        )
    }
}
```

### 3.2 EmptyState.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/components/EmptyState.kt`

```kotlin
package com.example.codechecker.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 空状态组件
 * @param icon 图标资源ID
 * @param title 标题文本
 * @param description 描述文本
 * @param modifier 修饰符
 * @param buttonText 按钮文本（可选）
 * @param onButtonClick 按钮点击事件（可选）
 */
@Composable
fun EmptyState(
    icon: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        // 调整图标颜色为50%透明度
        val colorFilter = ColorFilter.colorMatrix(
            ColorMatrix().apply {
                setToSaturation(0f) // 去色
            }
        )

        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .let { if (buttonText != null) it else it.padding(top = 48.dp) },
            colorFilter = colorFilter,
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (buttonText != null && onButtonClick != null) {
            Spacer(modifier = Modifier.height(24.dp))

            androidx.compose.material3.Button(
                onClick = onButtonClick
            ) {
                Text(buttonText)
            }
        }
    }
}

/**
 * 预设的空状态组件
 */
object EmptyStates {
    @Composable
    fun NoAssignments(
        onCreateAssignment: () -> Unit
    ) {
        EmptyState(
            icon = R.drawable.ic_assignment, // 需要创建图标资源
            title = "暂无作业",
            description = "创建您的第一个作业，开始使用CodeChecker",
            buttonText = "创建作业",
            onButtonClick = onCreateAssignment
        )
    }

    @Composable
    fun NoSubmissions() {
        EmptyState(
            icon = R.drawable.ic_upload, // 需要创建图标资源
            title = "暂无提交",
            description = "提交您的Python代码文件"
        )
    }

    @Composable
    fun NoReports() {
        EmptyState(
            icon = R.drawable.ic_report, // 需要创建图标资源
            title = "暂无报告",
            description = "执行查重后查看相似度报告"
        )
    }

    @Composable
    fun SearchEmpty() {
        EmptyState(
            icon = R.drawable.ic_search, // 需要创建图标资源
            title = "未找到结果",
            description = "请尝试修改搜索条件"
        )
    }
}
```

---

## 4. 页面实现示例 (Screen Implementation)

### 4.1 LoginScreen.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/screens/auth/LoginScreen.kt`

```kotlin
package com.example.codechecker.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codechecker.ui.theme.CodeCheckerTheme

/**
 * 登录页面
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    // 观察登录状态
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    CodeCheckerTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            LoginContent(
                uiState = uiState,
                onUsernameChange = viewModel::updateUsername,
                onPasswordChange = viewModel::updatePassword,
                onLoginClick = viewModel::login,
                onNavigateToRegister = onNavigateToRegister,
                focusManager = focusManager
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    focusManager: androidx.compose.ui.platform.FocusManager
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo和标题
        Text(
            text = "CodeChecker",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Python代码查重助手",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 用户名输入框
        OutlinedTextField(
            value = uiState.username,
            onValueChange = onUsernameChange,
            label = { Text("用户名") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            isError = uiState.error != null,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 密码输入框
        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text("密码") },
            singleLine = true,
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onLoginClick()
                }
            ),
            trailingIcon = {
                // 密码可见性切换按钮
                // TODO: 添加图标
            },
            isError = uiState.error != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 登录按钮
        Button(
            onClick = onLoginClick,
            enabled = !uiState.isLoading && uiState.username.isNotBlank() && uiState.password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("登录")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 注册链接
        TextButton(
            onClick = onNavigateToRegister
        ) {
            Text("没有账户？立即注册")
        }
    }
}
```

### 4.2 StudentHomeScreen.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/screens/home/StudentHomeScreen.kt`

```kotlin
package com.example.codechecker.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codechecker.domain.model.Assignment

/**
 * 学生主页
 */
@Composable
fun StudentHomeScreen(
    onNavigateToAssignmentList: () -> Unit,
    onNavigateToSubmissionList: () -> Unit,
    onNavigateToReportList: () -> Unit,
    viewModel: StudentHomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAssignments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CodeChecker") },
                actions = {
                    IconButton(onClick = viewModel::logout) {
                        // 登出图标
                        // Icon(painter = painterResource(R.drawable.ic_logout), contentDescription = "登出")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            StudentHomeContent(
                uiState = uiState,
                onNavigateToAssignmentList = onNavigateToAssignmentList,
                onNavigateToSubmissionList = onNavigateToSubmissionList,
                onNavigateToReportList = onNavigateToReportList,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
fun StudentHomeContent(
    uiState: StudentHomeUiState,
    onNavigateToAssignmentList: () -> Unit,
    onNavigateToSubmissionList: () -> Unit,
    onNavigateToReportList: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 用户信息卡片
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "欢迎，${uiState.user?.displayName ?: "学生"}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "角色: 学生",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 快捷操作
        Text(
            text = "快捷操作",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                QuickActionCard(
                    title = "查看作业",
                    description = "浏览可用作业并提交代码",
                    onClick = onNavigateToAssignmentList
                )
            }

            item {
                QuickActionCard(
                    title = "我的提交",
                    description = "查看代码提交历史",
                    onClick = onNavigateToSubmissionList
                )
            }

            item {
                QuickActionCard(
                    title = "查重报告",
                    description = "查看代码相似度报告",
                    onClick = onNavigateToReportList
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 最近作业
        Text(
            text = "最近作业",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.recentAssignments.isEmpty()) {
            Text(
                text = "暂无最近作业",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.recentAssignments) { assignment ->
                    AssignmentItem(
                        assignment = assignment,
                        onClick = { /* TODO: 导航到作业详情 */ }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickActionCard(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AssignmentItem(
    assignment: Assignment,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = assignment.title,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "截止日期: ${assignment.deadline?.let { java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(it)) } ?: "无"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

### 4.3 ReportDetailScreen.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/screens/plagiarism/ReportDetailScreen.kt`

```kotlin
package com.example.codechecker.ui.screens.plagiarism

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.codechecker.ui.components.SimilarityChart

/**
 * 报告详情页面
 */
@Composable
fun ReportDetailScreen(
    reportId: Long,
    onCompareCodeClick: (Long) -> Unit,
    viewModel: ReportDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(reportId) {
        viewModel.loadReport(reportId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("报告详情") },
                navigationIcon = {
                    // 返回按钮
                    // IconButton(onClick = /* TODO: 处理返回 */) {
                    //     Icon(painter = painterResource(R.drawable.ic_arrow_back), contentDescription = "返回")
                    // }
                }
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.report != null) {
            ReportDetailContent(
                uiState = uiState,
                onCompareCodeClick = onCompareCodeClick,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            Text(
                text = "报告不存在",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ReportDetailContent(
    uiState: ReportDetailUiState,
    onCompareCodeClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 报告概要信息
        item {
            ReportSummaryCard(
                report = uiState.report!!
            )
        }

        // 相似度分布图表
        item {
            Text(
                text = "相似度分布",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            SimilarityChart(
                similarityDistribution = uiState.similarityDistribution,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        // 高相似度警告
        item {
            Text(
                text = "高相似度警告 (>60%)",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.highSimilarityPairs.isEmpty()) {
                Text(
                    text = "未发现高相似度代码",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                uiState.highSimilarityPairs.forEach { pair ->
                    SimilarityWarningCard(
                        similarity = pair,
                        onClick = { onCompareCodeClick(pair.id) }
                    )
                }
            }
        }

        // 所有比对结果
        item {
            Text(
                text = "所有比对结果",
                style = MaterialTheme.typography.titleLarge
            )
        }

        items(uiState.allSimilarityPairs) { similarity ->
            SimilarityListItem(
                similarity = similarity,
                onClick = { onCompareCodeClick(similarity.id) }
            )
        }
    }
}

@Composable
fun ReportSummaryCard(
    report: com.example.codechecker.domain.model.Report
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "报告概要",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SummaryItem("总提交数", report.totalSubmissions.toString())
                SummaryItem("比对对数", report.totalPairs.toString())
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val status = when (report.status) {
                    com.example.codechecker.domain.model.ReportStatus.PENDING -> "进行中"
                    com.example.codechecker.domain.model.ReportStatus.COMPLETED -> "已完成"
                    com.example.codechecker.domain.model.ReportStatus.FAILED -> "失败"
                }
                SummaryItem("状态", status)
                SummaryItem(
                    "完成时间",
                    report.completedAt?.let {
                        java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").format(java.util.Date(it))
                    } ?: "-"
                )
            }
        }
    }
}

@Composable
fun SummaryItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun SimilarityWarningCard(
    similarity: com.example.codechecker.domain.model.Similarity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "相似度: ${String.format("%.1f%%", similarity.similarityScore)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )

                Text(
                    text = similarity.getRiskLevel() + "风险",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${similarity.submission1FileName} vs ${similarity.submission2FileName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
fun SimilarityListItem(
    similarity: com.example.codechecker.domain.model.Similarity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${similarity.submission1FileName} ↔ ${similarity.submission2FileName}",
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Jaccard: ${String.format("%.1f", similarity.jaccardScore)}% | LCS: ${String.format("%.1f", similarity.lcsScore)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = String.format("%.1f%%", similarity.similarityScore),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
```

---

## 5. 代码高亮组件 (Code Highlight View)

### 5.1 CodeHighlightView.kt

**文件**: `app/src/main/java/com/example/codechecker/ui/components/CodeHighlightView.kt`

```kotlin
package com.example.codechecker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codechecker.ui.theme.CodeBackground
import com.example.codechecker.ui.theme.CodeComment
import com.example.codechecker.ui.theme.CodeKeyword
import com.example.codechecker.ui.theme.CodeString
import com.example.codechecker.ui.theme.MaterialTheme

/**
 * 代码高亮显示组件
 * @param code Python代码字符串
 * @param highlightedLines 需要高亮的行号列表
 * @param modifier 修饰符
 */
@Composable
fun CodeHighlightView(
    code: String,
    highlightedLines: Set<Int> = emptySet(),
    modifier: Modifier = Modifier,
    showLineNumbers: Boolean = true
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CodeBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // 行号列
            if (showLineNumbers) {
                LineNumbersColumn(
                    code = code,
                    highlightedLines = highlightedLines,
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            // 代码内容
            CodeContent(
                code = code,
                highlightedLines = highlightedLines,
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            )
        }
    }
}

/**
 * 显示行号列
 */
@Composable
fun LineNumbersColumn(
    code: String,
    highlightedLines: Set<Int>,
    modifier: Modifier = Modifier
) {
    val lines = code.split("\n")
    val lineCount = lines.size

    Column(
        modifier = modifier
    ) {
        repeat(lineCount) { index ->
            val lineNumber = index + 1
            val isHighlighted = highlightedLines.contains(lineNumber)

            Text(
                text = lineNumber.toString().padStart(4, ' '),
                style = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = if (isHighlighted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                ),
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }
    }
}

/**
 * 代码内容显示
 */
@Composable
fun CodeContent(
    code: String,
    highlightedLines: Set<Int>,
    modifier: Modifier = Modifier
) {
    val highlightedCode = remember(code, highlightedLines) {
        highlightPythonCode(code, highlightedLines)
    }

    Text(
        text = highlightedCode,
        style = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            lineHeight = 20.sp
        ),
        modifier = modifier
    )
}

/**
 * Python代码高亮函数
 */
private fun highlightPythonCode(
    code: String,
    highlightedLines: Set<Int>
): androidx.compose.ui.text.AnnotatedString {
    val lines = code.split("\n")
    val annotatedString = buildAnnotatedString {
        lines.forEachIndexed { index, line ->
            val isHighlighted = highlightedLines.contains(index + 1)

            // 高亮背景
            if (isHighlighted) {
                addStyle(
                    style = SpanStyle(
                        background = Color(0xFFFFEB3B).copy(alpha = 0.3f)
                    ),
                    start = this.length,
                    end = this.length + line.length
                )
            }

            // 解析关键字
            val highlightedLine = highlightLine(line)

            append(highlightedLine)

            if (index < lines.size - 1) {
                append("\n")
            }
        }
    }

    return annotatedString
}

/**
 * 高亮单行代码
 */
private fun highlightLine(line: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        // 简单的关键字高亮实现
        // 实际应用中应使用更完善的词法分析

        // 移除注释
        val commentIndex = line.indexOf('#')
        val codePart = if (commentIndex >= 0) {
            line.substring(0, commentIndex)
        } else {
            line
        }
        val commentPart = if (commentIndex >= 0) {
            line.substring(commentIndex)
        } else {
            ""
        }

        // 高亮代码部分
        if (codePart.isNotEmpty()) {
            // 关键字
            val keywords = setOf(
                "def", "class", "if", "elif", "else", "for", "while", "try", "except",
                "finally", "with", "as", "import", "from", "return", "yield", "break",
                "continue", "pass", "raise", "assert", "del", "global", "nonlocal",
                "lambda", "and", "or", "not", "in", "is", "True", "False", "None"
            )

            var currentIndex = 0
            val regex = Regex("\\b(${keywords.joinToString("|")})\\b")

            regex.findAll(codePart).forEach { match ->
                // 添加非关键字部分
                if (match.range.first > currentIndex) {
                    append(codePart.substring(currentIndex, match.range.first))
                }

                // 添加关键字（高亮）
                addStyle(
                    style = SpanStyle(
                        color = CodeKeyword,
                        fontWeight = FontWeight.Bold
                    ),
                    start = this.length,
                    end = this.length + match.value.length
                )
                append(match.value)

                currentIndex = match.range.last + 1
            }

            // 添加剩余代码
            if (currentIndex < codePart.length) {
                append(codePart.substring(currentIndex))
            }
        }

        // 高亮注释部分
        if (commentPart.isNotEmpty()) {
            addStyle(
                style = SpanStyle(
                    color = CodeComment
                ),
                start = this.length,
                end = this.length + commentPart.length
            )
            append(commentPart)
        }
    }
}

/**
 * 复制代码按钮
 */
@Composable
fun CopyCodeButton(
    code: String,
    modifier: Modifier = Modifier
) {
    var copied by remember { mutableStateOf(false) }

    LaunchedEffect(copied) {
        if (copied) {
            // TODO: 实现复制到剪贴板功能
            // val clipboardManager = LocalClipboardManager.current
            // clipboardManager.setText(AnnotatedString(code))

            // 3秒后重置状态
            kotlinx.coroutines.delay(3000)
            copied = false
        }
    }

    Button(
        onClick = { copied = true },
        modifier = modifier
    ) {
        if (copied) {
            Text("已复制")
        } else {
            Text("复制代码")
        }
    }
}
```

---

## 6. 状态管理 (State Management)

### 6.1 StateFlow模式

**文件**: `app/src/main/java/com/example/codechecker/ui/screens/auth/LoginViewModel.kt`

```kotlin
package com.example.codechecker.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录页面ViewModel
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * 更新用户名
     */
    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username,
            error = null
        )
    }

    /**
     * 更新密码
     */
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            error = null
        )
    }

    /**
     * 执行登录
     */
    fun login() {
        val currentState = _uiState.value
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.value = currentState.copy(
                error = "请输入用户名和密码"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = null)

            authUseCase.login(currentState.username, currentState.password)
                .fold(
                    onSuccess = { user ->
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            isLoggedIn = true,
                            currentUser = user
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            error = exception.message ?: "登录失败"
                        )
                    }
                )
        }
    }

    /**
     * 清除错误状态
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

/**
 * 登录UI状态数据类
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: com.example.codechecker.domain.model.User? = null,
    val error: String? = null
)
```

---

## 7. 性能优化 (Performance Optimization)

### 7.1 重组范围优化

```kotlin
// ✅ 好的做法：使用remember缓存数据
@Composable
fun ExpensiveList(
    items: List<String>
) {
    val cachedItems by remember(items) { mutableStateOf(items) }

    LazyColumn {
        items(cachedItems) { item ->
            ListItem(item)
        }
    }
}

// ❌ 避免的做法：在Composable中执行复杂计算
@Composable
fun BadExample(
    items: List<String>
) {
    val expensiveResult = items.map { /* 复杂计算 */ } // 每次重组都会重新计算

    LazyColumn {
        items(expensiveResult) { item ->
            ListItem(item)
        }
    }
}
```

### 7.2 列表性能优化

```kotlin
@Composable
fun OptimizedLazyColumn(
    items: List<Item>
) {
    LazyColumn {
        // 使用key参数优化重组
        items(
            items = items,
            key = { item -> item.id } // 基于ID进行重组优化
        ) { item ->
            ItemRow(
                item = item,
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
}
```

---

## 8. 主题和暗色模式支持

### 8.1 主题切换

**文件**: `app/src/main/java/com/example/codechecker/ui/screens/settings/SettingsScreen.kt`

```kotlin
package com.example.codechecker.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") }
            )
        }
    ) { paddingValues ->
        SettingsContent(
            isDarkTheme = isDarkTheme,
            onThemeToggle = viewModel::toggleTheme,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun SettingsContent(
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "外观设置",
            style = MaterialTheme.typography.titleLarge
        )

        Switch(
            checked = isDarkTheme,
            onCheckedChange = onThemeToggle
        )

        Text(
            text = "深色主题"
        )
    }
}
```

---

## 9. 测试UI组件

### 9.1 Compose测试示例

**文件**: `app/src/androidTest/java/com/example/codechecker/ui/screens/auth/LoginScreenTest.kt`

```kotlin
package com.example.codechecker.ui.screens.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.codechecker.ui.theme.CodeCheckerTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginForm() {
        // 设置测试内容
        composeTestRule.setContent {
            CodeCheckerTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }

        // 测试用户名输入框
        composeTestRule
            .onNodeWithText("用户名")
            .assertIsDisplayed()
            .performTextInput("testuser")

        // 测试密码输入框
        composeTestRule
            .onNodeWithText("密码")
            .assertIsDisplayed()
            .performTextInput("password123")

        // 测试登录按钮
        composeTestRule
            .onNodeWithText("登录")
            .assertIsDisplayed()
            .assertIsEnabled()
            .performClick()

        // 验证加载状态
        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertIsDisplayed()
    }

    @Test
    fun testEmptyUsernameValidation() {
        composeTestRule.setContent {
            CodeCheckerTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("登录")
            .assertIsDisplayed()
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithText("密码")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("登录")
            .assertIsNotEnabled()
    }
}
```

---

## 10. 可访问性支持

### 10.1 添加ContentDescription

```kotlin
@Composable
fun AccessibleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.semantics {
            // 添加ContentDescription
            contentDescription = text
            // 添加角色描述
            role = Role.Button
        }
    ) {
        Text(text)
    }
}
```

---

## 总结

### Compose开发要点

1. **主题系统**: 统一色彩、字体、形状定义
2. **导航**: 使用Navigation Compose管理屏幕跳转
3. **状态管理**: StateFlow + ViewModel模式
4. **组件复用**: 创建可复用的UI组件
5. **性能优化**: 使用remember、key参数、重组范围控制

### 最佳实践

- ✅ 遵循Material Design 3规范
- ✅ 使用语义化修饰符提升可访问性
- ✅ 合理使用StateFlow管理UI状态
- ✅ 优化重组范围避免性能问题
- ✅ 添加适当的测试覆盖

### 参考资料

- [Jetpack Compose指南](https://developer.android.com/jetpack/compose)
- [Compose Material 3](https://developer.android.com/jetpack/compose/material3)
- [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)

---

**Jetpack Compose UI Guide Completed**: 2025-11-27
**Reference Documents**: tasks.md (T010-T011, T012-T017, T018-T023, T034-T035, T037-T040)
**Implementation Files**: ui/theme, ui/components, ui/screens, ui/navigation
**Test Files**: app/src/androidTest/java/com/example/codechecker/ui/
