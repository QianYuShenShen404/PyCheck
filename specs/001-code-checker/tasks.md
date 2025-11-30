# Tasks: CodeChecker Android应用 - Python代码查重助手

**Input**: Design documents from `specs/001-code-checker/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: 算法模块测试覆盖率要求 >80%，包含单元测试和集成测试

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Android app**: `app/src/main/java/com/example/codechecker/`
- Paths shown below assume Android project structure

## Phase Dependencies

- **Phase 0**: Research (已完成)
- **Phase 1**: Design & Contracts (已完成)
- **Phase 2**: Task Breakdown (当前阶段)
- **Phase 3**: Implementation (未来阶段)
- **Phase 4**: Testing (未来阶段)

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: 项目初始化和基础架构搭建

- [x] T001 Create project structure per implementation plan ✅
  - 创建完整的Android项目目录结构
  - 文件路径: `app/src/main/java/com/example/codechecker/`
  - 包含: di/, data/, domain/, algorithm/, ui/, util/ 目录

- [x] T002 Initialize Android project with Kotlin and dependencies ✅
  - 配置build.gradle.kts文件
  - 添加Jetpack Compose, Hilt, Room, Kotlin Coroutines等依赖
  - 配置Kotlin编译器版本1.9+
  - 文件路径: `app/build.gradle.kts`, `build.gradle.kts`

- [x] T003 [P] Configure linting and formatting tools ✅
  - 配置Ktlint代码格式检查
  - 配置Detekt静态代码分析
  - 设置Git hooks自动格式化
  - 文件路径: `.editorconfig`, `detekt-config.yml`

- [x] T004 [P] Setup Hilt dependency injection framework ✅
  - 创建Application类配置Hilt
  - 创建DatabaseModule, RepositoryModule, UseCaseModule
  - 配置@AndroidEntryPoint注解
  - 文件路径: `app/src/main/java/com/example/codechecker/CodeCheckerApp.kt`

- [x] T005 [P] Setup Room database with entities ✅
  - 创建UserEntity, AssignmentEntity, SubmissionEntity
  - 创建ReportEntity, SimilarityEntity
  - 配置AppDatabase类和DAO接口
  - 文件路径: `app/src/main/java/com/example/codechecker/data/local/entity/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T006 [P] Implement data models and mappers ✅
  - 创建domain/model目录下的User, Assignment, Submission, Report, Similarity模型
  - 创建Entity到Domain的映射器
  - 实现数据转换逻辑
  - 文件路径: `app/src/main/java/com/example/codechecker/domain/model/`

- [x] T007 [P] Implement repository pattern ✅
  - 创建Repository接口: UserRepository, AssignmentRepository, SubmissionRepository, ReportRepository
  - 实现RepositoryImpl类
  - 配置依赖注入
  - 文件路径: `app/src/main/java/com/example/codechecker/data/repository/`

- [x] T008 [P] Setup DataStore for user session management ✅
  - 创建UserSessionManager
  - 实现登录状态持久化
  - 配置自动登录功能
  - 文件路径: `app/src/main/java/com/example/codechecker/data/preference/`

- [x] T009 Create utility classes ✅
  - CryptoUtils (SHA-256加密)
  - FileUtils (文件处理)
  - MD5Utils (MD5计算)
  - TimeUtils (时间处理)
  - 文件路径: `app/src/main/java/com/example/codechecker/util/`

- [x] T010 Setup navigation structure ✅
  - 创建NavGraph和Screen路由定义
  - 配置Navigation Compose
  - 设置深色/浅色主题切换
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/navigation/`

- [x] T011 Create base UI components and theme ✅
  - 创建主题系统 (Color.kt, Theme.kt, Type.kt)
  - 创建可复用组件 (LoadingIndicator, EmptyState, ErrorMessage)
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/theme/`, `app/src/main/java/com/example/codechecker/ui/components/`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - 学生注册登录并查看作业 (Priority: P1) 🎯 MVP

**Goal**: 实现用户认证系统和基础导航，为学生用户提供入门功能

**Independent Test**: 学生可以完成注册 → 登录 → 查看作业列表 → 退出登录的完整流程

### Implementation for User Story 1

- [ ] T012 [P] [US1] Implement user registration
  - 创建RegisterScreen UI (Jetpack Compose)
  - 实现表单验证 (用户名、密码、显示名称、角色)
  - 实现UserRepository.registerUser()
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/auth/RegisterScreen.kt`

- [ ] T013 [P] [US1] Implement user login
  - 创建LoginScreen UI
  - 实现登录验证逻辑
  - 实现密码SHA-256加密验证
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/auth/LoginScreen.kt`

- [ ] T014 [P] [US1] Create authentication use case
  - 实现AuthUseCase
  - 处理注册和登录业务逻辑
  - 管理用户会话
  - 文件路径: `app/src/main/java/com/example/codechecker/domain/usecase/AuthUseCase.kt`

- [ ] T015 [P] [US1] Implement session persistence
  - 配置DataStore存储登录状态
  - 实现自动登录功能
  - 实现登出功能
  - 文件路径: `app/src/main/java/com/example/codechecker/data/preference/UserSessionManager.kt`

- [ ] T016 [US1] Create student home screen
  - 登录成功后显示作业列表
  - 支持学生角色和教师角色不同视图
  - 显示用户信息和登出按钮
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/home/StudentHomeScreen.kt`

- [ ] T017 [US1] Navigation integration
  - 集成登录/注册流程到NavGraph
  - 配置路由导航
  - 处理导航状态
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/navigation/NavGraph.kt`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - 学生提交Python代码 (Priority: P1) 🎯 MVP

**Goal**: 实现代码提交功能，这是学生的主要业务价值

**Independent Test**: 学生登录 → 选择作业 → 选择多个.py文件 → 确认提交 → 查看提交历史

### Implementation for User Story 2

- [ ] T018 [P] [US2] Implement file picker
  - 集成DocumentContract文件选择器
  - 实现文件类型过滤 (.py文件)
  - 处理权限持久化
  - 文件路径: `app/src/main/java/com/example/codechecker/util/FileUtils.kt`

- [ ] T019 [P] [US2] Create assignment detail screen
  - 显示作业信息 (标题、描述、截止日期)
  - 显示提交状态和截止时间
  - 支持提交文件按钮
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/assignment/AssignmentDetailScreen.kt`

- [ ] T020 [P] [US2] Implement code submission
  - 创建SubmitCodeScreen UI
  - 支持多文件选择
  - 实现文件验证 (类型、大小、内容)
  - 计算MD5哈希值
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/submission/SubmitCodeScreen.kt`

- [ ] T021 [P] [US2] Implement submission use case
  - 实现SubmissionUseCase
  - 处理文件上传逻辑
  - 存储代码内容到数据库
  - 文件路径: `app/src/main/java/com/example/codechecker/domain/usecase/SubmissionUseCase.kt`

- [ ] T022 [US2] Create submission history screen
  - 显示学生的所有提交记录
  - 按作业分组显示
  - 显示文件名、提交时间、状态
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/submission/SubmissionHistoryScreen.kt`

- [ ] T023 [US2] Integration with User Story 1
  - 从学生首页导航到作业列表
  - 从作业详情到提交页面
  - 确保登录状态检查
  - 文件路径: 多个UI文件

**Checkpoint**: At this point, User Story 2 should be fully functional and testable independently

---

## Phase 5: User Story 3 - 教师创建和管理作业 (Priority: P2)

**Goal**: 实现教师核心管理功能，创建作业和管理学生提交

**Independent Test**: 教师登录 → 创建作业 → 查看作业列表 → 查看作业提交

### Implementation for User Story 3

- [ ] T024 [P] [US3] Create teacher home screen
  - 显示教师专用界面
  - 创建作业按钮
  - 我的作业列表
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/home/TeacherHomeScreen.kt`

- [ ] T025 [P] [US3] Implement assignment creation
  - 创建CreateAssignmentScreen UI
  - 表单验证 (标题必填，截止日期可选)
  - 配置提交上限 (小型200/大型500/无限制)
  - 配置Python版本 (2.x/3.x/兼容)
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/assignment/CreateAssignmentScreen.kt`

- [ ] T026 [P] [US3] Implement assignment use case
  - 实现AssignmentUseCase
  - 处理作业CRUD操作
  - 权限控制 (教师只能管理自己的作业)
  - 文件路径: `app/src/main/java/com/example/codechecker/domain/usecase/AssignmentUseCase.kt`

- [ ] T027 [P] [US3] Create assignment list screen
  - 显示教师创建的所有作业
  - 按状态筛选 (草稿/进行中/已截止)
  - 快速编辑和删除操作
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/assignment/AssignmentListScreen.kt`

- [ ] T028 [US3] View student submissions
  - 查看某作业下的所有学生提交
  - 显示提交者、文件名、提交时间
  - 过滤和搜索功能
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/assignment/SubmissionListScreen.kt`

**Checkpoint**: At this point, User Story 3 should be fully functional and testable independently

---

## Phase 6: User Story 4 - 教师执行查重并查看报告 (Priority: P2) ⭐核心

**Goal**: 实现应用核心价值功能 - 代码相似度检测

**Independent Test**: 教师选择作业 → 执行查重 → 查看查重进度 → 查看报告详情

### Implementation for User Story 4

- [ ] T029 [P] [US4] Implement Python tokenizer
  - 创建PythonTokenizer类
  - 识别关键字、标识符、运算符、数字、字符串
  - 移除注释和空白行
  - 支持标识符标准化 (可选)
  - 文件路径: `app/src/main/java/com/example/codechecker/algorithm/tokenizer/PythonTokenizer.kt`

- [ ] T030 [P] [US4] Implement Jaccard similarity
  - 创建JaccardSimilarity类
  - 计算Token集合交并比
  - 返回百分比得分 (0-100)
  - 文件路径: `app/src/main/java/com/example/codechecker/algorithm/similarity/JaccardSimilarity.kt`

- [ ] T031 [P] [US4] Implement LCS similarity
  - 创建LCSSimilarity类
  - 使用动态规划算法
  - 内存优化 (Hirschberg算法)
  - 返回百分比得分 (0-100)
  - 文件路径: `app/src/main/java/com/example/codechecker/algorithm/similarity/LCSSimilarity.kt`

- [ ] T032 [P] [US4] Implement plagiarism engine
  - 创建PlagiarismEngine类
  - 实现两两比对逻辑
  - 计算综合得分 (0.4*J + 0.6*L)
  - 识别匹配区域 (高亮数据)
  - 文件路径: `app/src/main/java/com/example/codechecker/algorithm/engine/PlagiarismEngine.kt`

- [ ] T033 [US4] Create plagiarism report use case
  - 实现PlagiarismUseCase
  - 协程并发优化性能
  - 进度回调支持 (当前/总数)
  - 存储报告到数据库
  - 文件路径: `app/src/main/java/com/example/codechecker/domain/usecase/PlagiarismUseCase.kt`

- [ ] T034 [P] [US4] Create report list screen
  - 显示历史查重报告列表
  - 按作业筛选
  - 显示报告状态 (完成/进行中)
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/plagiarism/ReportListScreen.kt`

- [ ] T035 [P] [US4] Create report detail screen
  - 显示报告概要信息
  - 相似度分布图表 (柱状图: 0-20%, 20-40%, 40-60%, 60-80%, 80-100%)
  - 高相似度警告列表 (>60%)
  - 完整比对结果列表 (按相似度排序)
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/plagiarism/ReportDetailScreen.kt`

- [ ] T036 [P] [US4] Implement concurrency management
  - 队列管理多个查重请求
  - 避免资源冲突
  - 显示等待状态
  - 文件路径: `app/src/main/java/com/example/codechecker/algorithm/engine/ConcurrencyManager.kt`

**Checkpoint**: At this point, User Story 4 should be fully functional and testable independently

---

## Phase 7: User Story 5 - 查看代码高亮对比 (Priority: P3)

**Goal**: 提供查重结果的直观展示，增强用户体验

**Independent Test**: 从报告选择代码对 → 查看对比界面 → 识别高亮标记的相似区域

### Implementation for User Story 5

- [ ] T037 [P] [US5] Implement code highlight component
  - 创建CodeHighlightView组件
  - 支持Python语法着色
  - 显示行号
  - 支持复制代码功能
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/components/CodeHighlightView.kt`

- [ ] T038 [P] [US5] Create code comparison screen
  - 左右分栏显示两份代码
  - 同步滚动功能
  - 高亮标记相似区域
  - 显示相似度百分比
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/plagiarism/CompareCodeScreen.kt`

- [ ] T039 [P] [US5] Implement highlighting logic
  - 解析高亮数据JSON
  - 标记匹配区域
  - 支持多段匹配高亮
  - 颜色编码区分不同匹配
  - 文件路径: `app/src/main/java/com/example/codechecker/algorithm/engine/MatchingEngine.kt`

- [ ] T040 [US5] Navigation integration
  - 从报告详情跳转到代码对比
  - 上下翻页查看不同代码对
  - 返回报告列表
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/navigation/NavGraph.kt`

**Checkpoint**: At this point, User Story 5 should be fully functional and testable

---

## Phase 8: User Story 6 - AI智能分析（选做功能）(Priority: P3)

**Goal**: 提供增值服务，使用AI分析代码相似性原因

**Independent Test**: 可选功能，不影响主要流程 - 选择高相似度代码对 → 选择AI提供商 → 调用AI分析 → 查看分析报告

### Implementation for User Story 6 (Optional)

- [ ] T041 [P] [US6] Setup network module for AI API
  - 配置OkHttp客户端
  - 配置Kotlinx.serialization
  - 超时和重试策略
  - 文件路径: `app/src/main/java/com/example/codechecker/di/NetworkModule.kt`

- [ ] T042 [P] [US6] Implement AI service interfaces
  - 创建AIService接口
  - 定义AI分析请求/响应模型
  - 支持DeepSeek、通义千问、ModelScope
  - 文件路径: `app/src/main/java/com/example/codechecker/data/remote/api/AIService.kt`

- [ ] T043 [P] [US6] Implement AI repository
  - 创建AIRepository接口和实现
  - 多提供商适配器模式
  - 错误处理和降级策略
  - 文件路径: `app/src/main/java/com/example/codechecker/data/repository/AIRepositoryImpl.kt`

- [ ] T044 [P] [US6] Create AI analysis prompt template
  - 设计标准化的Prompt模板
  - 包含代码示例和上下文信息
  - 要求JSON格式返回
  - 文件路径: `app/src/main/java/com/example/codechecker/data/remote/dto/AIPromptTemplate.kt`

- [ ] T045 [US6] Add AI analysis to comparison screen
  - 添加AI分析按钮
  - 显示分析进度
  - 展示分析结果 (相似原因、风险等级)
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/screens/plagiarism/CompareCodeScreen.kt`

- [ ] T046 [US6] Error handling and fallback
  - 网络错误处理
  - API调用失败降级
  - 显示基础分析结果
  - 文件路径: `app/src/main/java/com/example/codechecker/data/repository/AIRepositoryImpl.kt`

**Checkpoint**: AI智能分析功能完成 (可选)

---

## Phase 9: Testing & Quality Assurance

**Purpose**: 确保代码质量和功能完整性

- [ ] T047 [P] Write algorithm unit tests
  - PythonTokenizer测试 (空文件、注释、特殊字符)
  - JaccardSimilarity测试 (边界情况、性能)
  - LCSSimilarity测试 (大文件、内存优化)
  - PlagiarismEngine测试 (综合得分、匹配区域)
  - 目标覆盖率: >80%
  - 文件路径: `app/src/test/java/com/example/codechecker/algorithm/`

- [ ] T048 [P] Write domain layer tests
  - AuthUseCase测试
  - AssignmentUseCase测试
  - SubmissionUseCase测试
  - PlagiarismUseCase测试
  - 文件路径: `app/src/test/java/com/example/codechecker/domain/usecase/`

- [ ] T049 [P] Write data layer tests
  - DAO测试 (查询、插入、更新)
  - Repository测试 (数据转换、错误处理)
  - 数据库迁移测试
  - 文件路径: `app/src/test/java/com/example/codechecker/data/`

- [ ] T050 [P] Write UI tests (Compose)
  - 登录/注册流程测试
  - 作业列表测试
  - 代码提交测试
  - 报告查看测试
  - 文件路径: `app/src/androidTest/java/com/example/codechecker/ui/`

- [ ] T051 [P] Integration testing
  - 端到端用户流程测试
  - 权限控制测试
  - 并发查重测试
  - 数据一致性测试
  - 文件路径: `app/src/androidTest/java/com/example/codechecker/integration/`

- [ ] T052 [P] Performance testing
  - 查重性能测试 (100份代码<30秒)
  - UI响应时间测试 (<100ms)
  - 内存使用测试
  - 冷启动时间测试 (<3秒)
  - 文件路径: `app/src/test/java/com/example/codechecker/performance/`

---

## Phase 10: Performance Optimization & Polish

**Purpose**: 性能优化、用户体验改进和跨领域功能增强

- [ ] T053 [P] Implement empty states
  - 作业列表空状态
  - 提交历史空状态
  - 报告列表空状态
  - 用户引导信息

- [ ] T054 [P] Add error handling
  - 全局错误处理器
  - 用户友好的错误提示
  - 网络错误处理
  - 数据库错误处理

- [ ] T055 [P] Implement logging system
  - 配置日志级别 (Debug/Info/Error)
  - 记录用户操作
  - 记录查重结果
  - 记录错误信息

- [ ] T056 [P] Data cleanup mechanism
  - 实现数据保留期限检查
  - 自动清理过期数据
  - 用户主动删除功能
  - 清理进度显示

- [ ] T057 [P] Accessibility improvements
  - 添加ContentDescription
  - 支持TalkBack
  - 支持字体缩放 (100%-200%)
  - 键盘导航支持

- [ ] T058 [P] Documentation updates
  - API文档 (如适用)
  - 数据库迁移日志
  - README.md更新
  - 代码注释 (KDoc)

- [ ] T059 [P] Code obfuscation and release build
  - 配置ProGuard规则
  - 生成签名密钥
  - 构建发布版本
  - APK性能测试

#### Performance Optimization Tasks

- [ ] T064 [P] Implement UI performance optimization
  - 优化LazyColumn重组范围（使用key、itemKeys参数）
  - 实现状态缓存（remember、rememberSaveable）
  - 优化图片和列表加载（预取、Paging3）
  - 文件路径: `app/src/main/java/com/example/codechecker/ui/components/`, various screens

- [ ] T065 [P] Implement app startup optimization
  - 延迟初始化非关键组件
  - 应用启动性能分析（Android Profiler）
  - 预加载常用数据（用户偏好、缓存策略）
  - 文件路径: `app/src/main/java/com/example/codechecker/CodeCheckerApp.kt`, `app/src/main/java/com/example/codechecker/util/StartupOptimizer.kt`

- [ ] T066 [P] Add performance monitoring
  - 集成LeakCanary内存泄漏检测
  - 实现自定义性能监控指标（帧率、启动时间）
  - 添加调试模式下性能日志输出
  - 文件路径: `app/src/main/java/com/example/codechecker/util/PerformanceMonitor.kt`

- [ ] T067 [P] Implement performance regression testing
  - 创建性能基准测试（100份代码<30秒）
  - UI性能自动化测试（响应时间<100ms）
  - 内存使用监控测试
  - 文件路径: `app/src/test/java/com/example/codechecker/performance/`

---

## Phase 11: Final Validation

**Purpose**: 最终验证和交付准备

- [ ] T060 Run full test suite
  - 单元测试 (所有模块)
  - 集成测试 (端到端)
  - UI测试 (关键流程)
  - 性能测试 (基准检查)

- [ ] T061 Validate all success criteria
  - SC-001: 注册登录<30秒 ✅/❌
  - SC-002: UI响应<100ms ✅/❌
  - SC-003: 100份代码查重<30秒 ✅/❌
  - SC-004: 代码提交<5分钟 ✅/❌
  - SC-005: 查看提交历史<3次点击 ✅/❌
  - SC-006: 作业创建<2分钟 ✅/❌
  - SC-007: 查看报告<30秒 ✅/❌
  - SC-008: 代码对比加载<2秒 ✅/❌
  - SC-009: 应用冷启动<3秒 ✅/❌
  - SC-010: 算法测试覆盖率>80% ✅/❌
  - SC-011: 边界情况处理 ✅/❌
  - SC-012: 权限控制生效 ✅/❌
  - SC-013: 用户反馈明确 ✅/❌
  - SC-014: 字体缩放支持 ✅/❌
  - SC-015: 离线查重支持 ✅/❌

- [ ] T062 Final documentation review
  - 验证所有README文件
  - 确认API文档 (如有)
  - 检查快速开始指南
  - 更新版本信息

- [ ] T063 [P] Package and deliver
  - 生成最终APK
  - 创建发布说明
  - 整理源码和文档
  - 准备演示材料

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - **BLOCKS all user stories**
- **User Stories (Phase 3-8)**: All depend on Foundational phase completion
  - User stories can proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Testing (Phase 9)**: Depends on User Stories completion
- **Polish (Phase 10)**: Depends on Testing completion
- **Final Validation (Phase 11)**: Depends on Polish completion

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P1)**: Can start after Foundational (Phase 2) - May integrate with US1 but independently testable
- **User Story 3 (P2)**: Can start after Foundational (Phase 2) - No dependencies on US1/US2
- **User Story 4 (P2)**: Can start after Foundational (Phase 2) - No dependencies on US1/US2/US3
- **User Story 5 (P3)**: Depends on US4 completion - 需要查重引擎和报告数据
- **User Story 6 (P3)**: Optional - Depends on US4/US5, can be skipped

### Within Each User Story

- Models before services
- Services before use cases
- Use cases before UI
- Core implementation before testing
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel (within Phase 2)
- Once Foundational phase completes, all user stories can start in parallel (if team capacity allows)
- Different user stories can be worked on in parallel by different team members

---

## Implementation Strategy

### MVP First (User Stories 1-2 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. Complete Phase 4: User Story 2
5. **STOP and VALIDATE**: Test User Stories 1-2 independently
6. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Add User Story 4 → Test independently → Deploy/Demo (Core functionality complete!)
6. Add User Story 5 → Test → Deploy/Demo
7. Add User Story 6 (Optional) → Test → Deploy/Demo
8. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (Authentication)
   - Developer B: User Story 2 (Code Submission)
   - Developer C: User Story 3 (Assignment Management)
   - Developer D: User Story 4 (Plagiarism Engine) ⭐核心
3. Stories complete and integrate independently
4. Developer A: User Story 5 (Code Comparison) after Story 4
5. Developer B: User Story 6 (AI Analysis) Optional

---

## Notes

- **[P]** tasks = different files, no dependencies
- **[Story]** label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests fail before implementing (TDD approach)
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
- Algorithm module tests MUST achieve >80% coverage
- All核心功能 must work offline (except optional AI analysis)

---

**Tasks Document Completed**: 2025-11-27
**Total Tasks**: 67 tasks (added T064-T067 for performance optimization)
**Estimated Duration**: 21 days
**Critical Path**: Setup → Foundational → US1 → US2 → US4 → Testing
**Next Phase**: Implementation (Phase 3-11)
