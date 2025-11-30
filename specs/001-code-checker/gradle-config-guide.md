# Gradle配置指南: CodeChecker Android项目

**Date**: 2025-11-27
**Feature**: CodeChecker Android应用 - Python代码查重助手
**Reference**: tasks.md (T002), quickstart.md
**Purpose**: 提供完整的Gradle构建配置模板和依赖管理指南

---

## 概述

本指南提供CodeChecker Android项目的完整Gradle配置，包括版本管理、依赖声明、构建优化和代码质量工具配置。

### 项目结构

```
project-root/
├── gradle/                    # Gradle wrapper
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle.kts          # 项目级构建配置
├── settings.gradle.kts       # 项目设置
├── gradle.properties         # Gradle属性
├── gradlew                   # Gradle wrapper脚本 (Unix)
├── gradlew.bat               # Gradle wrapper脚本 (Windows)
└── app/
    ├── build.gradle.kts      # 应用级构建配置
    └── proguard-rules.pro    # 代码混淆规则
```

---

## 1. 版本目录 (Version Catalog)

### 1.1 创建版本目录

**文件**: `gradle/libs.versions.toml`

```toml
[versions]
# Kotlin
kotlin = "1.9.10"
kotlinCoroutines = "1.7.3"

# Android
androidGradlePlugin = "8.1.4"
compileSdk = "34"
minSdk = "28"
targetSdk = "34"

# Compose
composeBom = "2023.10.01"
composeCompiler = "1.5.4"

# Core Libraries
coreKtx = "1.12.0"
junit = "4.13.2"
junitVersion = "1.1.5"
espressoCore = "3.5.1"

# Activity Compose
activityCompose = "1.8.2"

# Navigation
navigationCompose = "2.7.5"

# Lifecycle
lifecycleViewmodel = "2.7.0"
lifecycleRuntime = "2.7.0"
lifecycleLivedata = "2.7.0"

# Hilt
hilt = "2.48"
hiltNavigationCompose = "1.1.0"

# Room
room = "2.6.1"

# DataStore
datastore = "1.0.0"

# OkHttp
okhttp = "4.12.0"

# Serialization
kotlinxSerialization = "1.6.2"

# Testing
kotlinxCoroutinesTest = "1.7.3"
roomTesting = "2.6.1"
mockitoKotlin = "5.1.0"
mockitoCore = "5.7.0"

# Code Quality
ktlint = "12.1.0"
detekt = "1.23.4"

# Accompanist (optional - for permissions, etc.)
accompanist = "0.32.0"

[libraries]
# Kotlin
kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
kotlinx-coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "kotlinCoroutines" }
kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "kotlinCoroutines" }
kotlinx-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "kotlinCoroutines" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

# Android Core
androidx-core-ktx = { module = "androidx.core:core-ktx", version.ref = "coreKtx" }
androidx-activity = { module = "androidx.activity:activity", version.ref = "activityCompose" }

# Compose
androidx-compose-bom = { module = "androidx.compose:compose-bom", version.ref = "composeBom" }
androidx-compose-ui = { module = "androidx.compose.ui:ui" }
androidx-compose-ui-tooling = { module = "androidx.compose.ui:ui-tooling-preview" }
androidx-compose-material3 = { module = "androidx.compose.material3:material3" }
androidx-compose-runtime = { module = "androidx.compose.runtime:runtime" }
androidx-compose-runtime-livedata = { module = "androidx.compose.runtime:runtime-livedata" }
androidx-compose-ui-test-junit4 = { module = "androidx.compose.ui:ui-test-junit4" }
androidx-compose-ui-test-manifest = { module = "androidx.compose.ui:ui-test-manifest" }

# Navigation
androidx-navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "navigationCompose" }
androidx-hilt-navigation-compose = { module = "androidx.hilt:hilt-navigation-compose", version.ref = "hiltNavigationCompose" }

# Lifecycle
androidx-lifecycle-viewmodel = { module = "androidx.lifecycle:lifecycle-viewmodel-ktx", version.ref = "lifecycleViewmodel" }
androidx-lifecycle-runtime = { module = "androidx.lifecycle:lifecycle-runtime-ktx", version.ref = "lifecycleRuntime" }
androidx-lifecycle-livedata = { module = "androidx.lifecycle:lifecycle-livedata-ktx", version.ref = "lifecycleLivedata" }

# Hilt
google-hilt-android = { module = "com.google.dagger:hilt-android", version.ref = "hilt" }
google-hilt-compiler = { module = "com.google.dagger:hilt-compiler", version.ref = "hilt" }
androidx-hilt-compiler = { module = "androidx.hilt:hilt-compiler", version.ref = "hilt" }

# Room
androidx-room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
androidx-room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
androidx-room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
androidx-room-testing = { module = "androidx.room:room-testing", version.ref = "roomTesting" }

# DataStore
androidx-datastore-preferences = { module = "androidx.datastore:datastore-preferences", version.ref = "datastore" }

# Network
com-squareup-okhttp3-okhttp = { module = "com.squareup.okhttp3:okhttp", version.ref = "okhttp" }
com-squareup-okhttp3-logging-interceptor = { module = "com.squareup.okhttp3:logging-interceptor", version.ref = "okhttp" }

# Testing
junit = { module = "junit:junit", version.ref = "junit" }
androidx-test-ext-junit = { module = "androidx.test.ext:junit", version.ref = "junitVersion" }
androidx-test-espresso-core = { module = "androidx.test.espresso:espresso-core", version.ref = "espressoCore" }
mockito-kotlin = { module = "org.mockito.kotlin:mockito-kotlin", version.ref = "mockitoKotlin" }
mockito-core = { module = "org.mockito:mockito-core", version.ref = "mockitoCore" }

# Accompanist
accompanist-permissions = { module = "com.google.accompanist:accompanist-permissions", version.ref = "accompanist" }
accompanist-systemuicontroller = { module = "com.google.accompanist:accompanist-systemuicontroller", version.ref = "accompanist" }

[bundles]
compose = ["androidx-compose-ui", "androidx-compose-material3", "androidx-compose-runtime", "androidx-compose-ui-tooling"]
navigation = ["androidx-navigation-compose", "androidx-hilt-navigation-compose"]
lifecycle = ["androidx-lifecycle-viewmodel", "androidx-lifecycle-runtime", "androidx-lifecycle-livedata"]
room = ["androidx-room-runtime", "androidx-room-ktx"]
coroutines = ["kotlinx-coroutines-android", "kotlinx-coroutines-core"]

[plugins]
android-application = { id = "com.android.application", version.ref = "androidGradlePlugin" }
android-library = { id = "com.android.library", version.ref = "androidGradlePlugin" }
kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
kotlin-jvm = { id = "org.jetbrains.kotlin.jvm", version.ref = "kotlin" }
kotlin-kapt = { id = "org.jetbrains.kotlin.kapt", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
dagger-hilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
kotlin-parcelize = { id = "org.jetbrains.kotlin.plugin.parcelize", version.ref = "kotlin" }
detekt = { id = "io.gitlab.arturbosch.detekt", version.ref = "detekt" }
ktlint = { id = "org.jlleitschuh.gradle.ktlint", version.ref = "ktlint" }
```

---

## 2. 项目级构建配置

### 2.1 build.gradle.kts (项目根目录)

```kotlin
// project-root/build.gradle.kts

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}

allprojects {
    apply(plugin = alias(libs.plugins.detekt))
    detekt {
        config = files("${project.rootDir}/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
    }

    apply(plugin = alias(libs.plugins.ktlint))
    ktlint {
        debug.set(false)
        version.set(libs.versions.ktlint.get())
        ignoreFailures.set(false)
        filter {
            exclude("**/build/**")
            include("**/kotlin/**")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
```

### 2.2 settings.gradle.kts

```kotlin
// project-root/settings.gradle.kts

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "CodeChecker"
include(":app")
```

### 2.3 gradle.properties

```properties
# project-root/gradle.properties

# 项目配置
PROJECT_NAME=CodeChecker
PACKAGE_NAME=com.example.codechecker
VERSION_CODE=1
VERSION_NAME=1.0.0

# Kotlin配置
kotlin.code.style=official
kotlin.incremental=true
kotlin.incremental.android=true
kotlin.incremental.android.build.cache=true
kotlin.incremental.js=true
kotlin.incremental.js.klib=true

# Android配置
android.useAndroidX=true
android.enableJetifier=true
android.nonTransitiveRClass=true

# 编译配置
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.configureondemand=true
org.gradle.caching=true
org.gradle.daemon=true

# 构建优化
android.enableR8.fullMode=true
android.enableResourceShrinking=true

# 测试配置
android.testInstrumentationRunner=androidx.test.runner.AndroidJUnitRunner
android.enableUnitTestBinaryResources=true

# Kapt配置
kapt.use.worker.api=true
kapt.include.compile.classpath=false
kapt.mapstruct.defaultComponentModel=default
kapt.mapstruct.defaultAnnotationProcessorOption=mapstruct.defaultComponentModel=default

# 日志配置
org.gradle.logging.level=info

# JVM目标版本
java.sourceCompatibility=JavaVersion.VERSION_17
java.targetCompatibility=JavaVersion.VERSION_17
```

---

## 3. 应用级构建配置

### 3.1 app/build.gradle.kts

```kotlin
// app/build.gradle.kts

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.dagger.hilt.android)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.example.codechecker"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.codechecker"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 构建配置
        buildConfigField("String", "BUILD_TIME", "\"${System.currentTimeMillis()}\"")
        buildConfigField("boolean", "ENABLE_LOGGING", "true")

        // AI API密钥占位符（实际值在local.properties中）
        buildConfigField("String", "DEEPSEEK_API_KEY", "\"${properties.getProperty("DEEPSEEK_API_KEY", "")}\"")
        buildConfigField("String", "QWEN_API_KEY", "\"${properties.getProperty("QWEN_API_KEY", "")}\"")
        buildConfigField("String", "MODELSCOPE_API_KEY", "\"${properties.getProperty("MODELSCOPE_API_KEY", "")}\"")
    }

    signingConfigs {
        create("release") {
            val localProperties = Properties()
            localProperties.load(project.rootProject.file("local.properties").inputStream())

            storeFile = file(localProperties.getProperty("KEYSTORE_FILE", "keystore.jks"))
            storePassword = localProperties.getProperty("KEYSTORE_PASSWORD", "")
            keyAlias = localProperties.getProperty("KEY_ALIAS", "")
            keyPassword = localProperties.getProperty("KEY_PASSWORD", "")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
        }

        create("benchmark") {
            initWith(buildTypes.getByName("release"))
            matchingFallbacks += "release"
            isDebuggable = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs = freeCompilerArgs + listOf(
            // 启用所有编译器警告
            "-Xwarn-unused",
            // Compose编译器优化
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            // 启用序列化
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
            // 启用实验性API
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            // Hilt实验性API
            "-opt-in=dagger.hilt.ExperimentalGetContext"
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
        animationsDisabled = true
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
        disable += setOf("UnusedResources", "UnusedManifestResources")
    }

    bundle {
        language {
            enableSplit = false
        }
    }

    kotlin {
        compilerOptions {
            // 启用R8完整模式
            freeCompilerArgs += listOf(
                "-Xuse-ir",
                "-Xbackend-threads=4",
                "-XXLanguage:+InlineClasses",
                "-XXLanguage:+SealedInterfaces"
            )
        }
    }
}

dependencies {
    // Core library desugaring (Java 8+ API support)
    coreLibraryDesugaring(libs.androidx.core.ktx)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.runtime.livedata)

    // Navigation
    implementation(libs.bundles.navigation)

    // Lifecycle
    implementation(libs.bundles.lifecycle)

    // Room
    implementation(libs.bundles.room)
    kapt(libs.androidx.room.compiler)
    androidTestImplementation(libs.androidx.room.testing)

    // Hilt
    implementation(libs.google.hilt.android)
    kapt(libs.google.hilt.compiler)
    kapt(libs.androidx.hilt.compiler)

    // Coroutines
    implementation(libs.bundles.coroutines)
    testImplementation(libs.kotlinx.coroutines.test)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Activity Compose
    implementation(libs.androidx.activity)

    // Network (for AI features)
    implementation(libs.com.squareup.okhttp3.okhttp)
    implementation(libs.com.squareup.okhttp3.logging.interceptor)

    // Serialization
    implementation(libs.kotlinx.serialization.json)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}

// Kapt配置
kapt {
    correctErrorTypes = true
    includeCompileClasspath = false
    javacOptions {
        option("-Xmaxerrs", 500)
    }
}

// Hilt配置
kapt {
    generateStubs = true
}
```

---

## 4. 代码质量工具配置

### 4.1 Ktlint配置

**文件**: `.editorconfig`

```ini
# EditorConfig配置文件

root = true

[*]
charset = utf-8
end_of_line = lf
indent_size = 4
indent_style = space
insert_final_newline = true
trim_trailing_whitespace = true

[*.{kt,kts}]
ij_kotlin_allow_trailing_comma = true
ij_kotlin_allow_trailing_comma_on_call_site = true
ij_kotlin_imports_layout = *

[*.{yml,yaml}]
indent_size = 2
```

### 4.2 Detekt配置

**文件**: `config/detekt/detekt.yml`

```yaml
# Detekt静态代码分析配置

config:
  validation: true
  warningsAsErrors: false
  checkExhaustiveness: false
  excludes:
    - '.*/build/.*'
    - '.*/test/.*'
    - '.*/androidTest/.*'

build:
  maxIssues: 0
  weights:
    # 复杂度
    CyclomaticComplexity: 1
    LongParameterList: 0
    # 样式
    MagicNumber: -1
    LateinitUsage: -1

style:
  MagicNumber:
    excludes:
      - '**/test/**'
      - '**/androidTest/**'
      - '**/algorithm/**'
  LateinitUsage:
    active: false
  UnusedImports:
    active: true
    excludes:
      - '**/test/**'
      - '**/androidTest/**'
  MaxLineLength:
    excludes:
      - '**/test/**'
      - '**/androidTest/**'

complexity:
  TooManyFunctions:
    excludes:
      - '**/ui/screens/**'
      - '**/ui/components/**'
  CyclomaticComplexity:
    excludes:
      - '**/algorithm/**'
  LongMethod:
    excludes:
      - '**/ui/screens/**'
      - '**/ui/components/**'

performance:
  foreachOnArrayCallToIsEmptyFunction:
    active: true
  SpreadOperator:
    active: true
```

---

## 5. ProGuard混淆配置

### 5.1 app/proguard-rules.pro

```proguard
# 代码混淆规则

# =========================
# Android相关
# =========================

# 保留Application类
-keep class com.example.codechecker.CodeCheckerApp { *; }

# 保留Activity
-keep class * extends androidx.activity.ComponentActivity { *; }
-keep class * extends androidx.fragment.app.Fragment { *; }

# =========================
# Kotlin相关
# =========================

# 保留Kotlin元数据
-keepattributes RuntimeVisibleAnnotations,RuntimeVisibleParameterAnnotations,RuntimeVisibleTypeAnnotations
-keepclassmembers class ** {
    @org.jetbrains.annotations.NotNull <fields>;
    @org.jetbrains.annotations.Nullable <fields>;
}

# 保留data class
-keep class kotlin.Metadata { *; }
-keepclassmembers class * {
    public <methods>;
}

# 保留枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# =========================
# Hilt相关
# =========================

# 保留Hilt生成的类
-keep class * extends dagger.hilt.internal.GeneratedComponent
-keep class **_HiltModules*
-keep class **_Decorated*
-keep class **_Factory*
-keep class **_MembersInjector*
-keep class **_Impl*

# =========================
# Room相关
# =========================

# 保留Room实体和DAO
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *

# 保留类型转换器
-keep class * extends androidx.room.TypeConverter { *; }
-keepclassmembers class * {
    @androidx.room.TypeConverter <methods>;
}

# =========================
# 序列化相关
# =========================

# 保留Kotlin序列化类
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

-keep @kotlinx.serialization.Serializable class * {
    *;
}

# 保留枚举的序列化字段
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
}

# =========================
# 网络库相关 (OkHttp)
# =========================

# OkHttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

# =========================
# 应用业务逻辑
# =========================

# 保留数据模型
-keep class com.example.codechecker.domain.model.** { *; }
-keep class com.example.codechecker.data.local.entity.** { *; }

# 保留算法相关（不混淆算法逻辑）
-keep class com.example.codechecker.algorithm.** { *; }

# =========================
# 移除日志
# =========================

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

-assumenosideeffects class kotlin.io.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# =========================
# 反射相关
# =========================

# 保留Parcelable实现
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}
```

---

## 6. 本地配置文件

### 6.1 local.properties (不提交到Git)

```properties
# 密钥库配置
KEYSTORE_FILE=keystore.jks
KEYSTORE_PASSWORD=your_keystore_password
KEY_ALIAS=codechecker
KEY_PASSWORD=your_key_password

# AI API密钥（选做功能）
DEEPSEEK_API_KEY=your_deepseek_api_key
QWEN_API_KEY=your_qwen_api_key
MODELSCOPE_API_KEY=your_modelscope_api_key
```

### 6.2 .gitignore更新

```gitignore
# Gradle相关
.gradle/
build/
!gradle-wrapper.jar
!**/src/main/**/build/
!**/src/test/**/build/

# 本地配置文件
local.properties

# 密钥库文件
*.jks
*.keystore

# API密钥
api-keys.properties
secrets.properties

# 构建产物
*.apk
*.aab
```

---

## 7. Gradle任务示例

### 7.1 常用Gradle任务

```bash
# 清理构建
./gradlew clean

# 构建Debug APK
./gradlew assembleDebug

# 构建Release APK
./gradlew assembleRelease

# 构建Bundle
./gradlew bundleRelease

# 运行测试
./gradlew test
./gradlew testDebugUnitTest

# 运行集成测试（需要连接设备）
./gradlew connectedAndroidTest

# 检查代码质量
./gradlew detekt
./gradlew ktlintFormat

# 依赖分析
./gradlew app:dependencies

# 性能分析
./gradlew --profile --offline app:assembleDebug
```

### 7.2 自定义任务示例

在`app/build.gradle.kts`中添加：

```kotlin
tasks.register("printVersionInfo") {
    doLast {
        println("Version Code: ${android.defaultConfig.versionCode}")
        println("Version Name: ${android.defaultConfig.versionName}")
        println("Build Type: ${buildType.name}")
    }
}

tasks.register("uploadToDevice") {
    dependsOn("installDebug")
    doLast {
        println("Installed to device")
    }
}
```

---

## 8. CI/CD配置示例

### 8.1 GitHub Actions

**文件**: `.github/workflows/android.yml`

```yaml
name: Android CI

on:
  push:
    branches: [ master, develop ]
  pull_request:
    branches: [ master ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Cache Gradle packages
      uses: actions/cache@v3
      with:
        path: |
          ~/.gradle/caches
          ~/.gradle/wrapper
        key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
        restore-keys: |
          ${{ runner.os }}-gradle-

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew

    - name: Run tests
      run: ./gradlew test

    - name: Generate test coverage
      run: ./gradlew jacocoTestReport

    - name: Run Detekt
      run: ./gradlew detekt

    - name: Upload coverage reports
      uses: codecov/codecov-action@v3
```

---

## 9. 性能优化建议

### 9.1 构建优化

```properties
# gradle.properties 优化配置

# 启用配置缓存
org.gradle.configuration-cache=true

# 启用文件系统监控
org.gradle.vfs.watch=true

# 并行编译
org.gradle.parallel=true

# 配置缓存
org.gradle.configuration-cache-problems=warn

# 缓存构建结果
org.gradle.caching=true

# 配置缓存
org.gradle.configureondemand=true

# 启用Build Cache
org.gradle.caching=true

# 启用Build Scan
org.gradle.build.scan.enabled=true
```

### 9.2 Kotlin编译器优化

```kotlin
# 在build.gradle.kts的kotlinOptions中
kotlinOptions {
    jvmTarget = "17"
    freeCompilerArgs = freeCompilerArgs + listOf(
        "-Xuse-ir",
        "-Xbackend-threads=4",
        "-XXLanguage:+InlineClasses",
        "-XXLanguage:+SealedInterfaces",
        "-XXLanguage:+PrimitiveDefaultValuesInContracts"
    )
}
```

---

## 10. 故障排除

### 10.1 常见问题

**问题1: Gradle sync失败**
```bash
# 解决方案
./gradlew clean
rm -rf .gradle
./gradlew --refresh-dependencies
```

**问题2: Kotlin编译器内存不足**
```properties
# 在gradle.properties中增加JVM内存
org.gradle.jvmargs=-Xmx8192m -XX:MaxPermSize=512m -XX:+HeapDumpOnOutOfMemoryError -Dfile.encoding=UTF-8
```

**问题3: Kapt编译慢**
```properties
# 启用Kapt并行编译
kapt.use.worker.api=true
kapt.include.compile.classpath=false
```

### 10.2 性能监控

```bash
# 启用Gradle Profiler
./gradlew --profile --offline assembleDebug

# 查看构建缓存状态
./gradlew --build-cache app:assembleDebug
```

---

## 总结

### 配置要点

1. **版本管理**: 使用Version Catalog集中管理依赖版本
2. **构建优化**: 配置缓存、并行构建、构建缓存
3. **代码质量**: Detekt静态分析、Ktlint代码格式化
4. **测试覆盖**: 单元测试、集成测试、UI测试
5. **混淆配置**: R8完整模式、应用专用规则

### 性能目标

- ✅ 增量构建 < 10秒
- ✅ 冷构建 < 60秒
- ✅ 测试执行 < 30秒

### 参考资料

- [Android Gradle Plugin文档](https://developer.android.com/studio/build)
- [Kotlin官方文档](https://kotlinlang.org/docs/gradle.html)
- [Detekt静态分析](https://detekt.dev/)
- [Ktlint代码规范](https://github.com/pinterest/ktlint)

---

**Gradle Configuration Guide Completed**: 2025-11-27
**Reference Documents**: tasks.md (T002), quickstart.md
**Configuration Files**: build.gradle.kts, gradle.properties, libs.versions.toml
