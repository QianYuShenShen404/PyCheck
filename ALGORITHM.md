# Python 代码查重算法说明

## 概述
- 本系统的查重引擎围绕“词法分析 + 相似度度量 + 报告持久化”三步展开。
- 主要组件：
  - 词法分析器：`algorithm/tokenizer/PythonTokenizer.kt`，将 Python 源码转为 Token 序列。
  - 相似度计算器：`algorithm/similarity/SimilarityCalculator.kt`，计算 Jaccard 与 LCS，两者加权形成综合得分。
  - 引擎：`algorithm/engine/PlagiarismEngine.kt`，实现两两比对、快速模式筛选与高亮数据生成。
  - 用例层：`domain/usecase/PlagiarismUseCase.kt`，负责按不同策略生成报告并落库。

## 词法分析（Tokenizer）
- 文件：`app/src/main/java/com/example/codechecker/algorithm/tokenizer/PythonTokenizer.kt`
- 关键逻辑：
  - 移除注释与多余空白，统一字符串与数字的解析，输出 Token 列表。
  - 区分 `KEYWORD/IDENTIFIER/OPERATOR/DELIMITER/LITERAL/COMMENT` 等类型，当前相似度只使用 `value` 字段。
  - 参考：`PythonTokenizer.tokenize` 实现于 PythonTokenizer.kt:40-92 与辅助读取函数 237-258。
- 设计意图：
  - 通过词法层抽象规避变量名差异、空格与注释噪声，使得相似度更关注结构与语义片段。

## 相似度度量（SimilarityCalculator）
- 文件：`app/src/main/java/com/example/codechecker/algorithm/similarity/SimilarityCalculator.kt`
- 指标与公式：
  - Jaccard 相似度：`|A ∩ B| / |A ∪ B| * 100`，其中集合元素为 Token 的 `value`；实现见 SimilarityCalculator.kt:66-78。
  - LCS 相似度：`LCS_len / max(len1, len2) * 100`，对 Token 值序列做动态规划；实现见 SimilarityCalculator.kt:80-92 与 94-126。
  - 综合得分：`0.4 * Jaccard + 0.6 * LCS`；实现见 SimilarityCalculator.kt:24-40。
- 说明：
  - LCS 更敏感于顺序与结构，Jaccard更贴近集合重合；加权策略强化结构相似的影响。
  - 管理端参数（快速模式开关、阈值）通过 `AlgorithmSettingsService` 提供；文件：AlgorithmSettingsService.kt:41-76。

## 查重流程（PlagiarismEngine）
- 文件：`app/src/main/java/com/example/codechecker/algorithm/engine/PlagiarismEngine.kt`
- 标准流程 `detectPlagiarism`：
  - 输入：若干 `Submission`（含 `codeContent`、`codeHash` 等）。
  - 步骤：
    1. 两两比对，调用 `SimilarityCalculator.calculateSimilarity` 获取 `jaccard/lcs/combined`。
    2. 生成高亮数据（`HighlightData`）：基于公共 Token 行的简单行级“精确匹配”标注；见 PlagiarismEngine.kt:152-199。
    3. 汇总为 `Similarity` 记录（含分数与高亮），返回上层使用；见 PlagiarismEngine.kt:43-81。
- 快速模式 `detectPlagiarismFast`：
  - 策略：按 `codeHash` 前缀分组，仅在同组内做两两比对，且低分对（<10）提前跳过；见 PlagiarismEngine.kt:83-150。
  - 适用：大规模数据下的预筛选，提高整体吞吐。
- 阈值过滤：`findHighSimilarityPairs` 可按管理员配置阈值筛选高相似对；见 PlagiarismEngine.kt:201-226。

## 报告生成与持久化（UseCase）
- 文件：`app/src/main/java/com/example/codechecker/domain/usecase/PlagiarismUseCase.kt`
- 入口函数：
  - `generateReport`：对作业的“所有提交”两两比对并落库，适用于全量历史；见 PlagiarismUseCase.kt:120-213。
  - `generateReportFast`：使用快速模式，仅在 Hash 接近的提交间比对；见 PlagiarismUseCase.kt:234-294。
  - `generateReportLatestOnly`：每位学生“最后一次提交”的两两比对，面向截止后检查；见 PlagiarismUseCase.kt:32-69。
  - `generateStudentLatestTargetReport`：某学生最新提交与其他最新提交的定向比对（用于学生提交后即时报告）；见 PlagiarismUseCase.kt:71-119。
- 落库流程：
  1. 创建 `Report`（`PENDING`）并获取 `reportId`。
  2. 计算相似度，写入 `Similarity`（带 `reportId` 关联）。
  3. 更新 `Report` 状态为 `COMPLETED`，记录完成时间。

## 高亮数据生成（简化实现）
- 原理：
  - 对两段代码进行 Token 化，找出公共 Token。
  - 按行扫描，若两段中出现相同 Token 且行文本完全一致（`trim` 后相等），标记为行级 `EXACT_MATCH` 区间。
- 参考实现：PlagiarismEngine.kt:152-199。
- 说明：
  - 这是轻量级举例实现，能直观突出重复行或片段；更细粒度的 AST/语法树对齐可作为后续增强。

## 复杂度与性能
- 标准两两比对：`O(N^2 * C)`，其中 `C` 为一次相似度计算复杂度（LCS 为 `O(m*n)`）。
- 快速模式：通过哈希前缀分组将 `N^2` 降到分组内的规模，低相似提前退出，显著降低平均开销。
- 内存与时间权衡：LCS 的 DP 表为二维数组，极长代码可能带来较大内存；可采用滚动数组或分段对比优化。

## 参数配置（管理员）
- 相似度阈值：用于“高相似结果”筛选与风险提示；`AlgorithmSettingsService.getSimilarityThreshold`。
- 快速模式：开启后用 `detectPlagiarismFast` 的策略与早停；`AlgorithmSettingsService.isFastCompareModeEnabled`。

## 边界与准确性
- Token 粒度：当前以词法 Token 值为基本单位，能过滤注释与空白，但无法识别语义等价的结构改写（如循环交换、提取函数等）。
- 高亮局限：行级精确匹配对排版变动敏感，可能漏报结构性相似；可引入 AST 比对与最短编辑距的组合提升鲁棒性。
- 阈值选择：分数以百分比呈现，实际门槛需结合课程布置与经验数据调优。

## 与业务流程的关系
- 学生提交成功后：在 `SubmitCodeViewModel` 里自动生成“个人最新对比”报告并落库（`generateStudentLatestTargetReport`）；参考 SubmitCodeViewModel.kt:84-101。
- 作业详情页：支持教师生成“仅最后一次提交”或“包含所有历史”的报告；参考 AssignmentDetailViewModel.kt:84-146。
- 快速批量：教师可触发 `generateReportFast` 以较短时间产出全局报告。

## 后续增强建议
- 引入 AST 归一化（如移除重命名与空语句、标准化结构），提升对语义相似的识别。
- 增加编辑距离（Levenshtein）或指纹（w-shingling/SimHash）以抵抗微调与搬运改写。
- 更丰富的高亮：从行级扩展到 Token 区间或语法节点对齐，并支持交互式展开。
- 队列与缓存：对重复提交和相同代码散列做缓存，减少重复计算。

---

## 主要代码引用
- 词法分析：`app/src/main/java/com/example/codechecker/algorithm/tokenizer/PythonTokenizer.kt:40-92, 237-258`
- 相似度计算：`app/src/main/java/com/example/codechecker/algorithm/similarity/SimilarityCalculator.kt:24-40, 66-78, 80-92, 94-126`
- 引擎流程：`app/src/main/java/com/example/codechecker/algorithm/engine/PlagiarismEngine.kt:43-81, 83-150, 152-199, 201-226`
- 报告生成：`app/src/main/java/com/example/codechecker/domain/usecase/PlagiarismUseCase.kt:32-69, 120-213, 234-294`
- 提交触发：`app/src/main/java/com/example/codechecker/ui/screens/submission/SubmitCodeViewModel.kt:84-101`
