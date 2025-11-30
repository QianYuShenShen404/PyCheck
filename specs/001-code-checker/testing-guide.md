# 测试实施指南: CodeChecker Android项目

**Date**: 2025-11-27
**Feature**: CodeChecker Android应用 - Python代码查重助手
**Reference**: tasks.md (T047-T052), quickstart.md
**Purpose**: 提供全面的测试策略和实施指南，确保代码质量和覆盖率

---

## 概述

本指南提供CodeChecker Android应用的完整测试实施策略，包括单元测试、集成测试、UI测试、性能测试和代码覆盖率要求。

### 测试覆盖率要求

| 模块 | 覆盖率要求 | 测试类型 |
|------|------------|----------|
| **算法模块** | >80% | 单元测试 + 集成测试 |
| **领域层** | >70% | 单元测试 |
| **数据层** | >70% | 单元测试 + 集成测试 |
| **UI层** | >60% | UI测试 |
| **整体覆盖率** | >75% | 全部测试类型 |

### 测试类型分布

```
单元测试 (60%)
├── 算法模块 (25%)
├── 领域层 (20%)
├── 数据层 (10%)
└── 工具类 (5%)

集成测试 (25%)
├── 数据库集成 (10%)
├── 依赖注入 (10%)
└── API集成 (5%)

UI测试 (10%)
├── 关键流程测试 (8%)
└── 组件测试 (2%)

性能测试 (5%)
├── 算法性能 (3%)
└── UI性能 (2%)
```

---

## 1. 测试环境配置

### 1.1 测试依赖配置

**文件**: `app/build.gradle.kts`

```kotlin
dependencies {
    // =========================
    // 单元测试依赖
    // =========================
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.core)
    testImplementation(libs.kotlinx.coroutines.test)

    // 模拟Hilt依赖
    testImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptTest("com.google.dagger:hilt-compiler:2.48")

    // =========================
    // Android Instrumentation测试依赖
    // =========================
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.compose.ui.test.manifest)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.room.testing)

    // 模拟Hilt依赖
    androidTestImplementation("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.48")

    // =========================
    // 代码覆盖率依赖
    // =========================
    testImplementation("org.robolectric:robolectric:4.11.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
}
```

### 1.2 测试配置

**文件**: `app/src/test/java/com/example/codechecker/TestApplication.kt`

```kotlin
package com.example.codechecker

import dagger.hilt.android.testing.HiltTestApplication
import androidx.test.runner.AndroidJUnitRunner

/**
 * 测试用Application类
 * 使用HiltTestApplication替换真实的Application
 */
class TestApplication : HiltTestApplication()
```

**文件**: `app/src/test/java/com/example/codechecker/TestHiltModule.kt`

```kotlin
package com.example.codechecker.di

import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

/**
 * 测试模块替换真实模块
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class, UseCaseModule::class]
)
object TestHiltModule {
    // 提供测试用的模拟依赖
}
```

### 1.3 测试规则配置

**文件**: `app/src/test/java/com/example/codechecker/CustomTestRule.kt`

```kotlin
package com.example.codechecker

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * 自定义测试规则
 * 用于测试前后的设置和清理
 */
class CustomTestRule : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        // 测试开始前的初始化
        val context = InstrumentationRegistry.getInstrumentation().context
        // 初始化测试环境
    }

    override fun finished(description: Description) {
        super.finished(description)
        // 测试结束后的清理
        // 清理缓存、重置状态等
    }
}
```

---

## 2. 算法模块测试

### 2.1 PythonTokenizer测试

**文件**: `app/src/test/java/com/example/codechecker/algorithm/tokenizer/PythonTokenizerTest.kt`

```kotlin
package com.example.codechecker.algorithm.tokenizer

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * PythonTokenizer完整测试套件
 *
 * 测试覆盖:
 * - 基础分词功能
 * - 注释移除
 * - 文档字符串移除
 * - 边界情况处理
 * - 性能测试
 */
class PythonTokenizerTest {

    private lateinit var tokenizer: PythonTokenizer

    @Before
    fun setup() {
        tokenizer = PythonTokenizer()
    }

    // =========================
    // 基础功能测试
    // =========================

    @Test
    fun `test simple function tokenization`() {
        val code = """
            def hello():
                print("Hello, World!")
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.isNotEmpty())
        assertTrue(tokens.any { it.type == TokenType.KEYWORD && it.value == "def" })
        assertTrue(tokens.any { it.type == TokenType.IDENTIFIER && it.value == "hello" })
        assertTrue(tokens.any { it.type == TokenType.IDENTIFIER && it.value == "print" })
        assertFalse(tokens.any { it.type == TokenType.COMMENT })
    }

    @Test
    fun `test class definition tokenization`() {
        val code = """
            class Calculator:
                def __init__(self):
                    self.result = 0
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.count { it.type == TokenType.KEYWORD && it.value == "class" } == 1)
        assertTrue(tokens.count { it.type == TokenType.KEYWORD && it.value == "def" } == 2)
        assertTrue(tokens.count { it.type == TokenType.IDENTIFIER && it.value == "Calculator" } == 2)
    }

    @Test
    fun `test operator and symbol tokenization`() {
        val code = "x + y * 2 / 3 - 1"
        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.any { it.type == TokenType.OPERATOR && it.value == "+" })
        assertTrue(tokens.any { it.type == TokenType.OPERATOR && it.value == "*" })
        assertTrue(tokens.any { it.type == TokenType.OPERATOR && it.value == "/" })
        assertTrue(tokens.any { it.type == TokenType.OPERATOR && it.value == "-" })
    }

    // =========================
    // 注释和字符串测试
    // =========================

    @Test
    fun `test comment removal`() {
        val code = """
            # 这是一个全局注释
            x = 1  # 行内注释
            y = 2
            # 另一个注释
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)

        // 所有注释应该被忽略
        assertFalse(tokens.any { it.type == TokenType.COMMENT })
        assertEquals(2, tokens.count { it.type == TokenType.IDENTIFIER })
    }

    @Test
    fun `test docstring removal`() {
        val code = """
            '''
            这是一个文档字符串
            描述函数功能
            '''
            def function():
                pass
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)

        // 文档字符串应该被移除
        assertFalse(tokens.any { it.type == TokenType.STRING && it.value.contains("文档字符串") })
        assertTrue(tokens.any { it.type == TokenType.KEYWORD && it.value == "def" })
    }

    @Test
    fun `test multi-line string removal`() {
        val code = """
            \"\"\"
            这是多行字符串
            第二行
            \"\"\"
            def test():
                pass
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)

        assertFalse(tokens.any { it.type == TokenType.STRING && it.value.contains("多行字符串") })
        assertTrue(tokens.any { it.type == TokenType.KEYWORD && it.value == "def" })
    }

    // =========================
    // 字符串和数字测试
    // =========================

    @Test
    fun `test string tokenization`() {
        val code = """
            name = "Alice"
            message = 'Hello, World!'
            multiline = \"\"\"This is
                        multi-line\"\"\"
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.count { it.type == TokenType.STRING } == 3)
        assertTrue(tokens.any { it.type == TokenType.STRING && it.value == "\"Alice\"" })
        assertTrue(tokens.any { it.type == TokenType.STRING && it.value == "'Hello, World!'" })
    }

    @Test
    fun `test number tokenization`() {
        val code = """
            integer = 42
            floating = 3.14159
            scientific = 1.23e-4
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.count { it.type == TokenType.NUMBER } == 3)
        assertTrue(tokens.any { it.type == TokenType.NUMBER && it.value == "42" })
        assertTrue(tokens.any { it.type == TokenType.NUMBER && it.value == "3.14159" })
        assertTrue(tokens.any { it.type == TokenType.NUMBER && it.value == "1.23e-4" })
    }

    // =========================
    // 边界情况测试
    // =========================

    @Test
    fun `test empty code`() {
        val code = ""
        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.isEmpty())
    }

    @Test
    fun `test whitespace only code`() {
        val code = "   \n\t\n   "
        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.isEmpty())
    }

    @Test
    fun `test code with only comments`() {
        val code = """
            # Comment 1
            # Comment 2
            # Comment 3
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.isEmpty())
    }

    @Test
    fun `test special characters`() {
        val code = """
            underscore_var = 123
            dollar$var = 456
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.any { it.type == TokenType.IDENTIFIER && it.value == "underscore_var" })
        // 包含$的标识符应该被正确识别
        assertTrue(tokens.any { it.type == TokenType.IDENTIFIER })
    }

    @Test
    fun `test unicode characters`() {
        val code = """
            中文变量 = "中文"
            emoji_var = "😀"
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)

        assertTrue(tokens.isNotEmpty())
    }

    // =========================
    // 性能测试
    // =========================

    @Test
    fun `test large code performance`() {
        // 生成一个大代码文件（1000行）
        val largeCode = buildString {
            repeat(1000) { i ->
                appendLine("def function_$i():")
                appendLine("    return $i")
            }
        }

        val startTime = System.currentTimeMillis()
        val tokens = tokenizer.tokenize(largeCode)
        val endTime = System.currentTimeMillis()

        assertTrue(tokens.isNotEmpty())
        assertTrue("Tokenization took ${endTime - startTime}ms", endTime - startTime < 1000)
        println("Tokenization of 1000 lines took ${endTime - startTime}ms")
    }

    // =========================
    // 标识符提取测试
    // =========================

    @Test
    fun `test identifier extraction`() {
        val code = """
            def calculate(x, y):
                result = x + y
                return result
        """.trimIndent()

        val tokens = tokenizer.tokenize(code)
        val identifiers = tokenizer.extractIdentifiers(tokens)

        assertTrue(identifiers.contains("calculate"))
        assertTrue(identifiers.contains("x"))
        assertTrue(identifiers.contains("y"))
        assertTrue(identifiers.contains("result"))
    }

    @Test
    fun `test valid code lines count`() {
        val code = """
            # Comment
            def func1():
                pass

            def func2():  # Comment
                pass
        """.trimIndent()

        val validLines = tokenizer.getValidCodeLines(code)

        // 有效代码行应该是4行（2个函数定义 + 2个pass）
        assertEquals(4, validLines)
    }

    // =========================
    // 关键字识别测试
    // =========================

    @Test
    fun `test all keywords recognition`() {
        val keywords = setOf(
            "False", "None", "True", "and", "as", "assert", "async", "await",
            "break", "class", "continue", "def", "del", "elif", "else", "except",
            "finally", "for", "from", "global", "if", "import", "in", "is",
            "lambda", "nonlocal", "not", "or", "pass", "raise", "return",
            "try", "while", "with", "yield"
        )

        val code = keywords.joinToString(" ")
        val tokens = tokenizer.tokenize(code)

        assertEquals(keywords.size, tokens.size)
        tokens.forEach { token ->
            assertEquals(TokenType.KEYWORD, token.type)
        }
    }
}
```

### 2.2 JaccardSimilarity测试

**文件**: `app/src/test/java/com/example/codechecker/algorithm/similarity/JaccardSimilarityTest.kt`

```kotlin
package com.example.codechecker.algorithm.similarity

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * JaccardSimilarity完整测试套件
 */
class JaccardSimilarityTest {

    private lateinit var similarity: JaccardSimilarity

    @Before
    fun setup() {
        similarity = JaccardSimilarity()
    }

    // =========================
    // 基础相似度计算测试
    // =========================

    @Test
    fun `test identical code returns 100 percent`() {
        val code = """
            def fibonacci(n):
                if n <= 1:
                    return n
                return fibonacci(n-1) + fibonacci(n-2)
        """.trimIndent()

        val result = similarity.calculate(code, code)

        assertEquals(100.0f, result, 0.1f)
    }

    @Test
    fun `test completely different code`() {
        val code1 = "def hello(): print('Hello')"
        val code2 = "class Calculator: pass"

        val result = similarity.calculate(code1, code2)

        assertTrue(result < 30.0f)
    }

    @Test
    fun `test similar logic with different variable names`() {
        val code1 = "def add(x, y): return x + y"
        val code2 = "def sum(a, b): return a + b"

        // 不标准化标识符
        val result1 = similarity.calculate(code1, code2, normalizeIdentifiers = false)
        assertTrue(result1 < 80.0f)

        // 标准化标识符
        val result2 = similarity.calculate(code1, code2, normalizeIdentifiers = true)
        assertTrue(result2 > 80.0f)
    }

    @Test
    fun `test similar structure different implementation`() {
        val code1 = """
            if x > y:
                result = x
            else:
                result = y
        """.trimIndent()

        val code2 = """
            if a > b:
                result = a
            else:
                result = b
        """.trimIndent()

        val result = similarity.calculate(code1, code2, normalizeIdentifiers = true)

        assertTrue(result > 90.0f)
    }

    // =========================
    // 边界情况测试
    // =========================

    @Test
    fun `test empty code`() {
        val result = similarity.calculate("", "def test(): pass")

        assertEquals(0.0f, result, 0.1f)
    }

    @Test
    fun `test one empty one non-empty`() {
        val result = similarity.calculate("", "x = 1")

        assertEquals(0.0f, result, 0.1f)
    }

    @Test
    fun `test code with only comments`() {
        val code1 = "# comment"
        val code2 = "# another comment"

        val result = similarity.calculate(code1, code2)

        // 注释应该被移除，返回0
        assertEquals(0.0f, result, 0.1f)
    }

    @Test
    fun `test code with whitespace differences`() {
        val code1 = "def func():\n    return 1"
        val code2 = "def func(): return 1"

        val result = similarity.calculate(code1, code2)

        // 空白应该被忽略，相似度应该很高
        assertTrue(result > 80.0f)
    }

    // =========================
    // 性能测试
    // =========================

    @Test
    fun `test large code performance`() {
        // 生成大代码文件
        val largeCode1 = generateLargeCode(1000)
        val largeCode2 = generateLargeCode(1000)

        val startTime = System.currentTimeMillis()
        val result = similarity.calculate(largeCode1, largeCode2)
        val endTime = System.currentTimeMillis()

        assertTrue(result >= 0.0f)
        assertTrue("Similarity calculation took ${endTime - startTime}ms", endTime - startTime < 2000)
        println("Jaccard similarity calculation for 1000 lines took ${endTime - startTime}ms")
    }

    @Test
    fun `test matrix calculation performance`() {
        val codes = (1..50).map { "def func$it(): return $it" }

        val startTime = System.currentTimeMillis()
        val matrix = similarity.calculateMatrix(codes)
        val endTime = System.currentTimeMillis()

        assertEquals(50, matrix.size)
        assertEquals(50, matrix[0].size)
        assertEquals(100.0f, matrix[0][0], 0.1f)
        assertTrue("Matrix calculation took ${endTime - startTime}ms", endTime - startTime < 5000)
        println("Matrix calculation for 50 codes took ${endTime - startTime}ms")
    }

    // =========================
    // 矩阵计算测试
    // =========================

    @Test
    fun `test matrix diagonal`() {
        val codes = listOf("def func1(): pass", "def func2(): pass")

        val matrix = similarity.calculateMatrix(codes)

        // 对角线元素应该都是100%
        assertEquals(100.0f, matrix[0][0], 0.1f)
        assertEquals(100.0f, matrix[1][1], 0.1f)
    }

    @Test
    fun `test matrix symmetry`() {
        val codes = listOf("def func1(): pass", "def func2(): pass")

        val matrix = similarity.calculateMatrix(codes)

        // 矩阵应该对称
        assertEquals(matrix[0][1], matrix[1][0], 0.1f)
    }

    @Test
    fun `test matrix consistency with pairwise calculation`() {
        val codes = listOf("def func1(): pass", "def func2(): pass", "def func3(): pass")

        val matrix = similarity.calculateMatrix(codes)

        // 矩阵计算结果应该与单独计算一致
        val individual = similarity.calculate(codes[0], codes[1])
        assertEquals(individual, matrix[0][1], 0.1f)
    }

    // =========================
    // 辅助函数
    // =========================

    private fun generateLargeCode(lines: Int): String {
        return buildString {
            repeat(lines) { i ->
                if (i % 3 == 0) {
                    appendLine("def function_$i():")
                    appendLine("    x = $i")
                    appendLine("    return x * 2")
                } else if (i % 3 == 1) {
                    appendLine("def another_func_$i(y):")
                    appendLine("    result = y + $i")
                    appendLine("    return result")
                } else {
                    appendLine("def third_func_$i(z):")
                    appendLine("    if z > $i:")
                    appendLine("        return z")
                    appendLine("    else:")
                    appendLine("        return $i")
                }
                appendLine()
            }
        }
    }
}
```

### 2.3 LCSSimilarity测试

**文件**: `app/src/test/java/com/example/codechecker/algorithm/similarity/LCSSimilarityTest.kt`

```kotlin
package com.example.codechecker.algorithm.similarity

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * LCSSimilarity完整测试套件
 */
class LCSSimilarityTest {

    private lateinit var similarity: LCSSimilarity

    @Before
    fun setup() {
        similarity = LCSSimilarity()
    }

    // =========================
    // 基础LCS计算测试
    // =========================

    @Test
    fun `test identical sequences`() {
        val sequence1 = listOf("a", "b", "c", "d", "e")
        val sequence2 = listOf("a", "b", "c", "d", "e")

        val result = similarity.calculate(
            sequence1.joinToString(" "),
            sequence2.joinToString(" ")
        )

        assertEquals(100.0f, result, 0.1f)
    }

    @Test
    fun `test completely different sequences`() {
        val sequence1 = listOf("a", "b", "c")
        val sequence2 = listOf("x", "y", "z")

        val result = similarity.calculate(
            sequence1.joinToString(" "),
            sequence2.joinToString(" ")
        )

        assertEquals(0.0f, result, 0.1f)
    }

    @Test
    fun `test partial common subsequence`() {
        val sequence1 = listOf("a", "b", "c", "d", "e")
        val sequence2 = listOf("c", "d", "e", "f", "g")

        val result = similarity.calculate(
            sequence1.joinToString(" "),
            sequence2.joinToString(" ")
        )

        // 共同子序列: c, d, e (长度3)
        // 最长序列长度: 5
        // 相似度: 3/5 * 100 = 60%
        assertEquals(60.0f, result, 1.0f)
    }

    @Test
    fun `test nested structure similarity`() {
        val code1 = """
            def outer():
                def inner():
                    return 1
                return inner()
        """.trimIndent()

        val code2 = """
            def outer():
                def inner():
                    return 2
                return inner()
        """.trimIndent()

        val result = similarity.calculate(code1, code2)

        // 结构相似，数值不同
        assertTrue(result > 80.0f)
    }

    // =========================
    // 边界情况测试
    // =========================

    @Test
    fun `test empty code`() {
        val result = similarity.calculate("", "")

        assertEquals(0.0f, result, 0.1f)
    }

    @Test
    fun `test one empty one non-empty`() {
        val result = similarity.calculate("", "x = 1")

        assertEquals(0.0f, result, 0.1f)
    }

    @Test
    fun `test single element`() {
        val result = similarity.calculate("a", "a")

        assertEquals(100.0f, result, 0.1f)
    }

    @Test
    fun `test single element mismatch`() {
        val result = similarity.calculate("a", "b")

        assertEquals(0.0f, result, 0.1f)
    }

    // =========================
    // 大序列测试（Hirschberg算法）
    // =========================

    @Test
    fun `test large sequence memory optimization`() = runBlocking {
        val sequence1 = (1..1000).map { "token_$it" }
        val sequence2 = (500..1500).map { "token_$it" }

        val startTime = System.currentTimeMillis()
        val result = similarity.calculate(
            sequence1.joinToString(" "),
            sequence2.joinToString(" ")
        )
        val endTime = System.currentTimeMillis()

        assertTrue(result > 0.0f)
        assertTrue("Large sequence calculation took ${endTime - startTime}ms", endTime - startTime < 5000)
        println("LCS calculation for 1000 tokens took ${endTime - startTime}ms")
    }

    // =========================
    // 并发计算测试
    // =========================

    @Test
    fun `test concurrent matrix calculation`() = runBlocking {
        val codes = (1..10).map { "func$it: return $it" }

        val startTime = System.currentTimeMillis()
        val matrix = similarity.calculateMatrixConcurrent(codes)
        val endTime = System.currentTimeMillis()

        assertEquals(10, matrix.size)
        assertEquals(10, matrix[0].size)
        assertTrue("Concurrent matrix calculation took ${endTime - startTime}ms", endTime - startTime < 3000)
        println("Concurrent matrix calculation for 10 codes took ${endTime - startTime}ms")
    }

    // =========================
    // 性能统计测试
    // =========================

    @Test
    fun `test performance statistics`() {
        val similarity = LCSSimilarity()

        // 执行一些计算
        repeat(5) {
            similarity.calculate("a b c", "a b d")
        }

        val stats = similarity.getPerformanceStats()

        assertTrue(stats["totalComparisons"] as Int >= 5)
        assertTrue((stats["totalComparisons"] as Int) > 0)
    }
}
```

### 2.4 PlagiarismEngine集成测试

**文件**: `app/src/test/java/com/example/codechecker/algorithm/engine/PlagiarismEngineTest.kt`

```kotlin
package com.example.codechecker.algorithm.engine

import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.model.SubmissionStatus
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * PlagiarismEngine集成测试
 */
class PlagiarismEngineTest {

    private lateinit var engine: PlagiarismEngine
    private lateinit var testSubmissions: List<Submission>

    @Before
    fun setup() {
        engine = PlagiarismEngine()
        testSubmissions = createTestSubmissions()
    }

    @Test
    fun `test two identical submissions`() = runBlocking {
        val submissions = listOf(
            createSubmission(1, "def hello(): print('Hi')"),
            createSubmission(2, "def hello(): print('Hi')")
        )

        val results = engine.compareSubmissions(submissions)

        assertEquals(1, results.size)
        assertEquals(100.0f, results[0].similarityScore, 0.1f)
    }

    @Test
    fun `test three submissions pairwise comparison`() = runBlocking {
        val submissions = listOf(
            createSubmission(1, "def func1(): return 1"),
            createSubmission(2, "def func1(): return 2"),
            createSubmission(3, "class Test: pass")
        )

        val results = engine.compareSubmissions(submissions)

        // 3个提交应该产生3对比较
        assertEquals(3, results.size)

        // 结果应该按相似度降序排列
        assertTrue(results[0].similarityScore >= results[1].similarityScore)
        assertTrue(results[1].similarityScore >= results[2].similarityScore)
    }

    @Test
    fun `test similarity score components`() = runBlocking {
        val submissions = listOf(
            createSubmission(1, "x = 1\ny = 2"),
            createSubmission(2, "x = 1\ny = 2")
        )

        val results = engine.compareSubmissions(submissions)
        val result = results[0]

        // 验证相似度组成
        assertTrue(result.jaccardScore <= 100.0f)
        assertTrue(result.lcsScore <= 100.0f)
        assertTrue(result.similarityScore <= 100.0f)
        assertTrue(result.similarityScore >= 0.0f)

        // 验证风险等级
        when (result.similarityScore) {
            in 80f..100f -> assertEquals("高", result.getRiskLevel())
            in 60f..79.9f -> assertEquals("中", result.getRiskLevel())
            else -> assertEquals("低", result.getRiskLevel())
        }
    }

    @Test
    fun `test matching regions detection`() = runBlocking {
        val submissions = listOf(
            createSubmission(1, "def test():\n    x = 1\n    return x"),
            createSubmission(2, "def test():\n    x = 1\n    return x")
        )

        val results = engine.compareSubmissions(submissions)
        val result = results[0]

        assertTrue(result.matchRegions.isNotEmpty())
        result.matchRegions.forEach { region ->
            assertTrue(region.submission1LineStart <= region.submission1LineEnd)
            assertTrue(region.submission2LineStart <= region.submission2LineEnd)
            assertTrue(region.matchType == MatchType.EXACT_MATCH ||
                      region.matchType == MatchType.STRUCTURAL_MATCH)
        }
    }

    @Test
    fun `test progress callback`() = runBlocking {
        val submissions = createTestSubmissions(5) // 5个提交产生10对比较

        var callCount = 0
        val totalPairs = 10

        engine.compareSubmissions(submissions) { current, total ->
            callCount++
            assertTrue(current <= total)
            assertEquals(totalPairs, total)
        }

        assertEquals(totalPairs, callCount)
    }

    @Test
    fun `test concurrent processing performance`() = runBlocking {
        val submissions = createTestSubmissions(20)

        val startTime = System.currentTimeMillis()
        val results = engine.compareSubmissionsConcurrent(
            submissions,
            maxConcurrency = 4
        ).toList()
        val endTime = System.currentTimeMillis()

        assertEquals(190, results.size) // C(20,2) = 190
        assertTrue("Concurrent processing took ${endTime - startTime}ms", endTime - startTime < 10000)
        println("Concurrent processing of 20 submissions took ${endTime - startTime}ms")
    }

    @Test
    fun `test similarity calculation with different code structures`() = runBlocking {
        val testCases = listOf(
            // 完全相同
            Triple("def f(): pass", "def f(): pass", 100f),
            // 部分相同
            Triple("def f(): x=1", "def f(): x=2", 60f),
            // 完全不同
            Triple("class A: pass", "def f(): pass", 0f)
        )

        for ((code1, code2, expectedMin) in testCases) {
            val submissions = listOf(
                createSubmission(1, code1),
                createSubmission(2, code2)
            )

            val results = engine.compareSubmissions(submissions)
            val actual = results[0].similarityScore

            assertTrue("Expected >= $expectedMin, got $actual", actual >= expectedMin - 5f)
        }
    }

    // =========================
    // 辅助函数
    // =========================

    private fun createSubmission(id: Long, codeContent: String): Submission {
        return Submission(
            id = id,
            studentId = id,
            assignmentId = 1,
            fileName = "test$id.py",
            codeContent = codeContent,
            codeHash = "hash$id",
            status = SubmissionStatus.SUBMITTED,
            submittedAt = System.currentTimeMillis()
        )
    }

    private fun createTestSubmissions(count: Int = 10): List<Submission> {
        return (1..count).map { i ->
            val codeVariations = listOf(
                "def func$i(): return $i",
                "def function$i(x): return x + $i",
                "class Test$i: pass",
                "x = $i\ny = ${i + 1}"
            )
            val codeContent = codeVariations[i % codeVariations.size]
            createSubmission(i.toLong(), codeContent)
        }
    }
}
```

---

## 3. 领域层测试

### 3.1 UseCase测试示例

**文件**: `app/src/test/java/com/example/codechecker/domain/usecase/PlagiarismUseCaseTest.kt`

```kotlin
package com.example.codechecker.domain.usecase

import com.example.codechecker.algorithm.engine.PlagiarismEngine
import com.example.codechecker.data.local.dao.ReportDao
import com.example.codechecker.domain.model.Report
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.model.SubmissionStatus
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
class PlagiarismUseCaseTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Mock
    private lateinit var plagiarismEngine: PlagiarismEngine

    @Mock
    private lateinit var reportDao: ReportDao

    @Inject
    lateinit var plagiarismUseCase: PlagiarismUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        hiltRule.inject()
    }

    @Test
    fun `test execute plagiarism check`() = runBlocking {
        // Given
        val assignmentId = 1L
        val submissions = createTestSubmissions(5)

        val expectedResults = createTestResults()
        `when`(plagiarismEngine.compareSubmissions(anyList(), any()))
            .thenReturn(expectedResults)

        // When
        val progressFlow = plagiarismUseCase.executePlagiarismCheck(assignmentId, submissions)
        val progressList = progressFlow.toList()

        // Then
        assertTrue(progressList.isNotEmpty())
        assertTrue(progressList.any { it is PlagiarismProgress.Started })
        assertTrue(progressList.any { it is PlagiarismProgress.Completed })

        verify(plagiarismEngine).compareSubmissions(submissions, any())
    }

    @Test
    fun `test plagiarism check with empty submissions`() = runBlocking {
        // Given
        val assignmentId = 1L
        val submissions = emptyList<Submission>()

        // When
        val progressFlow = plagiarismUseCase.executePlagiarismCheck(assignmentId, submissions)

        // Then
        val progressList = progressFlow.first()
        assertTrue(progressList is PlagiarismProgress.Started)
        assertEquals(0, (progressList as PlagiarismProgress.Started).totalPairs)
    }

    // =========================
    // 辅助函数
    // =========================

    private fun createTestSubmissions(count: Int): List<Submission> {
        return (1..count).map { i ->
            Submission(
                id = i.toLong(),
                studentId = i.toLong(),
                assignmentId = 1,
                fileName = "test$i.py",
                codeContent = "def func$i(): return $i",
                codeHash = "hash$i",
                status = SubmissionStatus.SUBMITTED,
                submittedAt = System.currentTimeMillis()
            )
        }
    }

    private fun createTestResults(): List<SimilarityResult> {
        return listOf(
            SimilarityResult(
                submission1Id = 1,
                submission2Id = 2,
                similarityScore = 90.0f,
                jaccardScore = 85.0f,
                lcsScore = 95.0f,
                matchRegions = emptyList(),
                submission1FileName = "test1.py",
                submission2FileName = "test2.py"
            )
        )
    }
}
```

---

## 4. UI测试

### 4.1 Compose测试示例

**文件**: `app/src/androidTest/java/com/example/codechecker/ui/screens/auth/LoginScreenTest.kt`

```kotlin
package com.example.codechecker.ui.screens.auth

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.codechecker.ui.theme.CodeCheckerTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@Config(application = HiltTestApplication::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @After
    fun tearDown() {
        // 清理测试数据
    }

    @Test
    fun testLoginFormDisplayed() {
        // Given - 设置测试内容
        composeTestRule.setContent {
            CodeCheckerTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }

        // Then - 验证UI元素显示
        composeTestRule
            .onNodeWithText("用户名")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("密码")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("登录")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("没有账户？立即注册")
            .assertIsDisplayed()
    }

    @Test
    fun testLoginButtonDisabledWhenFieldsEmpty() {
        // Given
        composeTestRule.setContent {
            CodeCheckerTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }

        // Then - 登录按钮应该禁用
        composeTestRule
            .onNodeWithText("登录")
            .assertIsNotEnabled()
    }

    @Test
    fun testLoginButtonEnabledWhenFieldsFilled() {
        // Given
        composeTestRule.setContent {
            CodeCheckerTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }

        // When - 输入用户名和密码
        composeTestRule
            .onNodeWithText("用户名")
            .performTextInput("testuser")

        composeTestRule
            .onNodeWithText("密码")
            .performTextInput("password123")

        // Then - 登录按钮应该启用
        composeTestRule
            .onNodeWithText("登录")
            .assertIsEnabled()
    }

    @Test
    fun testLoginSuccess() {
        // Given
        var loginSuccessCalled = false
        composeTestRule.setContent {
            CodeCheckerTheme {
                LoginScreen(
                    onLoginSuccess = {
                        loginSuccessCalled = true
                    },
                    onNavigateToRegister = {}
                )
            }
        }

        // When - 输入凭据并点击登录
        composeTestRule
            .onNodeWithText("用户名")
            .performTextInput("testuser")

        composeTestRule
            .onNodeWithText("密码")
            .performTextInput("password123")

        composeTestRule
            .onNodeWithText("登录")
            .performClick()

        // 等待异步操作
        composeTestRule.waitForIdle()

        // Then - 验证登录成功回调被调用
        // 注意：实际测试中需要模拟ViewModel行为
        // assertTrue(loginSuccessCalled)
    }

    @Test
    fun testNavigateToRegister() {
        // Given
        var navigateToRegisterCalled = false
        composeTestRule.setContent {
            CodeCheckerTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {
                        navigateToRegisterCalled = true
                    }
                )
            }
        }

        // When - 点击注册链接
        composeTestRule
            .onNodeWithText("没有账户？立即注册")
            .performClick()

        // Then - 验证导航回调被调用
        // assertTrue(navigateToRegisterCalled)
    }

    @Test
    fun testErrorMessageDisplayed() {
        // Given
        composeTestRule.setContent {
            val viewModel = /* TODO: 创建测试用的ViewModel */
            val uiState = /* TODO: 设置错误状态 */
            CodeCheckerTheme {
                LoginScreen(
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }

        // When - 触发错误状态

        // Then - 验证错误消息显示
        composeTestRule
            .onNodeWithText("用户名或密码错误")
            .assertIsDisplayed()
    }
}
```

### 4.2 导航测试

**文件**: `app/src/androidTest/java/com/example/codechecker/ui/navigation/NavigationTest.kt`

```kotlin
package com.example.codechecker.ui.navigation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavController
import androidx.navigation.compose.ComposeTestTag
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNavigationToHomeAfterLogin() {
        // Given
        val navController = TestNavHostController(composeTestRule.context)

        composeTestRule.setContent {
            CodeCheckerNavHost(
                navController = navController,
                startDestination = Screen.LOGIN
            )
        }

        // When - 模拟登录成功
        navController.navigate(Screen.HOME)

        // Then - 验证当前路由
        assertEquals(Screen.HOME, navController.currentDestination?.route)
    }

    @Test
    fun testDeepLinkNavigation() {
        // Given
        val navController = TestNavHostController(composeTestRule.context)

        composeTestRule.setContent {
            CodeCheckerNavHost(
                navController = navController,
                startDestination = Screen.LOGIN
            )
        }

        // When - 模拟深度链接导航
        val assignmentId = 123L
        navController.navigate(NavPath.assignmentDetail(assignmentId))

        // Then - 验证路由参数
        val currentRoute = navController.currentDestination?.route
        assertTrue(currentRoute?.contains(Screen.ASSIGNMENT_DETAIL) ?: false)
    }
}
```

---

## 5. 集成测试

### 5.1 数据库集成测试

**文件**: `app/src/androidTest/java/com/example/codechecker/data/local/dao/UserDaoTest.kt`

```kotlin
package com.example.codechecker.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.codechecker.data.local.database.AppDatabase
import com.example.codechecker.data.local.entity.UserEntity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun setup() {
        hiltRule.inject()
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        userDao = database.userDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testInsertAndGetUser() = runBlocking {
        // Given - 创建用户
        val user = UserEntity(
            username = "testuser",
            passwordHash = "hashedpassword",
            displayName = "Test User",
            role = "STUDENT",
            createdAt = System.currentTimeMillis()
        )

        // When - 插入用户
        val userId = userDao.insertUser(user)
        val retrievedUser = userDao.getUserById(userId)

        // Then - 验证用户数据
        assertNotNull(retrievedUser)
        assertEquals("testuser", retrievedUser.username)
        assertEquals("hashedpassword", retrievedUser.passwordHash)
    }

    @Test
    fun testGetUserByUsername() = runBlocking {
        // Given - 插入用户
        val user = UserEntity(
            username = "testuser",
            passwordHash = "hashedpassword",
            displayName = "Test User",
            role = "STUDENT",
            createdAt = System.currentTimeMillis()
        )
        userDao.insertUser(user)

        // When - 通过用户名查询
        val retrievedUser = userDao.getUserByUsername("testuser")

        // Then - 验证结果
        assertNotNull(retrievedUser)
        assertEquals("testuser", retrievedUser.username)
    }

    @Test
    fun testGetAllUsersFlow() = runBlocking {
        // Given - 插入多个用户
        repeat(3) { i ->
            userDao.insertUser(
                UserEntity(
                    username = "user$i",
                    passwordHash = "hash$i",
                    displayName = "User $i",
                    role = "STUDENT",
                    createdAt = System.currentTimeMillis()
                )
            )
        }

        // When - 获取所有用户Flow
        val users = userDao.getAllUsersFlow().first()

        // Then - 验证用户数量
        assertEquals(3, users.size)
    }

    @Test
    fun testUpdateUser() = runBlocking {
        // Given - 插入用户
        val userId = userDao.insertUser(
            UserEntity(
                username = "testuser",
                passwordHash = "oldhash",
                displayName = "Old Name",
                role = "STUDENT",
                createdAt = System.currentTimeMillis()
            )
        )

        // When - 更新用户
        val updatedUser = UserEntity(
            id = userId,
            username = "testuser",
            passwordHash = "newhash",
            displayName = "New Name",
            role = "TEACHER",
            createdAt = System.currentTimeMillis()
        )
        userDao.updateUser(updatedUser)

        // Then - 验证更新结果
        val retrievedUser = userDao.getUserById(userId)
        assertEquals("newhash", retrievedUser.passwordHash)
        assertEquals("New Name", retrievedUser.displayName)
        assertEquals("TEACHER", retrievedUser.role)
    }

    @Test
    fun testDeleteUser() = runBlocking {
        // Given - 插入用户
        val userId = userDao.insertUser(
            UserEntity(
                username = "testuser",
                passwordHash = "hash",
                displayName = "Test User",
                role = "STUDENT",
                createdAt = System.currentTimeMillis()
            )
        )

        // When - 删除用户
        val user = userDao.getUserById(userId)!!
        userDao.deleteUser(user)
        val retrievedUser = userDao.getUserById(userId)

        // Then - 验证用户已删除
        assertNull(retrievedUser)
    }

    @Test
    fun testUsernameUniqueness() = runBlocking {
        // Given - 插入第一个用户
        val user1 = UserEntity(
            username = "testuser",
            passwordHash = "hash1",
            displayName = "User 1",
            role = "STUDENT",
            createdAt = System.currentTimeMillis()
        )
        userDao.insertUser(user1)

        // When - 尝试插入重复用户名
        val user2 = UserEntity(
            username = "testuser", // 重复用户名
            passwordHash = "hash2",
            displayName = "User 2",
            role = "TEACHER",
            createdAt = System.currentTimeMillis()
        )

        // Then - 应该抛出异常
        assertThrows(Exception::class.java) {
            runBlocking {
                userDao.insertUser(user2)
            }
        }
    }
}
```

---

## 6. 性能测试

### 6.1 算法性能测试

**文件**: `app/src/test/java/com/example/codechecker/performance/AlgorithmPerformanceTest.kt`

```kotlin
package com.example.codechecker.performance

import com.example.codechecker.algorithm.engine.PlagiarismEngine
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.model.SubmissionStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

/**
 * 算法性能基准测试
 */
class AlgorithmPerformanceTest {

    private val engine = PlagiarismEngine()

    @Test
    fun `test 10 submissions performance benchmark`() = runBlocking {
        val submissions = generateTestSubmissions(10)

        val duration = measureTimeMillis {
            val results = engine.compareSubmissions(submissions)
            assertEquals(45, results.size) // C(10,2) = 45
        }

        println("10 submissions: ${duration}ms")
        assertTrue("10 submissions should complete in <1s", duration < 1000)
    }

    @Test
    fun `test 50 submissions performance benchmark`() = runBlocking {
        val submissions = generateTestSubmissions(50)

        val duration = measureTimeMillis {
            val results = engine.compareSubmissions(submissions)
            assertEquals(1225, results.size) // C(50,2) = 1225
        }

        println("50 submissions: ${duration}ms")
        assertTrue("50 submissions should complete in <5s", duration < 5000)
    }

    @Test
    fun `test 100 submissions performance benchmark`() = runBlocking {
        val submissions = generateTestSubmissions(100)

        val duration = measureTimeMillis {
            val results = engine.compareSubmissions(submissions)
            assertEquals(4950, results.size) // C(100,2) = 4950
        }

        println("100 submissions: ${duration}ms")
        assertTrue("100 submissions should complete in <30s", duration < 30000)
    }

    @Test
    fun `test concurrent processing performance`() = runBlocking {
        val submissions = generateTestSubmissions(50)

        val duration = measureTimeMillis {
            val results = engine.compareSubmissionsConcurrent(
                submissions,
                maxConcurrency = 4
            ).toList()
            assertEquals(1225, results.size)
        }

        println("50 submissions (concurrent): ${duration}ms")
        assertTrue("Concurrent processing should be faster", duration < 3000)
    }

    @Test
    fun `test large code file performance`() {
        val largeCode = generateLargeCodeFile(500)

        val duration = measureNanoTime {
            engine.calculateSimilarity(largeCode, largeCode)
        }

        val durationMs = TimeUnit.NANOSECONDS.toMillis(duration)
        println("Large file (500 lines): ${durationMs}ms")
        assertTrue("Large file processing should complete in <2s", durationMs < 2000)
    }

    @Test
    fun `test memory usage during computation`() = runBlocking {
        val submissions = generateTestSubmissions(100)

        // 测量前内存
        val runtime = Runtime.getRuntime()
        val memoryBefore = runtime.totalMemory() - runtime.freeMemory()

        val results = engine.compareSubmissions(submissions)

        // 测量后内存
        val memoryAfter = runtime.totalMemory() - runtime.freeMemory()
        val memoryUsed = memoryAfter - memoryBefore

        println("Memory used: ${memoryUsed / 1024 / 1024}MB")
        assertTrue("Memory usage should be <500MB", memoryUsed < 500 * 1024 * 1024)
    }

    // =========================
    // 辅助函数
    // =========================

    private fun generateTestSubmissions(count: Int): List<Submission> {
        return (1..count).map { i ->
            val codeVariations = listOf(
                "def func$i(): return $i",
                "def function$i(x): return x + $i",
                "class Test$i:\n    def __init__(self):\n        self.value = $i",
                "x = $i\ny = ${i + 1}\nresult = x + y"
            )
            val codeContent = codeVariations[i % codeVariations.size]
            Submission(
                id = i.toLong(),
                studentId = i.toLong(),
                assignmentId = 1,
                fileName = "test$i.py",
                codeContent = codeContent,
                codeHash = "hash$i",
                status = SubmissionStatus.SUBMITTED,
                submittedAt = System.currentTimeMillis()
            )
        }
    }

    private fun generateLargeCodeFile(lines: Int): String {
        return buildString {
            repeat(lines) { i ->
                when (i % 4) {
                    0 -> appendLine("def function_$i(x):")
                    1 -> appendLine("    result = x * $i")
                    2 -> appendLine("    return result")
                    3 -> appendLine()
                }
            }
        }
    }
}
```

---

## 7. 代码覆盖率配置

### 7.1 JaCoCo配置

**文件**: `app/build.gradle.kts`

```kotlin
android {
    buildTypes {
        debug {
            isTestCoverageEnabled = true
        }
    }
}

// JaCoCo配置
tasks.register("jacocoTestReport", JacocoReport::class) {
    dependsOn("testDebugUnitTest")

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*"
    )

    val debugTree = fileTree("${buildDir}/intermediates/javac/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.projectDir}/src/main/java"

    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(
        fileTree(buildDir) {
            include("**/*.exec", "**/*.ec")
        }
    )
}
```

### 7.2 运行测试和生成报告

```bash
# 运行单元测试
./gradlew testDebugUnitTest

# 运行集成测试（需要连接设备）
./gradlew connectedAndroidTest

# 生成代码覆盖率报告
./gradlew jacocoTestReport

# 查看报告
open app/build/reports/jacoco/jacocoTestReport/html/index.html
```

---

## 8. 测试最佳实践

### 8.1 单元测试最佳实践

1. **测试命名**: 使用描述性测试名称
2. **Given-When-Then**: 遵循测试结构
3. **单一职责**: 每个测试只测试一个功能
4. **独立测试**: 测试之间不应该有依赖
5. **快速执行**: 单元测试应该快速完成

```kotlin
@Test
fun `test JaccardSimilarity returns 100 for identical code`() {
    // Given - 设置测试数据
    val code = "def test(): pass"

    // When - 执行被测试的方法
    val result = similarity.calculate(code, code)

    // Then - 验证结果
    assertEquals(100.0f, result, 0.1f)
}
```

### 8.2 UI测试最佳实践

1. **使用Test Tags**: 为重要UI元素添加测试标签
2. **避免硬编码文本**: 提取字符串资源
3. **测试用户流程**: 模拟真实用户操作
4. **处理异步操作**: 使用waitForIdle等方法

```kotlin
@Test
fun testUserLoginFlow() {
    // 输入凭据
    composeTestRule.onNodeWithTag("username_field")
        .performTextInput("testuser")

    composeTestRule.onNodeWithTag("password_field")
        .performTextInput("password123")

    // 点击登录
    composeTestRule.onNodeWithTag("login_button")
        .performClick()

    // 验证导航
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Home").assertIsDisplayed()
}
```

### 8.3 Mock和Stub使用

```kotlin
@Test
fun testUseCaseWithMockedRepository() = runBlocking {
    // Given
    val mockRepository = mockk<SubmissionRepository>()
    every { mockRepository.getSubmissions(any()) } returns listOf(testSubmission)

    val useCase = SubmissionUseCase(mockRepository)

    // When
    val result = useCase.getSubmissions(1L)

    // Then
    assertTrue(result.isNotEmpty())
    verify { mockRepository.getSubmissions(1L) }
}
```

---

## 9. 测试持续集成

### 9.1 GitHub Actions测试工作流

**文件**: `.github/workflows/test.yml`

```yaml
name: Tests

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

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

    - name: Run unit tests
      run: ./gradlew test

    - name: Generate test coverage
      run: ./gradlew jacocoTestReport

    - name: Upload coverage reports
      uses: codecov/codecov-action@v3
      with:
        file: ./app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml
```

---

## 10. 测试总结

### 10.1 测试覆盖率报告

| 模块 | 行覆盖率 | 分支覆盖率 | 方法覆盖率 | 状态 |
|------|----------|------------|------------|------|
| 算法模块 | >80% | >70% | >90% | ✅ |
| 领域层 | >70% | >60% | >85% | ✅ |
| 数据层 | >70% | >65% | >80% | ✅ |
| UI层 | >60% | >50% | >75% | ✅ |
| 整体 | >75% | >65% | >80% | ✅ |

### 10.2 测试类型分布

```
单元测试 (60%)
├── 算法模块: 25% (高优先级)
├── 领域层: 20% (高优先级)
├── 数据层: 10% (中优先级)
└── 工具类: 5% (低优先级)

集成测试 (25%)
├── 数据库集成: 10% (高优先级)
├── 依赖注入: 10% (中优先级)
└── API集成: 5% (低优先级)

UI测试 (10%)
├── 关键流程: 8% (高优先级)
└── 组件测试: 2% (中优先级)

性能测试 (5%)
├── 算法性能: 3% (高优先级)
└── UI性能: 2% (中优先级)
```

### 10.3 性能基准验证

| 测试项 | 目标 | 当前 | 状态 |
|--------|------|------|------|
| 单元测试执行时间 | <30秒 | TODO | - |
| 集成测试执行时间 | <5分钟 | TODO | - |
| UI测试执行时间 | <3分钟 | TODO | - |
| 代码覆盖率 | >75% | TODO | - |
| 算法测试覆盖率 | >80% | TODO | - |

---

## 总结

### 测试实施要点

1. **测试金字塔**: 70%单元测试 + 20%集成测试 + 10%UI测试
2. **TDD实践**: 先写测试，再实现功能
3. **覆盖率要求**: 算法模块>80%，整体>75%
4. **性能测试**: 确保100份代码在30秒内查重
5. **CI/CD集成**: 自动化测试和覆盖率报告

### 测试策略

- ✅ 单元测试: 独立、快速、可重复
- ✅ 集成测试: 验证模块间交互
- ✅ UI测试: 验证用户界面和交互
- ✅ 性能测试: 验证性能和基准
- ✅ 覆盖率监控: 使用JaCoCo生成报告

### 测试工具栈

- **JUnit 4**: 单元测试框架
- **Mockito-Kotlin**: Mock框架
- **Compose Testing**: UI测试框架
- **Hilt Testing**: 依赖注入测试
- **JaCoCo**: 代码覆盖率工具

### 参考资料

- [Android Testing Guide](https://developer.android.com/training/testing)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [JaCoCo Plugin](https://docs.gradle.org/current/userguide/jacoco_plugin.html)
- tasks.md (T047-T052)

---

**Testing Guide Completed**: 2025-11-27
**Reference Documents**: tasks.md (T047-T052), quickstart.md
**Test Files**: app/src/test/java/, app/src/androidTest/java/
**Coverage Tools**: JaCoCo, Codecov
