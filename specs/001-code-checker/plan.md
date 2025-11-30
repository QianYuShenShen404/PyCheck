# Implementation Plan: CodeChecker Android应用 - Python代码查重助手

**Branch**: `001-code-checker` | **Date**: 2025-11-27 | **Spec**: [Link to spec.md](../spec.md)
**Input**: Feature specification from `specs/001-code-checker/spec.md`

## Summary

基于功能规格说明书，CodeChecker是一个面向北航程序设计课程的学术诚信检测Android应用。应用采用Kotlin + Jetpack Compose技术栈，实现MVVM + Clean Architecture架构模式。核心功能包括用户认证、作业管理、Python代码提交和本地查重引擎。查重算法基于Token化方法，使用Jaccard和LCS两种算法组合（0.4 * Jaccard + 0.6 * LCS）计算代码相似度。所有数据存储在本地Room数据库，核心查重功能完全离线可用，支持Android 9.0 (API 28)及以上版本。

## Technical Context

**Language/Version**: Kotlin 1.9+
**Primary Dependencies**: Jetpack Compose (最新稳定版), Hilt, Room, Navigation Compose, Kotlin Coroutines + StateFlow/SharedFlow
**Storage**: Room (SQLite ORM) - 本地数据库
**Testing**: JUnit 4 + Mockito-Kotlin, Compose UI Test
**Target Platform**: Android 9.0 (API 28) - Android 14 (API 34)
**Project Type**: Mobile Application (Android)
**Performance Goals**: 100份代码（每份200行）查重在30秒内完成；UI操作响应时间<100ms；应用冷启动<3秒
**Constraints**: 全部使用Kotlin；仅使用Room本地数据库；禁止依赖远程服务器；核心查重算法必须本地实现；算法模块测试覆盖率>80%
**Scale/Scope**: 支持200-500份作业提交；代码文件大小不超过1MB；单作业最多200份提交（默认）

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Phase 0: Initial Check ✅ PASSED

所有技术选型均符合宪法要求，无违反项。

### Phase 1: Post-Design Re-Check ✅ PASSED

**设计验证结果**:

#### 代码质量标准 (Code Quality Standards) ✅
- [GATE] 所有代码使用Kotlin语言，禁止Java - **已确认**
  - 研究结果：Jetpack Compose + Kotlin实现，符合要求
  - 架构：MVVM + Clean Architecture，清晰三层分离
  - 命名规范：已在研究文档中定义PascalCase/camelCase/SCREAMING_SNAKE_CASE
  - 文档要求：KDoc注释已纳入实现计划

- [GATE] 严格遵循MVVM + Clean Architecture，三层分离 - **已确认**
  - data/层：数据访问和存储
  - domain/层：业务逻辑和用例
  - ui/层：UI和状态管理

- [GATE] 函数长度不超过30行 - **已确认**
  - 已在算法模块设计中考虑，单一职责原则

#### 技术约束 (Technical Constraints) ✅
- [GATE] 最低支持Android 9.0 (API 28+) - **已确认**
  - 最低API级别设置为28
  - 目标API级别为34

- [GATE] UI开发使用Jetpack Compose和Material Design 3 - **已确认**
  - 使用Compose BOM 2023.10.01
  - Material Design 3组件库
  - Navigation Compose支持

- [GATE] 仅使用Room本地数据库，禁止依赖远程服务器 - **已确认**
  - 数据模型设计：5个核心表，全部本地存储
  - 远程依赖：仅AI分析功能（选做），有降级方案
  - 核心查重：100%本地实现

- [GATE] 依赖注入使用Hilt - **已确认**
  - @AndroidEntryPoint配置
  - Hilt Module设计完成
  - 依赖注入图表清晰

- [GATE] 异步处理使用Kotlin Coroutines + Flow - **已确认**
  - Dispatchers.Default用于计算密集型任务
  - StateFlow/SharedFlow用于状态管理
  - 协程并发提升查重性能

- [GATE] 核心查重算法本地实现，不依赖网络服务 - **已确认**
  - PythonTokenizer本地实现
  - Jaccard和LCS算法本地计算
  - 进度回调支持

#### 用户体验标准 (User Experience Standards) ✅
- [GATE] UI操作响应时间<100ms - **已确认**
  - Compose优化：LazyColumn重组范围控制
  - 状态管理：StateFlow优化重组
  - 性能测试计划已制定

- [GATE] 所有用户操作有明确的成功/失败反馈 - **已确认**
  - UI设计：成功/失败状态显示
  - 错误处理：全局错误处理器
  - 用户友好提示已纳入设计

- [GATE] 核心功能（查重）支持离线使用 - **已确认**
  - 查重引擎完全本地化
  - 数据存储：Room本地数据库
  - AI分析作为增值功能，可离线禁用

- [GATE] 支持系统字体缩放和内容描述 - **已确认**
  - Material Design 3自适应布局
  - ContentDescription已纳入UI组件设计

#### 安全要求 (Security Requirements) ✅
- [GATE] 用户密码使用SHA-256加密存储 - **已确认**
  - CryptoUtils加密工具类设计完成
  - 数据库存储加密哈希值

- [GATE] 教师/学生角色严格隔离 - **已确认**
  - 权限控制：Repository层实现
  - 学生只能查看自己的提交
  - 教师只能管理自己的作业

- [GATE] 文件访问权限仅限用户主动选择 - **已确认**
  - DocumentContract文件选择器
  - 用户主动授权，无需权限申请
  - 持久化URI权限管理

#### 测试标准 (Testing Standards) ✅
- [GATE] 算法模块单元测试覆盖率>80% - **已确认**
  - PythonTokenizer测试计划
  - JaccardSimilarity测试用例
  - LCSSimilarity测试用例
  - PlagiarismEngine集成测试

- [GATE] 处理边界情况（空文件、超大文件、特殊字符等） - **已确认**
  - 文件验证：类型、大小、内容检查
  - 边界测试：空文件、超大文件、特殊字符
  - 错误处理：优雅降级和用户提示

- [GATE] 查重算法修改后运行完整测试套件 - **已确认**
  - CI/CD流程（可选）
  - 回归测试计划已制定
  - 单元测试 + 集成测试 + 性能测试

### 决策优先级验证 ✅

**所有技术决策均遵循宪法规定的优先级**:
1. ✅ 正确性优先于性能 - 算法准确度为首要目标
2. ✅ 可维护性优先于功能丰富度 - 清晰的模块化架构
3. ✅ 用户体验优先于技术炫酷 - 简洁高效的UI设计
4. ✅ 必做功能优先于选做功能 - 核心功能优先实现

**总体评估**: ✅ **宪法检查完全通过**

## Project Structure

### Documentation (this feature)

```text
specs/001-code-checker/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
│   └── ai-api-spec.json # AI集成API规范（选做功能）
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
app/src/main/java/com/example/codechecker/
├── CodeCheckerApp.kt              # Application类，Hilt入口点
├── MainActivity.kt                # 单Activity架构
├── di/                            # Hilt依赖注入模块
│   ├── DatabaseModule.kt          # 数据库模块
│   ├── RepositoryModule.kt        # Repository模块
│   ├── UseCaseModule.kt           # 用例模块
│   └── NetworkModule.kt           # 网络模块（AI功能）
├── data/                          # 数据层
│   ├── local/                     # 本地存储
│   │   ├── database/
│   │   │   ├── AppDatabase.kt     # Room数据库
│   │   │   ├── dao/               # Data Access Objects
│   │   │   │   ├── UserDao.kt
│   │   │   │   ├── AssignmentDao.kt
│   │   │   │   ├── SubmissionDao.kt
│   │   │   │   ├── ReportDao.kt
│   │   │   │   └── SimilarityDao.kt
│   │   │   └── entity/            # 数据库实体
│   │   │       ├── UserEntity.kt
│   │   │       ├── AssignmentEntity.kt
│   │   │       ├── SubmissionEntity.kt
│   │   │       ├── ReportEntity.kt
│   │   │       └── SimilarityEntity.kt
│   │   ├── repository/            # Repository实现
│   │   │   ├── UserRepositoryImpl.kt
│   │   │   ├── AssignmentRepositoryImpl.kt
│   │   │   ├── SubmissionRepositoryImpl.kt
│   │   │   └── ReportRepositoryImpl.kt
│   │   └── preference/            # DataStore偏好设置
│   │       └── UserSessionManager.kt
│   ├── remote/                    # 远程API（仅AI功能）
│   │   ├── api/
│   │   │   ├── AIService.kt       # AI分析服务接口
│   │   │   └── dto/               # 数据传输对象
│   │   └── repository/
│   │       └── AIRepositoryImpl.kt
│   └── datasource/                # 数据源接口
├── domain/                        # 领域层
│   ├── model/                     # 领域模型
│   │   ├── User.kt
│   │   ├── Assignment.kt
│   │   ├── Submission.kt
│   │   ├── Report.kt
│   │   └── Similarity.kt
│   ├── repository/                # Repository接口
│   │   ├── UserRepository.kt
│   │   ├── AssignmentRepository.kt
│   │   ├── SubmissionRepository.kt
│   │   └── ReportRepository.kt
│   └── usecase/                   # 用例
│       ├── AuthUseCase.kt
│       ├── AssignmentUseCase.kt
│       ├── SubmissionUseCase.kt
│       ├── PlagiarismUseCase.kt
│       └── ReportUseCase.kt
├── algorithm/                     # 查重算法（核心模块）
│   ├── tokenizer/
│   │   ├── PythonTokenizer.kt     # Python词法分析器
│   │   └── Token.kt               # Token定义
│   ├── similarity/
│   │   ├── JaccardSimilarity.kt   # Jaccard相似度算法
│   │   └── LCSSimilarity.kt       # LCS相似度算法
│   ├── engine/
│   │   ├── PlagiarismEngine.kt    # 查重引擎
│   │   └── MatchingEngine.kt      # 匹配区域识别
│   └── model/
│       ├── TokenizedCode.kt       # Token化代码模型
│       └── SimilarityResult.kt    # 相似度结果模型
├── ui/                            # 表现层
│   ├── navigation/
│   │   ├── NavGraph.kt            # 导航图
│   │   └── Screen.kt              # 页面路由定义
│   ├── theme/
│   │   ├── Color.kt               # 颜色主题
│   │   ├── Theme.kt               # 主题配置
│   │   └── Type.kt                # 字体主题
│   ├── components/                # 可复用UI组件
│   │   ├── CodeHighlightView.kt   # 代码高亮组件
│   │   ├── SimilarityChart.kt     # 相似度图表组件
│   │   ├── ProgressIndicator.kt   # 进度指示器
│   │   └── EmptyState.kt          # 空状态组件
│   └── screens/                   # 页面
│       ├── auth/
│       │   ├── LoginScreen.kt
│       │   └── RegisterScreen.kt
│       ├── home/
│       │   ├── StudentHomeScreen.kt
│       │   └── TeacherHomeScreen.kt
│       ├── assignment/
│       │   ├── AssignmentListScreen.kt
│       │   ├── AssignmentDetailScreen.kt
│       │   └── CreateAssignmentScreen.kt
│       ├── submission/
│       │   ├── SubmitCodeScreen.kt
│       │   └── SubmissionHistoryScreen.kt
│       ├── plagiarism/
│       │   ├── ReportListScreen.kt
│       │   ├── ReportDetailScreen.kt
│       │   └── CompareCodeScreen.kt
│       └── settings/
│           └── SettingsScreen.kt
├── util/                          # 工具类
│   ├── CryptoUtils.kt             # 加密工具（SHA-256）
│   ├── FileUtils.kt               # 文件处理工具
│   ├── TimeUtils.kt               # 时间处理工具
│   ├── MD5Utils.kt                # MD5计算工具
│   └── LogUtils.kt                # 日志工具
└── test/                          # 测试
    ├── unit/
    │   ├── algorithm/             # 算法模块测试
    │   ├── domain/                # 领域层测试
    │   └── util/                  # 工具类测试
    └── integration/               # 集成测试
```

**Structure Decision**: 采用Android + 本地数据库的移动应用架构。使用MVVM + Clean Architecture实现三层分离：Presentation（ui/）、Domain（domain/）、Data（data/）。算法模块（algorithm/）独立于其他层，可单独测试。模块化设计支持Future扩展和测试隔离。

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | N/A | N/A |

**当前无宪法违反** - 所有技术选型均符合宪法要求

## Phases

### Phase 0: Outline & Research

#### Research Tasks
1. **Jetpack Compose最佳实践研究**
   - 任务：研究Compose在复杂列表、导航、状态管理方面的最佳实践
   - 关注点：LazyColumn性能优化、Navigation Compose集成、StateFlow状态管理
   - 输出：compose-best-practices.md

2. **Room数据库迁移策略研究**
   - 任务：研究Room数据库迁移、索引优化、查询性能调优
   - 关注点：数据库版本管理、复杂查询优化、批量操作性能
   - 输出：room-migration-strategy.md

3. **Python词法分析算法研究**
   - 任务：研究Python代码Token化方法、正则表达式优化、AST解析可能性
   - 关注点：注释移除、缩进处理、标识符标准化、多版本Python兼容性
   - 输出：python-tokenization-research.md

4. **LCS算法性能优化研究**
   - 任务：研究动态规划算法优化、内存优化、并行计算可能性
   - 关注点：时间复杂度O(n²)优化、空间复杂度优化、Kotlin协程集成
   - 输出：lcs-algorithm-optimization.md

5. **Android文件访问权限研究**
   - 任务：研究Android 10+分区存储、文件选择器集成、代码文件读取
   - 关注点：Scoped Storage、DocumentContract使用、大文件处理
   - 输出：android-file-access-guide.md

6. **AI API集成模式研究**
   - 任务：研究多AI提供商集成、API封装、错误处理和重试策略
   - 关注点：Kotlinx.serialization、OkHttp配置、超时和重试
   - 输出：ai-integration-patterns.md

### Phase 1: Design & Contracts

#### Data Model Design
基于功能规格说明书中的Key Entities和数据需求，设计完整的数据模型：

- **用户实体（User）**: 用户名（全局唯一）、密码哈希、显示名称、角色、创建时间
- **作业实体（Assignment）**: 标题、描述、截止日期、创建者、Python版本配置、提交上限、状态
- **提交实体（Submission）**: 学生ID、作业ID、文件名、代码内容、MD5、提交时间、状态
- **报告实体（Report）**: 作业ID、执行时间、状态、生成者、提交数量、对数
- **相似度记录（SimilarityRecord）**: 两个提交ID、综合相似度、Jaccard得分、LCS得分、匹配区域
- **AI分析结果（AIAnalysis）**: 相似度记录ID、分析类型、相似原因、风险等级、分析时间

数据库表结构已在plan_demo.md中定义，包含users、assignments、submissions、plagiarism_reports、similarity_pairs五个核心表。

#### API Contracts
对于AI智能分析功能（选做），定义RESTful API接口：

- **POST /ai/analyze**: 调用AI分析两段代码的相似性
- **请求体**: { code1: String, code2: String, similarity: Float, provider: String }
- **响应体**: { reason: String, isCommonCode: Boolean, plagiarismRisk: String, analysis: String }

#### Quickstart Guide
包含以下内容：
1. 开发环境搭建（Android Studio、Kotlin、JDK）
2. 项目克隆和依赖安装
3. 运行和调试步骤
4. 测试执行方法
5. 常见问题解决

## Implementation Timeline (21 Days)

### 第一阶段 (Day 1-4): 项目初始化与基础框架
- Day 1: 项目创建、Kotlin版本配置、依赖管理
- Day 2: Hilt配置、Room数据库搭建、实体类生成
- Day 3: MVVM架构搭建、导航配置、主题系统
- Day 4: 登录/注册页面UI和逻辑、Session管理

### 第二阶段 (Day 5-8): 用户认证与作业管理
- Day 5: 用户注册、登录验证、密码加密存储
- Day 6: 作业创建页面（教师）、文件上传UI
- Day 7: 作业列表、作业详情页面
- Day 8: 作业提交功能、文件选择器集成

### 第三阶段 (Day 9-12): 查重引擎 ⭐核心
- Day 9: PythonTokenizer实现、Token模型定义
- Day 10: JaccardSimilarity算法实现
- Day 11: LCSSimilarity算法实现
- Day 12: PlagiarismEngine集成、两两比对逻辑、进度回调

### 第四阶段 (Day 13-16): 报告展示与可视化
- Day 13: 报告列表页面、历史记录功能
- Day 14: 报告详情页面、相似度分布图表
- Day 15: 代码对比页面、高亮显示、同步滚动
- Day 16: 权限控制（学生只能查看自己的报告）

### 第五阶段 (Day 17-19): 选做功能与优化
- Day 17: AI API集成、DeepSeek/通义千问/ModelScope支持
- Day 18: 批量查重、历史趋势分析
- Day 19: 性能优化、日志完善、数据清理机制

### 第六阶段 (Day 20-21): 测试与收尾
- Day 20: 集成测试、单元测试（算法模块覆盖率>80%）
- Day 21: Bug修复、文档整理、应用打包

## Success Criteria

- ✅ 宪法检查完全通过
- ✅ 技术架构符合MVVM + Clean Architecture
- ✅ 所有依赖符合技术约束
- ✅ 项目结构清晰，模块化设计
- ✅ 研究任务规划完整，覆盖所有关键技术点
- ✅ 数据模型设计完整，支持所有功能需求
- ✅ API契约清晰（针对选做AI功能）
- ✅ 实现计划可行，21天内可完成

## Next Steps

执行Phase 0研究任务，生成research.md：
1. 运行research agents解决NEEDS CLARIFICATION
2. 研究Jetpack Compose、Room、Python词法分析等关键技术
3. 整理研究结果到research.md

然后进入Phase 1：
1. 生成data-model.md
2. 创建contracts/ai-api-spec.json
3. 生成quickstart.md
4. 更新agent context
