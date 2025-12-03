# 学号显示问题解决方案文档

## 问题分析

### 问题描述
教师身份进入时，在查看提交的界面中，每个提交的学生学号显示与其注册账号的学号不一致，而是按照提交顺序递增。

### 根本原因

#### 1. **数据结构设计问题**
- **User表** 中学号存储在 `username` 字段中
- **Submission表** 中仅存储 `student_id`（用户ID），没有存储学号信息
- 当查询提交列表时，无法获取到学号，只能获取用户ID

#### 2. **Submission模型缺陷**
- `domain/model/Submission.kt` 没有 `studentNumber` 字段
- `data/local/entity/SubmissionEntity.kt` 没有 `studentNumber` 字段
- 无法在域模型层传递学号信息

#### 3. **数据库查询问题**
- `data/local/dao/SubmissionDao.kt` 的 `getSubmissionsByAssignment()` 方法没有与User表做JOIN
- 无法从数据库获取到学号

#### 4. **映射层问题**
- `data/mapper/SubmissionMapper.kt` 没有映射学号字段

#### 5. **UI层显示问题**
- `ui/screens/assignment/SubmissionListScreen.kt` 第321行显示的是 `submission.studentId`（用户ID）
- 应该显示 `submission.studentNumber`（学号）

#### 6. **数据库架构设计缺陷**
- SubmissionEntity应该冗余存储 `student_number` 和 `student_name` 字段
- 这样可以避免复杂的JOIN查询，提高查询性能

---

## 解决方案

### 整体思路
采用**冗余字段设计**方案，在Submission表中直接存储学号和姓名，避免每次查询都需要JOIN。这样做的优点：
- 查询性能更好（无需JOIN）
- 数据独立性强（学生信息变化不影响已有提交记录）
- 符合反范式化设计原则（为了性能牺牲一定的冗余）

---

## 详细修改方案

### 1. 修改 SubmissionEntity（data/local/entity/SubmissionEntity.kt）

**当前代码：**
```kotlin
@Entity(tableName = "submissions")
data class SubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "student_id")
    val studentId: Long,

    @ColumnInfo(name = "assignment_id")
    val assignmentId: Long,

    @ColumnInfo(name = "file_name")
    val fileName: String,

    @ColumnInfo(name = "code_content")
    val codeContent: String,

    @ColumnInfo(name = "code_hash")
    val codeHash: String,

    @ColumnInfo(name = "status")
    val status: String, // "SUBMITTED", "ANALYZED", "PROCESSED"

    @ColumnInfo(name = "submitted_at")
    val submittedAt: Long
)
```

**需要修改为：**
```kotlin
@Entity(tableName = "submissions")
data class SubmissionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "student_id")
    val studentId: Long,

    @ColumnInfo(name = "assignment_id")
    val assignmentId: Long,

    @ColumnInfo(name = "file_name")
    val fileName: String,

    @ColumnInfo(name = "code_content")
    val codeContent: String,

    @ColumnInfo(name = "code_hash")
    val codeHash: String,

    @ColumnInfo(name = "status")
    val status: String, // "SUBMITTED", "ANALYZED", "PROCESSED"

    @ColumnInfo(name = "submitted_at")
    val submittedAt: Long,

    // 新增字段：学号（冗余存储，提高查询性能）
    @ColumnInfo(name = "student_number")
    val studentNumber: String = "",

    // 新增字段：学生姓名（冗余存储，提高查询性能）
    @ColumnInfo(name = "student_name")
    val studentName: String = ""
)
```

**修改说明：**
- 新增 `studentNumber` 字段：存储学号（来自User的username）
- 新增 `studentName` 字段：存储学生姓名（来自User的displayName）
- 两个字段都有默认值 `""` 以保证向后兼容性
- 这两个字段是冗余字段，提高查询性能但需要在插入时维护一致性

---

### 2. 修改 Submission 模型（domain/model/Submission.kt）

**当前代码：**
```kotlin
data class Submission(
    val id: Long = 0,
    val studentId: Long,
    val assignmentId: Long,
    val fileName: String,
    val codeContent: String,
    val codeHash: String,
    val status: SubmissionStatus,
    val submittedAt: Long,
    val studentName: String = "",
    val fileSize: Long = 0
)
```

**需要修改为：**
```kotlin
data class Submission(
    val id: Long = 0,
    val studentId: Long,
    val assignmentId: Long,
    val fileName: String,
    val codeContent: String,
    val codeHash: String,
    val status: SubmissionStatus,
    val submittedAt: Long,
    val studentNumber: String = "",    // 新增：学号
    val studentName: String = "",      // 已有，保持不变
    val fileSize: Long = 0
)
```

**修改说明：**
- 新增 `studentNumber` 字段到第8位
- 与 `studentName` 字段相邻，便于管理学生信息
- 保持默认值 `""`

---

### 3. 修改 SubmissionMapper（data/mapper/SubmissionMapper.kt）

**当前代码：**
```kotlin
object SubmissionMapper {
    fun toDomain(entity: SubmissionEntity): Submission {
        return Submission(
            id = entity.id,
            studentId = entity.studentId,
            assignmentId = entity.assignmentId,
            fileName = entity.fileName,
            codeContent = entity.codeContent,
            codeHash = entity.codeHash,
            status = SubmissionStatus.fromValue(entity.status),
            submittedAt = entity.submittedAt
        )
    }

    fun toEntity(domain: Submission): SubmissionEntity {
        return SubmissionEntity(
            id = domain.id,
            studentId = domain.studentId,
            assignmentId = domain.assignmentId,
            fileName = domain.fileName,
            codeContent = domain.codeContent,
            codeHash = domain.codeHash,
            status = domain.status.value,
            submittedAt = domain.submittedAt
        )
    }

    fun toDomainList(entities: List<SubmissionEntity>): List<Submission> {
        return entities.map { toDomain(it) }
    }
}
```

**需要修改为：**
```kotlin
object SubmissionMapper {
    /**
     * Convert entity to domain model
     */
    fun toDomain(entity: SubmissionEntity): Submission {
        return Submission(
            id = entity.id,
            studentId = entity.studentId,
            assignmentId = entity.assignmentId,
            fileName = entity.fileName,
            codeContent = entity.codeContent,
            codeHash = entity.codeHash,
            status = SubmissionStatus.fromValue(entity.status),
            submittedAt = entity.submittedAt,
            studentNumber = entity.studentNumber,  // 新增映射
            studentName = entity.studentName       // 新增映射
        )
    }

    /**
     * Convert domain model to entity
     */
    fun toEntity(domain: Submission): SubmissionEntity {
        return SubmissionEntity(
            id = domain.id,
            studentId = domain.studentId,
            assignmentId = domain.assignmentId,
            fileName = domain.fileName,
            codeContent = domain.codeContent,
            codeHash = domain.codeHash,
            status = domain.status.value,
            submittedAt = domain.submittedAt,
            studentNumber = domain.studentNumber,  // 新增映射
            studentName = domain.studentName       // 新增映射
        )
    }

    /**
     * Convert list of entities to domain models
     */
    fun toDomainList(entities: List<SubmissionEntity>): List<Submission> {
        return entities.map { toDomain(it) }
    }
}
```

**修改说明：**
- 在 `toDomain()` 方法中添加 `studentNumber` 和 `studentName` 的映射
- 在 `toEntity()` 方法中添加 `studentNumber` 和 `studentName` 的映射
- `toDomainList()` 方法无需改变，会自动调用 `toDomain()`

---

### 4. 修改 SubmissionUseCase（domain/usecase/SubmissionUseCase.kt）

**当前 submitCode 方法代码：**
```kotlin
suspend fun submitCode(
    assignmentId: Long,
    studentId: Long,
    fileName: String,
    codeContent: String
): Result<Long> {
    return try {
        if (!fileUtils.isPythonFile(fileName)) {
            return Result.failure(Exception("只支持Python文件(.py)"))
        }
        val codeHash = md5Utils.calculateMD5(codeContent)
        val submission = Submission(
            studentId = studentId,
            assignmentId = assignmentId,
            fileName = fileName,
            codeContent = codeContent,
            codeHash = codeHash,
            status = com.example.codechecker.domain.model.SubmissionStatus.SUBMITTED,
            submittedAt = System.currentTimeMillis()
        )
        val submissionId = submissionRepository.submitCode(submission)
        Result.success(submissionId)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

**需要修改为：**
```kotlin
suspend fun submitCode(
    assignmentId: Long,
    studentId: Long,
    fileName: String,
    codeContent: String,
    studentNumber: String = "",    // 新增参数
    studentName: String = ""       // 新增参数
): Result<Long> {
    return try {
        if (!fileUtils.isPythonFile(fileName)) {
            return Result.failure(Exception("只支持Python文件(.py)"))
        }
        val codeHash = md5Utils.calculateMD5(codeContent)
        val submission = Submission(
            studentId = studentId,
            assignmentId = assignmentId,
            fileName = fileName,
            codeContent = codeContent,
            codeHash = codeHash,
            status = com.example.codechecker.domain.model.SubmissionStatus.SUBMITTED,
            submittedAt = System.currentTimeMillis(),
            studentNumber = studentNumber,  // 新增赋值
            studentName = studentName       // 新增赋值
        )
        val submissionId = submissionRepository.submitCode(submission)
        Result.success(submissionId)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

**修改说明：**
- 增加 `submitCode` 方法的 `studentNumber` 和 `studentName` 参数
- 两个参数都有默认值 `""` 以保证向后兼容性
- 在创建Submission对象时将这两个参数赋值给对应字段

---

### 5. 修改 SubmissionDao（data/local/dao/SubmissionDao.kt）

**需要新增方法（不修改现有方法，只添加新方法）：**

```kotlin
/**
 * Get all submissions for an assignment with student info (using JOIN)
 * This method retrieves submissions with student number and name from the User table
 *
 * @param assignmentId the assignment ID
 * @return list of SubmissionEntity with populated studentNumber and studentName
 */
@Query("""
    SELECT
        s.id, s.student_id, s.assignment_id, s.file_name,
        s.code_content, s.code_hash, s.status, s.submitted_at,
        u.username as student_number, u.displayName as student_name
    FROM submissions s
    LEFT JOIN users u ON s.student_id = u.id
    WHERE s.assignment_id = :assignmentId
    ORDER BY s.submitted_at DESC
""")
suspend fun getSubmissionsByAssignmentWithStudentInfo(assignmentId: Long): List<SubmissionEntity>
```

**修改说明：**
- 新增方法 `getSubmissionsByAssignmentWithStudentInfo()`
- 使用LEFT JOIN与users表关联，获取学号和姓名
- 返回的SubmissionEntity会自动填充 `student_number` 和 `student_name` 字段
- 保持原有 `getSubmissionsByAssignment()` 方法不变，用于其他用途

---

### 6. 修改 SubmissionRepositoryImpl（data/repository/SubmissionRepositoryImpl.kt）

**当前 getAllSubmissionsByAssignment 方法：**
```kotlin
override suspend fun getAllSubmissionsByAssignment(assignmentId: Long): List<Submission> {
    val entities = submissionDao.getSubmissionsByAssignment(assignmentId)
    return SubmissionMapper.toDomainList(entities)
}
```

**需要修改为：**
```kotlin
override suspend fun getAllSubmissionsByAssignment(assignmentId: Long): List<Submission> {
    // 使用新的DAO方法获取带有学生信息的提交
    val entities = submissionDao.getSubmissionsByAssignmentWithStudentInfo(assignmentId)
    return SubmissionMapper.toDomainList(entities)
}
```

**修改说明：**
- 改为调用新的 `getSubmissionsByAssignmentWithStudentInfo()` 方法
- 这样可以在数据库层直接获取学号和姓名，而不需要额外的处理

---

### 7. 修改 SubmissionListScreen（ui/screens/assignment/SubmissionListScreen.kt）

**当前 SubmissionCard 函数中显示学号的部分（第320-324行）：**
```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Text(
        text = "学生学号: ${submission.studentId}",  // 问题：显示的是用户ID而不是学号
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    TextButton(onClick = onViewClick) {
        Text("查看")
    }
}
```

**需要修改为：**
```kotlin
Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Text(
        text = "学生学号: ${submission.studentNumber}",  // 修复：显示正确的学号
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    TextButton(onClick = onViewClick) {
        Text("查看")
    }
}
```

**修改说明：**
- 将 `submission.studentId` 改为 `submission.studentNumber`
- `studentId` 是数据库内部的用户ID（通常是自增的）
- `studentNumber` 是用户注册时的学号（用户可见的标识）

---

### 8. 其他相关更新（可选但推荐）

#### SubmissionDetailScreen（ui/screens/submission/SubmissionDetailScreen.kt）
如果该页面也显示学号，需要进行相同修改：
- 将 `submission.studentId` 改为 `submission.studentNumber`

#### SubmissionHistoryScreen（ui/screens/submission/SubmissionHistoryScreen.kt）
如果该页面也显示学号，需要进行相同修改：
- 将 `submission.studentId` 改为 `submission.studentNumber`

---

## 数据库迁移方案

### 如果需要添加数据库迁移（MIGRATION_3_4）

由于SubmissionEntity添加了新字段，如果现有App已有用户，需要创建数据库迁移：

**创建文件：** `app/src/main/java/com/example/codechecker/data/local/database/migrations/MIGRATION_3_4.kt`

```kotlin
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加 student_number 列
        database.execSQL(
            "ALTER TABLE submissions ADD COLUMN student_number TEXT NOT NULL DEFAULT ''"
        )

        // 添加 student_name 列
        database.execSQL(
            "ALTER TABLE submissions ADD COLUMN student_name TEXT NOT NULL DEFAULT ''"
        )

        // 从 users 表中更新学号和姓名
        database.execSQL("""
            UPDATE submissions
            SET student_number = (SELECT username FROM users WHERE users.id = submissions.student_id),
                student_name = (SELECT displayName FROM users WHERE users.id = submissions.student_id)
        """)
    }
}
```

**在 AppDatabase 中更新：**
```kotlin
@Database(
    entities = [UserEntity::class, AssignmentEntity::class, SubmissionEntity::class, /* ... */],
    version = 4,  // 从3改为4
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    companion object {
        val MIGRATION_3_4 = // ... 上面定义的迁移

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, "codechecker_db")
                .addMigrations(MIGRATION_1_2, MIGRATION_3_4)  // 添加新的迁移
                .build()
        }
    }
}
```

---

## 总结

### 修改的文件列表

| 文件路径 | 修改内容 | 优先级 |
|---------|--------|-------|
| `data/local/entity/SubmissionEntity.kt` | 添加 `studentNumber` 和 `studentName` 字段 | **高** |
| `domain/model/Submission.kt` | 添加 `studentNumber` 字段 | **高** |
| `data/mapper/SubmissionMapper.kt` | 添加字段映射 | **高** |
| `domain/usecase/SubmissionUseCase.kt` | 修改 `submitCode()` 方法参数 | **中** |
| `data/local/dao/SubmissionDao.kt` | 添加新的查询方法 | **高** |
| `data/repository/SubmissionRepositoryImpl.kt` | 修改调用的DAO方法 | **高** |
| `ui/screens/assignment/SubmissionListScreen.kt` | 修改显示的字段 | **高** |
| `ui/screens/submission/SubmissionDetailScreen.kt` | 修改显示的字段（如果需要） | **低** |
| `ui/screens/submission/SubmissionHistoryScreen.kt` | 修改显示的字段（如果需要） | **低** |

### 实现顺序建议
1. **数据层先行**：Entity → Mapper → DAO → Repository
2. **业务层跟进**：UseCase
3. **UI层最后**：SubmissionListScreen 及相关页面
4. **数据库迁移**：最后执行（如果有现有数据）

### 验证清单
- [ ] 编译通过，无编译错误
- [ ] 新建提交时，学号被正确存储到数据库
- [ ] 查询提交列表时，学号被正确显示
- [ ] 学号显示与注册账号一致（不是递增ID）
- [ ] 现有功能没有被破坏
- [ ] 数据库迁移成功（如适用）
