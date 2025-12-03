# 代码对比页面空白问题分析与解决方案

## 问题描述
当点击查重报告中的某份对比结果的"查看详情"时，显示空页面，无法展示两份代码的对比。

## 根本原因分析

### 1. CompareCodeViewModel 的数据加载bug

**文件：** `domain/usecase/CompareCodeViewModel.kt`（第43-45行）

```kotlin
fun loadSimilarity(similarityId: Long) {
    viewModelScope.launch {
        try {
            val similarities = plagiarismUseCase.getSimilaritiesByReport(
                reportId = 0L  // ❌ 严重bug：写死为0L！
            )
            val similarity = similarities.find { it.id == similarityId }
            // ...
```

**问题：**
- `getSimilaritiesByReport()` 需要正确的 `reportId` 参数
- 当前代码写死为 `0L`，导致永远查询不到数据
- 结果是 `similarity` 总是 null，页面显示"未找到相似度数据"

### 2. 导航结构分析

**文件：** `ui/navigation/Screen.kt`（第71-73行）

```kotlin
fun createCodeComparisonRoute(similarityId: Long): String {
    return "${Screen.CODE_COMPARISON}?${NavArguments.SIMILARITY_ID}=$similarityId"
}
```

**现状：**
- 只传递了 `similarityId`
- 没有传递 `reportId`
- 这导致 ViewModel 无法确定该similarity属于哪个report

### 3. Repository 接口分析

**文件：** `domain/repository/ReportRepository.kt`

```kotlin
// 存在的方法
suspend fun getSimilaritiesByReport(reportId: Long): List<Similarity>

// 缺失的方法
// suspend fun getSimilarityById(similarityId: Long): Similarity?
```

**问题：**
- 没有直接查询单个Similarity的方法
- 必须通过reportId来查询

---

## 解决方案对比

### 方案A：修改导航传递reportId（需改动多个地方）

**优点：**
- 符合数据关系设计

**缺点：**
- 需要修改：
  1. `Screen.kt` 的 `createCodeComparisonRoute()` 函数
  2. `ReportDetailScreen.kt` 的调用处（第138行）
  3. `CompareCodeViewModel.kt` 的 `loadSimilarity()` 方法

**修改文件数：3个**

```kotlin
// Screen.kt
fun createCodeComparisonRoute(similarityId: Long, reportId: Long): String {
    return "${Screen.CODE_COMPARISON}?${NavArguments.SIMILARITY_ID}=$similarityId&${NavArguments.REPORT_ID}=$reportId"
}

// ReportDetailScreen.kt
onNavigateToCodeComparison(similarity.id, report.id)

// CompareCodeViewModel.kt
fun loadSimilarity(similarityId: Long, reportId: Long) {
    val similarities = plagiarismUseCase.getSimilaritiesByReport(reportId)
    val similarity = similarities.find { it.id == similarityId }
}
```

### 方案B：在Repository中添加getSimilarityById方法（推荐）

**优点：**
- 只需修改2个文件（Repository接口 + 实现 + ViewModel）
- 不需要改导航逻辑
- 更加模块化和独立

**缺点：**
- 多一次数据库查询

**修改文件数：2个**

```kotlin
// ReportRepository.kt（接口）
suspend fun getSimilarityById(similarityId: Long): Similarity?

// ReportRepositoryImpl.kt（实现）
override suspend fun getSimilarityById(similarityId: Long): Similarity? {
    return reportDao.getSimilarityById(similarityId)
}

// CompareCodeViewModel.kt
fun loadSimilarity(similarityId: Long) {
    val similarity = plagiarismUseCase.getSimilarityById(similarityId)
    // ...
}
```

---

## 推荐方案：方案B（Repository中添加getSimilarityById）

### 原因：
1. **改动最小化** - 只改2个文件
2. **不破坏导航结构** - 维持当前导航逻辑
3. **更符合Repository模式** - 数据层提供所需查询
4. **更好的模块化** - ViewModel不需要知道reportId

### 详细步骤

#### Step 1: 修改 ReportRepository 接口

**文件：** `domain/repository/ReportRepository.kt`

在现有方法后添加：

```kotlin
/**
 * Get similarity by ID
 */
suspend fun getSimilarityById(similarityId: Long): Similarity?
```

#### Step 2: 修改 ReportRepositoryImpl 实现

**文件：** `data/repository/ReportRepositoryImpl.kt`

实现新方法：

```kotlin
override suspend fun getSimilarityById(similarityId: Long): Similarity? {
    return reportDao.getSimilarityById(similarityId)
}
```

#### Step 3: 修改 SimilarityDao 中添加查询方法

**文件：** `data/local/dao/SimilarityDao.kt`

添加Query方法：

```kotlin
@Query("SELECT * FROM similarity_pairs WHERE id = :similarityId")
suspend fun getSimilarityById(similarityId: Long): SimilarityEntity?
```

#### Step 4: 修改 CompareCodeViewModel

**文件：** `ui/screens/plagiarism/CompareCodeViewModel.kt`

替换 `loadSimilarity()` 方法：

```kotlin
fun loadSimilarity(similarityId: Long) {
    viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        try {
            // ✅ 修复：直接查询单个similarity，无需reportId
            val similarity = plagiarismUseCase.getSimilarityById(similarityId)

            if (similarity != null) {
                val submission1 = submissionRepository.getSubmissionById(similarity.submission1Id)
                val submission2 = submissionRepository.getSubmissionById(similarity.submission2Id)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    similarity = similarity,
                    code1 = submission1?.codeContent ?: "",
                    code2 = submission2?.codeContent ?: ""
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "未找到相似度数据"
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                error = e.message ?: "加载失败"
            )
        }
    }
}
```

#### Step 5: 在 PlagiarismUseCase 中添加方法

**文件：** `domain/usecase/PlagiarismUseCase.kt`

添加方法：

```kotlin
/**
 * Get similarity by ID
 */
suspend fun getSimilarityById(similarityId: Long): Similarity? {
    return withContext(Dispatchers.IO) {
        reportRepository.getSimilarityById(similarityId)
    }
}
```

---

## 修改文件清单

| 文件 | 修改内容 | 优先级 |
|------|--------|-------|
| `data/local/dao/SimilarityDao.kt` | 添加 `getSimilarityById()` 查询方法 | **高** |
| `domain/repository/ReportRepository.kt` | 添加 `getSimilarityById()` 接口方法 | **高** |
| `data/repository/ReportRepositoryImpl.kt` | 实现 `getSimilarityById()` | **高** |
| `domain/usecase/PlagiarismUseCase.kt` | 添加 `getSimilarityById()` | **高** |
| `ui/screens/plagiarism/CompareCodeViewModel.kt` | 修复 `loadSimilarity()` 方法 | **高** |

---

## 验证清单

- [ ] 编译通过，无编译错误
- [ ] 点击"查看详情"能正确导航到代码对比页面
- [ ] 两份代码都能正确加载和显示
- [ ] 相似度分数正确显示
- [ ] 可以滚动查看完整代码
- [ ] 返回按钮能正常回到报告页面

---

## 预期效果

修改前：点击"查看详情" → 空页面 → "未找到相似度数据"

修改后：点击"查看详情" → 展示两份代码 + 相似度分数 + 代码对比分析