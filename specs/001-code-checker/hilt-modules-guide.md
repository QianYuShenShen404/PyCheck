# Hilt依赖注入指南: CodeChecker Android项目

**Date**: 2025-11-27
**Feature**: CodeChecker Android应用 - Python代码查重助手
**Reference**: tasks.md (T004), data-model.md (Repository接口)
**Purpose**: 提供完整的Hilt依赖注入实现示例和最佳实践

---

## 概述

Hilt是Google推荐的Android依赖注入库，基于Dagger构建，简化了Android应用的依赖注入配置。本指南提供CodeChecker项目的完整Hilt模块设计和实现。

### 项目架构

```
app/src/main/java/com/example/codechecker/
├── CodeCheckerApp.kt              # Application类，Hilt入口
├── di/                            # Hilt模块
│   ├── DatabaseModule.kt          # 数据库模块
│   ├── RepositoryModule.kt        # Repository模块
│   ├── UseCaseModule.kt           # 用例模块
│   ├── NetworkModule.kt           # 网络模块（AI功能）
│   └── AlgorithmModule.kt         # 算法模块
└── MainActivity.kt                # 主Activity，@AndroidEntryPoint
```

---

## 1. Application类配置

### 1.1 CodeCheckerApp.kt

**文件**: `app/src/main/java/com/example/codechecker/CodeCheckerApp.kt`

```kotlin
package com.example.codechecker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 应用入口类
 *
 * 使用@HiltAndroidApp注解启用Hilt，
 * 会自动生成Dagger组件和依赖注入图
 */
@HiltAndroidApp
class CodeCheckerApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // 初始化全局配置
        initializeApp()
    }

    private fun initializeApp() {
        // TODO: 初始化全局配置，如日志系统、性能监控等
        if (BuildConfig.ENABLE_LOGGING) {
            // 启用日志记录
        }
    }
}
```

### 1.2 MainActivity.kt

**文件**: `app/src/main/java/com/example/codechecker/MainActivity.kt`

```kotlin
package com.example.codechecker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint

/**
 * 主Activity
 *
 * 使用@AndroidEntryPoint注解启用Hilt依赖注入
 * 只能在有@AndroidEntryPoint的组件中使用@Inject注解
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // TODO: 设置Compose UI主题
            // CodeCheckerTheme {
            //     Navigation()
            // }
        }
    }
}
```

---

## 2. 数据库模块 (DatabaseModule)

### 2.1 DatabaseModule.kt

**文件**: `app/src/main/java/com/example/codechecker/di/DatabaseModule.kt`

```kotlin
package com.example.codechecker.di

import android.content.Context
import androidx.room.Room
import com.example.codechecker.data.local.database.AppDatabase
import com.example.codechecker.data.local.dao.AssignmentDao
import com.example.codechecker.data.local.dao.ReportDao
import com.example.codechecker.data.local.dao.SubmissionDao
import com.example.codechecker.data.local.dao.SimilarityDao
import com.example.codechecker.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 数据库模块
 *
 * 提供Room数据库和DAO接口的依赖注入
 * 使用@Module和@InstallIn注解定义Hilt模块
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供Room数据库实例
     * @param context 应用上下文
     * @return AppDatabase实例
     */
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "codechecker_database"
        )
        .addMigrations(AppDatabase.MIGRATION_1_2)
        .fallbackToDestructiveMigration() // 开发阶段可以清理数据库
        .build()
    }

    /**
     * 提供UserDao接口
     * @param database 数据库实例
     * @return UserDao实例
     */
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    /**
     * 提供AssignmentDao接口
     * @param database 数据库实例
     * @return AssignmentDao实例
     */
    @Provides
    fun provideAssignmentDao(database: AppDatabase): AssignmentDao {
        return database.assignmentDao()
    }

    /**
     * 提供SubmissionDao接口
     * @param database 数据库实例
     * @return SubmissionDao实例
     */
    @Provides
    fun provideSubmissionDao(database: AppDatabase): SubmissionDao {
        return database.submissionDao()
    }

    /**
     * 提供ReportDao接口
     * @param database 数据库实例
     * @return ReportDao实例
     */
    @Provides
    fun provideReportDao(database: AppDatabase): ReportDao {
        return database.reportDao()
    }

    /**
     * 提供SimilarityDao接口
     * @param database 数据库实例
     * @return SimilarityDao实例
     */
    @Provides
    fun provideSimilarityDao(database: AppDatabase): SimilarityDao {
        return database.similarityDao()
    }
}
```

### 2.2 AppDatabase.kt

**文件**: `app/src/main/java/com/example/codechecker/data/local/database/AppDatabase.kt`

```kotlin
package com.example.codechecker.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.codechecker.data.local.dao.*
import com.example.codechecker.data.local.entity.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Provider

/**
 * Room数据库类
 *
 * 定义数据库实体、版本和迁移策略
 */
@Database(
    entities = [
        UserEntity::class,
        AssignmentEntity::class,
        SubmissionEntity::class,
        ReportEntity::class,
        SimilarityEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // DAO接口声明
    abstract fun userDao(): UserDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun submissionDao(): SubmissionDao
    abstract fun reportDao(): ReportDao
    abstract fun similarityDao(): SimilarityDao

    companion object {
        // 数据库迁移示例
        // 从版本1升级到版本2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 添加新列或表结构调整
                // 示例：添加新字段
                // database.execSQL("ALTER TABLE users ADD COLUMN email TEXT")
            }
        }
    }
}
```

---

## 3. Repository模块 (RepositoryModule)

### 3.1 RepositoryModule.kt

**文件**: `app/src/main/java/com/example/codechecker/di/RepositoryModule.kt`

```kotlin
package com.example.codechecker.di

import com.example.codechecker.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository模块
 *
 * 使用@Binds注解绑定Repository接口到其实现类
 * 适用于通过构造函数注入的接口实现
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * 绑定UserRepository接口到UserRepositoryImpl实现
     * @param userRepositoryImpl 用户仓库实现
     * @return UserRepository接口
     */
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    /**
     * 绑定AssignmentRepository接口到AssignmentRepositoryImpl实现
     * @param assignmentRepositoryImpl 作业仓库实现
     * @return AssignmentRepository接口
     */
    @Binds
    @Singleton
    abstract fun bindAssignmentRepository(
        assignmentRepositoryImpl: AssignmentRepositoryImpl
    ): AssignmentRepository

    /**
     * 绑定SubmissionRepository接口到SubmissionRepositoryImpl实现
     * @param submissionRepositoryImpl 提交仓库实现
     * @return SubmissionRepository接口
     */
    @Binds
    @Singleton
    abstract fun bindSubmissionRepository(
        submissionRepositoryImpl: SubmissionRepositoryImpl
    ): SubmissionRepository

    /**
     * 绑定ReportRepository接口到ReportRepositoryImpl实现
     * @param reportRepositoryImpl 报告仓库实现
     * @return ReportRepository接口
     */
    @Binds
    @Singleton
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): ReportRepository
}
```

---

## 4. 用例模块 (UseCaseModule)

### 4.1 UseCaseModule.kt

**文件**: `app/src/main/java/com/example/codechecker/di/UseCaseModule.kt`

```kotlin
package com.example.codechecker.di

import com.example.codechecker.domain.usecase.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 用例模块
 *
 * 绑定所有业务用例
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    /**
     * 绑定认证用例
     * @param authUseCase 认证用例实现
     * @return AuthUseCase接口
     */
    @Binds
    @Singleton
    abstract fun bindAuthUseCase(
        authUseCase: AuthUseCase
    ): AuthUseCase

    /**
     * 绑定作业用例
     * @param assignmentUseCase 作业用例实现
     * @return AssignmentUseCase接口
     */
    @Binds
    @Singleton
    abstract fun bindAssignmentUseCase(
        assignmentUseCase: AssignmentUseCase
    ): AssignmentUseCase

    /**
     * 绑定提交用例
     * @param submissionUseCase 提交用例实现
     * @return SubmissionUseCase接口
     */
    @Binds
    @Singleton
    abstract fun bindSubmissionUseCase(
        submissionUseCase: SubmissionUseCase
    ): SubmissionUseCase

    /**
     * 绑定查重用例
     * @param plagiarismUseCase 查重用例实现
     * @return PlagiarismUseCase接口
     */
    @Binds
    @Singleton
    abstract fun bindPlagiarismUseCase(
        plagiarismUseCase: PlagiarismUseCase
    ): PlagiarismUseCase

    /**
     * 绑定报告用例
     * @param reportUseCase 报告用例实现
     * @return ReportUseCase接口
     */
    @Binds
    @Singleton
    abstract fun bindReportUseCase(
        reportUseCase: ReportUseCase
    ): ReportUseCase
}
```

---

## 5. 网络模块 (NetworkModule)

### 5.1 NetworkModule.kt

**文件**: `app/src/main/java/com/example/codechecker/di/NetworkModule.kt`

```kotlin
package com.example.codechecker.di

import com.example.codechecker.data.remote.api.AIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import com.example.codechecker.BuildConfig

/**
 * 网络模块（用于AI分析功能）
 *
 * 提供OkHttpClient、Retrofit和AIService的依赖注入
 * 仅在启用AI功能时使用
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * 提供OkHttpClient实例
     * @return 配置好的OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build()
    }

    /**
     * 提供DeepSeek Retrofit实例
     * @param okHttpClient OkHttpClient
     * @return DeepSeek AIService
     */
    @Provides
    @Singleton
    @Named("deepseek")
    fun provideDeepSeekRetrofit(
        okHttpClient: OkHttpClient
    ): AIService {
        return Retrofit.Builder()
            .baseUrl("https://api.deepseek.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AIService::class.java)
    }

    /**
     * 提供通义千问Retrofit实例
     * @param okHttpClient OkHttpClient
     * @return 通义千问AIService
     */
    @Provides
    @Singleton
    @Named("qwen")
    fun provideQwenRetrofit(
        okHttpClient: OkHttpClient
    ): AIService {
        return Retrofit.Builder()
            .baseUrl("https://dashscope.aliyuncs.com/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AIService::class.java)
    }

    /**
     * 提供ModelScope Retrofit实例
     * @param okHttpClient OkHttpClient
     * @return ModelScope AIService
     */
    @Provides
    @Singleton
    @Named("modelscope")
    fun provideModelScopeRetrofit(
        okHttpClient: OkHttpClient
    ): AIService {
        return Retrofit.Builder()
            .baseUrl("https://dashscope.aliyuncs.com/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AIService::class.java)
    }

    /**
     * 提供AI服务管理器
     * @param deepSeekService DeepSeek服务
     * @param qwenService 通义千问服务
     * @param modelScopeService ModelScope服务
     * @return AI服务管理器
     */
    @Provides
    @Singleton
    fun provideAIServiceManager(
        @Named("deepseek") deepSeekService: AIService,
        @Named("qwen") qwenService: AIService,
        @Named("modelscope") modelScopeService: AIService
    ): AIService {
        // 可以根据配置选择不同的AI服务提供商
        return when (BuildConfig.AI_PROVIDER) {
            "DEEPSEEK" -> deepSeekService
            "QWEN" -> qwenService
            "MODELSCOPE" -> modelScopeService
            else -> deepSeekService
        }
    }
}
```

---

## 6. 算法模块 (AlgorithmModule)

### 6.1 AlgorithmModule.kt

**文件**: `app/src/main/java/com/example/codechecker/di/AlgorithmModule.kt`

```kotlin
package com.example.codechecker.di

import com.example.codechecker.algorithm.engine.PlagiarismEngine
import com.example.codechecker.algorithm.similarity.JaccardSimilarity
import com.example.codechecker.algorithm.similarity.LCSSimilarity
import com.example.codechecker.algorithm.tokenizer.PythonTokenizer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 算法模块
 *
 * 提供查重算法相关的依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
object AlgorithmModule {

    /**
     * 提供Python词法分析器
     * @return PythonTokenizer实例
     */
    @Provides
    @Singleton
    fun providePythonTokenizer(): PythonTokenizer {
        return PythonTokenizer()
    }

    /**
     * 提供Jaccard相似度计算器
     * @param tokenizer 词法分析器
     * @return JaccardSimilarity实例
     */
    @Provides
    @Singleton
    fun provideJaccardSimilarity(
        tokenizer: PythonTokenizer
    ): JaccardSimilarity {
        return JaccardSimilarity(tokenizer)
    }

    /**
     * 提供LCS相似度计算器
     * @param tokenizer 词法分析器
     * @return LCSSimilarity实例
     */
    @Provides
    @Singleton
    fun provideLCSSimilarity(
        tokenizer: PythonTokenizer
    ): LCSSimilarity {
        return LCSSimilarity(tokenizer)
    }

    /**
     * 提供查重引擎
     * @param tokenizer 词法分析器
     * @param jaccardSimilarity Jaccard相似度计算器
     * @param lcsSimilarity LCS相似度计算器
     * @return PlagiarismEngine实例
     */
    @Provides
    @Singleton
    fun providePlagiarismEngine(
        tokenizer: PythonTokenizer,
        jaccardSimilarity: JaccardSimilarity,
        lcsSimilarity: LCSSimilarity
    ): PlagiarismEngine {
        return PlagiarismEngine(
            tokenizer = tokenizer,
            jaccardSimilarity = jaccardSimilarity,
            lcsSimilarity = lcsSimilarity
        )
    }
}
```

---

## 7. DataStore模块 (DataStoreModule)

### 7.1 DataStoreModule.kt

**文件**: `app/src/main/java/com/example/codechecker/di/DataStoreModule.kt`

```kotlin
package com.example.codechecker.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.codechecker.data.preference.UserSessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * DataStore模块
 *
 * 提供用户偏好设置和会话管理的依赖注入
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    // 创建DataStore实例
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = "codechecker_preferences"
    )

    /**
     * 提供DataStore实例
     * @param context 应用上下文
     * @return DataStore实例
     */
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }

    /**
     * 提供用户会话管理器
     * @param dataStore DataStore实例
     * @return UserSessionManager实例
     */
    @Provides
    @Singleton
    fun provideUserSessionManager(
        dataStore: DataStore<Preferences>
    ): UserSessionManager {
        return UserSessionManager(dataStore)
    }
}
```

---

## 8. Repository实现示例

### 8.1 UserRepositoryImpl.kt

**文件**: `app/src/main/java/com/example/codechecker/data/repository/UserRepositoryImpl.kt`

```kotlin
package com.example.codechecker.data.repository

import com.example.codechecker.data.local.dao.UserDao
import com.example.codechecker.data.local.entity.UserEntity
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.repository.UserRepository
import com.example.codechecker.util.CryptoUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 用户仓库实现
 *
 * 使用@Inject注解标记构造函数，
 * 让Hilt自动创建实例
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val cryptoUtils: CryptoUtils
) : UserRepository {

    /**
     * 获取用户列表（Flow）
     * @return 用户列表Flow
     */
    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsersFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    /**
     * 通过ID获取用户
     * @param id 用户ID
     * @return 用户Flow
     */
    override fun getUserById(id: Long): Flow<User?> {
        return userDao.getUserByIdFlow(id).map { entity ->
            entity?.toDomain()
        }
    }

    /**
     * 通过用户名获取用户
     * @param username 用户名
     * @return 用户Flow
     */
    override fun getUserByUsername(username: String): Flow<User?> {
        return userDao.getUserByUsernameFlow(username).map { entity ->
            entity?.toDomain()
        }
    }

    /**
     * 注册新用户
     * @param username 用户名
     * @param password 密码
     * @param displayName 显示名称
     * @param role 用户角色
     * @return 用户ID
     */
    override suspend fun registerUser(
        username: String,
        password: String,
        displayName: String,
        role: String
    ): Long {
        val passwordHash = cryptoUtils.hashPassword(password)
        val userEntity = UserEntity(
            username = username,
            passwordHash = passwordHash,
            displayName = displayName,
            role = role,
            createdAt = System.currentTimeMillis()
        )
        return userDao.insertUser(userEntity)
    }

    /**
     * 验证用户登录
     * @param username 用户名
     * @param password 密码
     * @return 是否验证成功
     */
    override suspend fun validateLogin(
        username: String,
        password: String
    ): Boolean {
        val user = userDao.getUserByUsername(username)
        return user?.let { storedUser ->
            val inputPasswordHash = cryptoUtils.hashPassword(password)
            inputPasswordHash == storedUser.passwordHash
        } ?: false
    }
}
```

---

## 9. UseCase实现示例

### 9.1 AuthUseCase.kt

**文件**: `app/src/main/java/com/example/codechecker/domain/usecase/AuthUseCase.kt`

```kotlin
package com.example.codechecker.domain.usecase

import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.model.User
import com.example.codechecker.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证用例
 *
 * 处理用户注册、登录、登出等业务逻辑
 */
@Singleton
class AuthUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val userSessionManager: UserSessionManager
) {

    /**
     * 注册新用户
     * @param username 用户名
     * @param password 密码
     * @param displayName 显示名称
     * @param role 用户角色
     * @return 注册结果
     */
    suspend fun registerUser(
        username: String,
        password: String,
        displayName: String,
        role: String
    ): Result<Long> {
        return try {
            // TODO: 添加用户名长度、密码强度验证
            if (username.length < 4) {
                return Result.failure(IllegalArgumentException("用户名长度至少4个字符"))
            }

            if (password.length < 6) {
                return Result.failure(IllegalArgumentException("密码长度至少6个字符"))
            }

            val userId = userRepository.registerUser(username, password, displayName, role)
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    suspend fun login(
        username: String,
        password: String
    ): Result<User> {
        return try {
            val isValid = userRepository.validateLogin(username, password)
            if (isValid) {
                val user = userRepository.getUserByUsername(username).first()
                user?.let { currentUser ->
                    // 保存登录状态
                    userSessionManager.saveLoginState(currentUser.id, true)
                    Result.success(currentUser)
                } ?: Result.failure(Exception("用户不存在"))
            } else {
                Result.failure(Exception("用户名或密码错误"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 用户登出
     */
    suspend fun logout() {
        userSessionManager.clearLoginState()
    }

    /**
     * 获取当前用户会话状态
     * @return 会话状态Flow
     */
    fun getCurrentSession(): Flow<User?> {
        return userSessionManager.getCurrentUser()
    }

    /**
     * 检查用户是否已登录
     * @return 是否已登录
     */
    suspend fun isLoggedIn(): Boolean {
        return userSessionManager.isLoggedIn().first()
    }

    /**
     * 自动登录
     * @return 自动登录结果
     */
    suspend fun autoLogin(): Result<User?> {
        return try {
            if (isLoggedIn()) {
                val user = getCurrentSession().first()
                Result.success(user)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

---

## 10. 在UI层使用依赖注入

### 10.1 ViewModel示例

**文件**: `app/src/main/java/com/example/codechecker/ui/screens/auth/LoginViewModel.kt`

```kotlin
package com.example.codechecker.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.codechecker.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 登录页面ViewModel
 *
 * 使用@HiltViewModel注解启用Hilt依赖注入
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * 执行登录
     * @param username 用户名
     * @param password 密码
     */
    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = authUseCase.login(username, password)
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message
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
 * 登录UI状态
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val error: String? = null
)
```

### 10.2 Composable函数示例

**文件**: `app/src/main/java/com/example/codechecker/ui/screens/auth/LoginScreen.kt`

```kotlin
package com.example.codechecker.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

/**
 * 登录页面
 *
 * 使用hiltViewModel()函数获取ViewModel实例
 * Hilt会自动注入所需依赖
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // 观察登录成功状态
    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    // 登录表单
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "CodeChecker 登录", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.login(username, password)
            },
            enabled = !uiState.isLoading && username.isNotEmpty() && password.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp))
            } else {
                Text("登录")
            }
        }

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = uiState.error,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
```

---

## 11. 依赖注入最佳实践

### 11.1 注解使用指南

| 注解 | 作用域 | 用途 |
|------|--------|------|
| `@AndroidEntryPoint` | Activity/Fragment/Service | 启用Hilt依赖注入 |
| `@HiltAndroidApp` | Application | 启用Hilt应用级依赖注入 |
| `@Module` | 类 | 定义Hilt模块 |
| `@Provides` | 方法 | 提供依赖实例 |
| `@Binds` | 抽象方法 | 绑定接口到实现 |
| `@Singleton` | 组件作用域 | 单例作用域 |
| `@ActivityScoped` | Activity作用域 | Activity作用域 |
| `@Inject` | 构造函数/字段 | 注入依赖 |

### 11.2 依赖注入原则

1. **构造函数注入优先**: 优先使用`@Inject`构造函数，避免`@Provides`
2. **接口绑定**: 使用`@Binds`绑定接口到实现
3. **作用域管理**: 根据生命周期选择合适的作用域
4. **模块化**: 将相关依赖组织到单独的模块中
5. **避免循环依赖**: 注意依赖关系的方向

### 11.3 常见错误及解决方案

**错误1: 无法提供依赖**
```
error: [Dagger/MissingBinding] Type cannot be provided without an @Inject constructor
```
**解决方案**: 检查是否在Module中提供了该依赖，或添加`@Inject`构造函数

**错误2: Hilt生命周期不匹配**
```
error: [Dagger/ScopeMismatch] This element is not scope-compatible
```
**解决方案**: 检查作用域注解，确保在正确的组件中

**错误3: 重复绑定**
```
error: [Dagger/DuplicateBindings] Type is bound multiple times
```
**解决方案**: 检查是否重复提供了同一个依赖

---

## 12. 测试中的依赖注入

### 12.1 单元测试替换

**文件**: `app/src/test/java/com/example/codechecker/domain/usecase/AuthUseCaseTest.kt`

```kotlin
package com.example.codechecker.domain.usecase

import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.repository.UserRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
class AuthUseCaseTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var userSessionManager: UserSessionManager

    @Inject
    lateinit var authUseCase: AuthUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        hiltRule.inject()
    }

    @Test
    fun `test login success`() = runBlocking {
        // Given
        val username = "testuser"
        val password = "password123"
        `when`(userRepository.validateLogin(username, password))
            .thenReturn(true)

        // When
        val result = authUseCase.login(username, password)

        // Then
        assertTrue(result.isSuccess)
        verify(userSessionManager).saveLoginState(any(), eq(true))
    }
}
```

### 12.2 测试模块替换

**文件**: `app/src/test/java/com/example/codechecker/di/TestModule.kt`

```kotlin
package com.example.codechecker.di

import com.example.codechecker.data.preference.UserSessionManager
import com.example.codechecker.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

/**
 * 测试模块
 *
 * 在测试中替换真实的依赖注入
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class, UseCaseModule::class]
)
object TestModule {

    @Provides
    @Singleton
    fun provideTestUserRepository(): UserRepository {
        // 返回模拟的Repository
        return mockk() // 或使用Mockito.mock()
    }

    @Provides
    @Singleton
    fun provideTestUserSessionManager(): UserSessionManager {
        return mockk()
    }
}
```

---

## 13. 性能优化

### 13.1 减少依赖注入开销

```kotlin
// 使用@Binds代替@Provides（性能更好）
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}

// 避免在运行时创建对象
@Singleton
class ExpensiveObject @Inject constructor(
    @ApplicationContext context: Context,
    database: AppDatabase // 直接注入依赖
) {
    // 构造函数中的依赖由Hilt自动提供
}
```

### 13.2 延迟初始化

```kotlin
// 对于非关键依赖，使用延迟初始化
class NonCriticalService @Inject constructor(
    @Inject private val lazyDependency: Lazy<HeavyDependency>
) {
    fun doSomething() {
        // 实际使用时才初始化
        val dependency = lazyDependency.get()
        dependency.use()
    }
}
```

---

## 14. 常见问题解答

### Q1: 如何处理第三方库依赖？

**A1**: 创建Module包装第三方库
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ThirdPartyModule {
    @Provides
    @Singleton
    fun provideThirdPartyService(): ThirdPartyService {
        return ThirdPartyServiceImpl()
    }
}
```

### Q2: 如何在单测中替换Hilt依赖？

**A2**: 使用`@TestInstallIn`
```kotlin
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RealModule::class]
)
object TestModule {
    // 提供测试用的依赖
}
```

### Q3: 如何处理多实例依赖？

**A3**: 使用`@Named`注解
```kotlin
@Provides
@Named("debug")
fun provideDebugConfig(): Config {
    return DebugConfig()
}

@Provides
@Named("release")
fun provideReleaseConfig(): Config {
    return ReleaseConfig()
}
```

---

## 15. 迁移到Hilt

### 15.1 从Dagger迁移

1. 替换`@Component`为`@HiltAndroidApp`
2. 替换`@Component.Builder`为`@Module`和`@InstallIn`
3. 更新作用域注解：`@Singleton` → `@Singleton`
4. 添加`@AndroidEntryPoint`到Android组件

### 15.2 从手动依赖注入迁移

1. 分析依赖关系图
2. 创建Module组织依赖
3. 使用`@Binds`绑定接口
4. 在Activity/Fragment中添加`@AndroidEntryPoint`

---

## 总结

### Hilt配置要点

1. **Application类**: `@HiltAndroidApp`启用Hilt
2. **Android组件**: `@AndroidEntryPoint`启用依赖注入
3. **依赖绑定**: `@Binds`绑定接口，`@Provides`提供实例
4. **作用域**: `@Singleton`单例，`@ActivityScoped`活动作用域
5. **模块**: `@Module`定义模块，`@InstallIn`指定作用域

### 测试支持

- ✅ 单元测试: `@TestInstallIn`替换依赖
- ✅ 集成测试: `HiltAndroidRule`初始化
- ✅ UI测试: `HiltTestApplication`应用

### 性能考虑

- ✅ 使用`@Binds`提高性能
- ✅ 避免过度依赖注入
- ✅ 合理使用作用域

### 参考资料

- [Hilt官方文档](https://dagger.dev/hilt/)
- [Hilt Android指南](https://developer.android.com/training/dependency-injection/hilt-android)
- tasks.md (T004)
- data-model.md (Repository接口定义)

---

**Hilt Modules Guide Completed**: 2025-11-27
**Reference Documents**: tasks.md (T004), data-model.md
**Implementation Files**: di/模块, Repository实现, UseCase实现
**Test Files**: app/src/test/java/com/example/codechecker/
