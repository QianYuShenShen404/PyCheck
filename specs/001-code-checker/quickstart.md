# Quick Start Guide: CodeChecker Android应用

**Date**: 2025-11-27
**Feature**: CodeChecker Android应用 - Python代码查重助手
**Output of**: Phase 1 - Quick Start Guide

## 概述

CodeChecker是一个面向北航程序设计课程的Python代码查重Android应用。本指南将帮助您在21天内完成应用开发。

**快速开始时间估算**: 30-45分钟

---

## 第一部分：开发环境搭建

### 1. 系统要求

**操作系统**: Windows 11 / macOS 12+ / Ubuntu 20.04+

**硬件要求**:
- CPU: 4核心以上
- 内存: 8GB以上（推荐16GB）
- 存储: 10GB可用空间
- 网络: 稳定的互联网连接（用于依赖下载和AI功能测试）

### 2. 软件依赖

#### 必需软件

1. **JDK 17+**
   ```bash
   # 检查是否已安装
   java -version

   # Windows: 从Oracle官网下载或使用winget
   winget install Oracle.JavaRuntimeEnvironment

   # macOS: 使用Homebrew
   brew install openjdk@17

   # Ubuntu: 使用apt
   sudo apt update
   sudo apt install openjdk-17-jdk
   ```

2. **IntelliJ IDEA**
   - 下载地址: https://www.jetbrains.com/idea/
   - 版本: IntelliJ IDEA 2023.2+ 或 IntelliJ IDEA Ultimate
   - 安装时选择 "Android" 插件组合，或在IDEA中安装Android插件
   - 验证方法: File → Settings → Plugins → 确认 "Android", "Android APK Support", "Android System Tools" 已安装

3. **Kotlin插件**
   - IntelliJ IDEA内置Kotlin支持
   - 版本: Kotlin 1.9+
   - 验证方法: File → Settings → Plugins → 确认 "Kotlin" 插件已安装且版本为1.9+

4. **Git**
   ```bash
   # Windows
   winget install --id Git.Git -e --source winget

   # macOS
   brew install git

   # Ubuntu
   sudo apt install git
   ```

### 3. 环境变量配置

#### Windows (PowerShell)
```powershell
# 设置JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"

# 设置ANDROID_HOME
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"

# 将路径添加到PATH
$env:PATH += ";$env:JAVA_HOME\bin;$env:ANDROID_HOME\tools;$env:ANDROID_HOME\platform-tools"

# 验证
java -version
adb version
```

#### macOS/Linux (Bash)
```bash
# 编辑~/.bashrc或~/.zshrc
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools

# 重新加载
source ~/.bashrc

# 验证
java -version
adb version
```

---

## 第二部分：项目克隆与配置

### 1. 克隆项目

```bash
# 克隆代码仓库
git clone <repository-url>
cd codechecker-android

# 查看分支
git branch -a

# 切换到开发分支
git checkout 001-code-checker

# 拉取最新代码
git pull origin 001-code-checker
```

### 2. IntelliJ IDEA配置

1. **打开项目**
   ```bash
   # 方式1: 双击打开 IntelliJ IDEA
   # 方式2: 命令行打开
   cd path/to/project
   idea .
   ```

2. **首次打开项目**
   - 选择 "Open" 或 "Import Project"
   - 选择项目根目录
   - 选择 "Gradle" 作为导入类型
   - 确认 "Use Gradle 'wrapper'" 已勾选
   - 等待Gradle sync完成（约5-10分钟）

3. **配置SDK**
   - 打开 `File → Project Structure` (Ctrl+Alt+Shift+S)
   - 在 `Project Settings → Project` 中：
     - Project SDK: 选择 Java 17 (如未配置需先安装JDK)
     - Project language level: 17
   - 在 `Platform Settings → SDKs` 中：
     - 点击 "+" 添加Android SDK
     - 选择Android SDK路径: `$HOME/Android/Sdk` (Linux/Mac) 或 `%LOCALAPPDATA%\Android\Sdk` (Windows)

4. **配置模拟器（可选，用于调试）**
   - 打开 `Tools → AVD Manager`
   - 点击 "Create Virtual Device"
   - 选择设备: "Pixel 6" 或 "Pixel 6 Pro"
   - 选择系统镜像: "UpsideDownCake" (API 34)
   - 完成创建并启动模拟器

5. **启用Gradle工具窗口**
   - 在IDEA右侧点击 "Gradle" 标签
   - 如果未显示，通过 `View → Tool Windows → Gradle` 启用
   - 可以通过Gradle工具窗口运行任务

### 3. 项目结构说明

```
app/
├── build.gradle.kts          # 应用级Gradle配置
├── src/main/
│   ├── java/com/example/codechecker/
│   │   ├── CodeCheckerApp.kt # Application类
│   │   ├── MainActivity.kt   # 主Activity
│   │   ├── di/              # Hilt依赖注入模块
│   │   ├── data/            # 数据层
│   │   ├── domain/          # 领域层
│   │   ├── algorithm/       # 查重算法模块
│   │   ├── ui/              # 表现层
│   │   └── util/            # 工具类
│   ├── res/                 # 资源文件
│   └── AndroidManifest.xml  # 应用清单
├── build.gradle.kts          # 模块级Gradle配置
└── proguard-rules.pro       # 代码混淆规则
```

---

## 第三部分：依赖安装与验证

### 1. 依赖管理说明

项目使用以下主要依赖：

```kotlin
// app/build.gradle.kts 关键依赖
dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.10")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Hilt依赖注入
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Room数据库
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // 异步处理
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // 网络请求（AI功能）
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    // 测试
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.room:room-testing:2.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

### 2. 依赖安装验证

打开终端，在项目根目录执行：

```bash
# 1. 检查Gradle wrapper
./gradlew wrapper --gradle-version 8.4

# 2. 运行依赖解析
./gradlew --refresh-dependencies

# 3. 编译项目
./gradlew assembleDebug

# 4. 运行单元测试
./gradlew test

# 5. 运行UI测试
./gradlew connectedAndroidTest
```

预期输出：
```
BUILD SUCCESSFUL in 2m 30s
XXX tests completed, 0 failed
```

**故障排除**:

- 如果Gradle sync失败，检查网络连接
- 如果编译失败，检查JDK版本（需要17+）
- 如果测试失败，检查依赖是否完整安装

---

## 第四部分：运行与调试

### 1. 运行应用

#### 方法1: 使用IntelliJ IDEA

1. 连接Android设备或启动模拟器
2. 右键点击 `app` 模块
3. 选择 "Run 'app'" 或点击工具栏中的绿色 "Run" 按钮
4. 在运行配置中选择目标设备
5. 等待应用安装和启动

**快捷键**: Shift+F10 (Windows/Linux) 或 Ctrl+R (macOS)

#### 方法2: 使用命令行

```bash
# 安装到连接的设备
./gradlew installDebug

# 启动应用
adb shell am start -n com.example.codechecker/.MainActivity

# 查看日志
adb logcat | grep CodeChecker

# 卸载应用
adb uninstall com.example.codechecker
```

### 2. 调试应用

#### 日志调试

```kotlin
// 在代码中添加日志
import android.util.Log

class SomeClass {
    fun someMethod() {
        Log.d("CodeChecker", "Debug message")
        Log.i("CodeChecker", "Info message")
        Log.e("CodeChecker", "Error message")
    }
}
```

#### 网络调试（AI功能）

```kotlin
// 启用OkHttp日志拦截器
val logging = HttpLoggingInterceptor().apply {
    level = if (BuildConfig.DEBUG) {
        HttpLoggingInterceptor.Level.BODY
    } else {
        HttpLoggingInterceptor.Level.NONE
    }
}

val client = OkHttpClient.Builder()
    .addInterceptor(logging)
    .build()
```

#### 数据库调试

```kotlin
// 使用Room的logging
@Database(entities = [...], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    // ...
}

// 在Application中启用
Room.databaseBuilder(
    applicationContext,
    AppDatabase::class.java, "codechecker.db"
)
    .setJournalMode(RoomDatabase.JournalMode.TRACE)  // 启用SQL日志
    .build()
```

---

## 第五部分：测试指南

### 1. 单元测试

#### 算法模块测试

```kotlin
// test/java/com/example/codechecker/algorithm/PythonTokenizerTest.kt
@RunWith(JUnit4::class)
class PythonTokenizerTest {

    @Test
    fun testSimpleTokenization() {
        val code = """
            def hello():
                print("Hello, World!")
        """.trimIndent()

        val tokenizer = PythonTokenizer()
        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.isNotEmpty())
        assertTrue(tokens.any { it.type == TokenType.KEYWORD })
    }

    @Test
    fun testCommentRemoval() {
        val code = """
            # This is a comment
            x = 1  # inline comment
            """
        val tokenizer = PythonTokenizer()
        val tokens = tokenizer.tokenize(code)

        assertFalse(tokens.any { it.type == TokenType.COMMENT })
    }
}
```

运行单元测试：
```bash
./gradlew test
```

**或在IDEA中运行**:
- 展开 `app/src/test` 目录
- 右键点击测试包或单个测试文件
- 选择 "Run 'Tests in ...'"

#### 领域层测试

```kotlin
// test/java/com/example/codechecker/domain/usecase/PlagiarismUseCaseTest.kt
@ExtendWith(MockitoExtension::class)
class PlagiarismUseCaseTest {

    @Mock
    private lateinit var repository: PlagiarismRepository

    @InjectMocks
    private lateinit var useCase: PlagiarismUseCase

    @Test
    fun `test plagiarism calculation`() = runTest {
        val submission1 = createTestSubmission(1)
        val submission2 = createTestSubmission(2)

        `when`(repository.getSubmissionsByAssignment(1))
            .thenReturn(listOf(submission1, submission2))

        val results = useCase.compareSubmissions(1)

        assertEquals(1, results.size)
        assertTrue(results[0].similarityScore in 0f..100f)
    }
}
```

### 2. 集成测试

```kotlin
// androidTest/java/com/example/codechecker/LoginFlowTest.kt
@RunWith(AndroidJUnit4::class)
class LoginFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoginFlow() {
        // 启动应用
        composeTestRule.setContent {
            CodeCheckerTheme {
                LoginScreen(
                    onLoginSuccess = { },
                    onNavigateToRegister = { }
                )
            }
        }

        // 输入用户名和密码
        composeTestRule
            .onNodeWithText("用户名")
            .performTextInput("testuser")

        composeTestRule
            .onNodeWithText("密码")
            .performTextInput("password123")

        // 点击登录按钮
        composeTestRule
            .onNodeWithText("登录")
            .performClick()

        // 验证跳转到主页
        composeTestRule
            .onNodeWithText("欢迎")
            .assertIsDisplayed()
    }
}
```

运行集成测试：
```bash
# 需要连接设备或模拟器
./gradlew connectedAndroidTest

# 生成测试报告
./gradlew createDebugCoverageReport
```

**或在IDEA中运行**:
- 展开 `app/src/androidTest` 目录
- 右键点击测试包或单个测试文件
- 选择 "Run 'Instrumented Tests in ...'"
- 确保已连接设备或模拟器

### 3. 性能测试

```kotlin
// test/java/com/example/codechecker/algorithm/PerformanceTest.kt
class PerformanceTest {

    @Test
    fun testPlagiarismPerformance() = runTest {
        val submissions = (1..100).map { createLargeSubmission(it) }

        val startTime = System.currentTimeMillis()
        val results = useCase.compareSubmissionsBatch(submissions)
        val endTime = System.currentTimeMillis()

        val duration = endTime - startTime

        // 要求：100份代码在30秒内完成
        assertTrue("性能测试失败: 耗时${duration}ms", duration < 30000)
        assertEquals("结果数量不正确", 4950, results.size)
    }
}
```

---

## 第六部分：功能验证清单

### 核心功能测试

#### ✅ 用户认证
- [ ] 新用户注册（学生/教师）
- [ ] 用户登录验证
- [ ] 密码SHA-256加密存储
- [ ] 登录状态持久化
- [ ] 登出功能

#### ✅ 作业管理
- [ ] 教师创建作业（标题、描述、截止日期）
- [ ] 作业列表显示
- [ ] 作业详情查看
- [ ] 提交上限配置

#### ✅ 代码提交
- [ ] 文件选择器集成
- [ ] .py文件验证
- [ ] 多文件提交
- [ ] 提交历史查看
- [ ] MD5哈希计算

#### ✅ 查重引擎
- [ ] Python词法分析
- [ ] 注释和空行移除
- [ ] Jaccard相似度计算
- [ ] LCS相似度计算
- [ ] 综合得分计算（0.4*J + 0.6*L）
- [ ] 两两比对
- [ ] 进度回调

#### ✅ 报告展示
- [ ] 报告列表
- [ ] 相似度分布图表
- [ ] 高相似度警告
- [ ] 代码对比视图
- [ ] 高亮显示

#### ✅ 权限控制
- [ ] 学生只能查看自己的提交
- [ ] 教师只能管理自己的作业
- [ ] 数据访问权限验证

#### ✅ 选做功能（可选）
- [ ] AI分析API集成
- [ ] 多AI提供商支持
- [ ] 错误处理和重试
- [ ] 降级策略

---

## 第七部分：常见问题解决

### Q1: Gradle sync失败

**错误信息**: "Failed to sync project"

**解决方案**:
```bash
# 1. 清理构建缓存
./gradlew clean

# 2. 删除.gradle文件夹
rm -rf .gradle  # Linux/Mac
rmdir /s .gradle  # Windows

# 3. 重新同步
./gradlew --refresh-dependencies
```

### Q2: Kotlin编译错误

**错误信息**: "Unresolved reference: Compose"

**解决方案**:
```kotlin
// 检查Compose BOM版本
// 在build.gradle.kts (app)
android {
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.4"
    }
}
```

### Q3: Hilt依赖注入失败

**错误信息**: "cannot be provided without an @Inject constructor"

**解决方案**:
```kotlin
// 1. 确保@AndroidEntryPoint在Activity/Application上
@AndroidEntryPoint
class MainActivity : ComponentActivity()

// 2. 使用@Inject注解构造函数
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository

// 3. 确保Module是单例
@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule
```

### Q4: Room数据库错误

**错误信息**: "Cannot figure out how to save this field into database"

**解决方案**:
```kotlin
// 1. 确保@Entity注解在实体类上
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "username") val username: String
)

// 2. 使用TypeConverter处理复杂类型
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value.split(",")
    }
}
```

### Q5: AI API调用失败

**错误信息**: "API call failed with 401 Unauthorized"

**解决方案**:
```kotlin
// 1. 检查API密钥配置
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideApiKey(): String {
        return BuildConfig.AI_API_KEY
    }
}

// 2. 检查请求头
val request = Request.Builder()
    .url(url)
    .addHeader("Authorization", "Bearer $apiKey")
    .build()
```

### Q6: 模拟器无法启动

**错误信息**: "AVD is not running"

**解决方案**:
```bash
# 1. 检查虚拟化支持
# Windows: 在BIOS中启用Intel VT-x或AMD-V

# 2. 重置AVD
android avd  # 打开AVD Manager
# 点击 "Wipe Data" 和 "Cold Boot Now"

# 3. 检查HAXM安装（Windows）
# 下载并安装Intel HAXM: https://github.com/intel/haxm/releases
```

---

## 第八部分：性能优化建议

### 1. 数据库优化

```kotlin
// 批量插入使用事务
@Transaction
suspend fun insertSubmissions(submissions: List<SubmissionEntity>) {
    submissions.forEach { insertSubmission(it) }
}

// 使用索引加速查询
@Dao
interface SubmissionDao {
    @Query("SELECT * FROM submissions WHERE assignment_id = :assignmentId ORDER BY submitted_at DESC")
    suspend fun getSubmissionsByAssignment(assignmentId: Long): List<SubmissionEntity>
}
```

### 2. UI性能优化

```kotlin
// 使用LazyColumn处理大列表
@Composable
fun SubmissionList(submissions: List<Submission>) {
    LazyColumn {
        items(
            items = submissions,
            key = { it.id }  // 添加key提升性能
        ) { submission ->
            SubmissionItem(submission = submission)
        }
    }
}

// 避免在Composable中进行复杂计算
@Composable
fun ExpensiveComputation(data: List<String>) {
    val expensiveResult = remember(data) {
        data.map { it.uppercase() }
    }
    // 渲染逻辑
}
```

### 3. 算法性能优化

```kotlin
// 使用协程并发处理
class ParallelPlagiarismEngine {
    suspend fun compareBatch(
        submissions: List<Submission>
    ): List<SimilarityResult> = withContext(Dispatchers.Default) {
        submissions.chunked(10).map { chunk ->
            async {
                processChunk(chunk)
            }
        }.awaitAll().flatten()
    }
}
```

---

## 第九部分：发布准备

### 1. 代码混淆

```kotlin
// app/proguard-rules.pro
# 保留数据模型
-keep class com.example.codechecker.domain.model.** { *; }

# 保留Room实体
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }

# 保留Hilt注入
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

# 移除日志
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
```

### 2. 构建发布版本

```bash
# 生成签名密钥
keytool -genkey -v -keystore codechecker-key.jks -alias codechecker-key -keyalg RSA -keysize 2048 -validity 10000

# 配置签名
# 在gradle.properties中添加:
CODE_CHECKER_STORE_FILE=codechecker-key.jks
CODE_CHECKER_STORE_PASSWORD=*****
CODE_CHECKER_KEY_ALIAS=codechecker-key
CODE_CHECKER_KEY_PASSWORD=*****

# 构建发布版本
./gradlew bundleRelease

# 构建APK
./gradlew assembleRelease

# 生成测试报告
./gradlew createReleaseCoverageReport
```

### 3. 应用签名配置

```kotlin
// app/build.gradle.kts
android {
    signingConfigs {
        create("release") {
            storeFile = file("../codechecker-key.jks")
            storePassword = findProperty("CODE_CHECKER_STORE_PASSWORD") as String?
            keyAlias = findProperty("CODE_CHECKER_KEY_ALIAS") as String?
            keyPassword = findProperty("CODE_CHECKER_KEY_PASSWORD") as String?
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            minifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

---

## 第十部分：技术文档链接

### 相关资源

1. **Kotlin官方文档**: https://kotlinlang.org/docs/home.html
2. **Jetpack Compose指南**: https://developer.android.com/jetpack/compose
3. **Room数据库指南**: https://developer.android.com/training/data-storage/room
4. **Hilt依赖注入**: https://dagger.dev/hilt/
5. **Kotlin协程**: https://kotlinlang.org/docs/coroutines-overview.html
6. **IntelliJ IDEA Android开发指南**: https://developer.android.com/studio/intro
7. **IDEA Gradle集成**: https://www.jetbrains.com/help/idea/gradle.html
8. **IDEA Android插件**: https://www.jetbrains.com/help/idea/android.html

### 性能基准

| 测试项 | 目标 | 测试方法 |
|--------|------|----------|
| 100份代码查重 | <30秒 | 运行PerformanceTest |
| UI响应时间 | <100ms | 使用Compose测试 |
| 应用冷启动 | <3秒 | 使用IDEA Profiler |
| 算法测试覆盖率 | >80% | 运行 `./gradlew testDebugUnitTestCoverage` |
| Gradle sync | <5分钟 | 运行 `./gradlew --refresh-dependencies` |

---

## 总结

本快速开始指南涵盖了：

✅ **环境搭建**: JDK、Android Studio、Git配置
✅ **项目配置**: Gradle依赖、构建配置
✅ **运行调试**: 真机/模拟器运行、日志调试
✅ **测试验证**: 单元测试、集成测试、性能测试
✅ **功能验证**: 21天开发清单
✅ **故障排除**: 常见问题解决方案
✅ **发布准备**: 代码混淆、签名配置

**下一步行动**:

1. 按照本指南完成环境搭建（30-45分钟）
2. 运行基础功能测试，验证开发环境
3. 参考 `plan.md` 中的21天实现计划开始开发
4. 定期运行测试，确保功能完整性

**获得帮助**:

- 查看项目README文档
- 搜索相关技术文档
- 在团队内部沟通渠道提问
- 参考代码示例和最佳实践

---

**Quick Start Completed**: 2025-11-27
**IDE Environment**: IntelliJ IDEA + Android Plugins
**Next Phase**: Phase 2 - Task Breakdown
**Output Files**: quickstart.md
