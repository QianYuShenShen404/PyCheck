# Python代码查重助手 (CodeChecker) Constitution

<!--
Sync Impact Report - Initial Creation
Version: N/A → 1.0.0
- New constitution created for CodeChecker Android application
- Established 5 core principles covering code quality, technical constraints, UX, security, and testing
- Added 2 additional sections: Decision Priorities and Documentation Requirements
- No template updates required as this is initial creation
- No deferred items
-->

## Core Principles

### I. 代码质量标准 (Code Quality Standards)

**所有代码必须使用 Kotlin 语言，禁止使用 Java。** 项目必须严格遵循 MVVM + Clean Architecture 架构模式，实现清晰的三层分离：Presentation/Domain/Data。所有代码遵循统一的命名规范：类名使用 PascalCase，函数/变量使用 camelCase，常量使用 SCREAMING_SNAKE_CASE，包名全小写。所有公共 API 必须配备 KDoc 注释，复杂算法需要行内注释说明。坚持单一职责原则，每个类/函数只负责一项职责，函数长度不超过 30 行。

### II. 技术约束 (Technical Constraints)

**项目最低支持 Android 9.0 (API 28+)。** UI 开发优先使用 Jetpack Compose 和 Material Design 3 风格。数据存储仅使用 Room 本地数据库，严格禁止依赖远程服务器。依赖注入必须使用 Hilt。异步处理统一使用 Kotlin Coroutines + Flow，禁止使用 RxJava。**核心查重算法必须本地实现，完全不依赖网络服务。**

### III. 用户体验标准 (User Experience Standards)

**UI 操作响应时间必须小于 100ms。** 所有用户操作必须有明确的成功/失败反馈机制。**核心功能（查重）必须支持离线使用，优先离线体验。** 应用必须支持系统字体缩放，所有关键操作配备内容描述以确保无障碍访问。查重计算过程中必须显示实时进度。

### IV. 安全要求 (Security Requirements)

**用户密码必须使用 SHA-256 加密存储，严格禁止明文存储。** 教师和学生角色必须严格隔离，学生绝对不能访问其他学生的提交记录。文件访问权限仅限用户主动选择的文件，禁止未经授权的文件读取。安全事件必须完整记录并可追溯。

### V. 测试标准 (Testing Standards)

**算法模块单元测试覆盖率必须大于 80%。** 必须处理所有边界情况：空文件、超大文件、特殊字符等。每次修改查重算法后必须运行完整测试套件执行回归测试。集成测试必须覆盖：新增算法接口测试、接口变更测试、模块间通信测试、共享数据模式测试。

## VI. 决策优先级 (Decision Priorities)

当面临技术选型或实现方案冲突时，决策顺序为：**正确性优先于性能**（宁可执行缓慢也不能产生错误结果）；**可维护性优先于功能丰富度**；**用户体验优先于技术炫酷**；**必做功能优先于选做功能**。所有偏离此优先级顺序的决策必须在文档中明确记录理由和权衡分析。

## VII. 文档要求 (Documentation Requirements)

每个模块必须配备 README 文件，说明其职责和使用方式。数据库表结构变更必须记录迁移日志。API 变更必须同步更新对应的接口文档。技术决策记录必须包含：决策背景、备选方案、选择理由、影响分析。所有文档必须保持与代码实现同步更新。

## Governance

**本宪法优先于所有其他开发实践和约定。** 所有 PR 代码审查必须验证宪法遵循情况。任何复杂度的增加必须提供明确理由。违背宪法的变更需要提供详细的例外申请，包括：为什么需要此例外、更简单的替代方案、为什么被拒绝、使用时长限制、退出计划。

**宪法修订流程**：
- MAJOR 版本变更：向后不兼容的治理/原则移除或重新定义
- MINOR 版本变更：新增原则/章节或重要扩展指导
- PATCH 版本变更：澄清、措辞、修正性错误修复、非语义优化

**遵循性审查**：
- 所有新功能开发前必须通过宪法检查（GATE）
- 每次重大架构决策前必须进行宪法符合性审查
- 定期（每两周）进行项目实践与宪法的一致性审计
- 违反宪法的行为必须记录在复杂度跟踪表中

**指导文件**：
运行时开发指导请参考项目 README 和各模块内的指南文档。

**Version**: 1.0.0 | **Ratified**: 2025-11-27 | **Last Amended**: 2025-11-27
