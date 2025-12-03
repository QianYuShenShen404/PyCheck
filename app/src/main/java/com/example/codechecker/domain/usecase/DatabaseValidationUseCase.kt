package com.example.codechecker.domain.usecase

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.codechecker.data.local.database.AppDatabase
import com.example.codechecker.util.Logger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseValidationUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val logger: Logger
) {
    suspend fun validateOnce() {
        val prefs = context.getSharedPreferences("codechecker_validation", Context.MODE_PRIVATE)
        val done = prefs.getBoolean("schema_validated", false)
        if (done) return
        validateSchema()
        prefs.edit().putBoolean("schema_validated", true).putLong("schema_validated_at", System.currentTimeMillis()).apply()
    }

    private suspend fun validateSchema() = withContext(Dispatchers.IO) {
        val db: SupportSQLiteDatabase = database.openHelper.writableDatabase
        logger.info("DB_VALIDATION_START")

        validateTable(
            db,
            table = "users",
            expected = setOf(
                "id","username","passwordHash","displayName","role","createdAt","isActive","status"
            )
        )

        validateTable(
            db,
            table = "assignments",
            expected = setOf(
                "id","title","description","teacher_id","due_date","submission_limit","python_version","status","created_at"
            )
        )

        validateTable(
            db,
            table = "submissions",
            expected = setOf(
                "id","student_id","assignment_id","file_name","code_content","code_hash","status","submitted_at"
            )
        )

        validateTable(
            db,
            table = "plagiarism_reports",
            expected = setOf(
                "id","assignment_id","executor_id","status","total_submissions","total_pairs","created_at","completed_at"
            )
        )

        validateTable(
            db,
            table = "similarity_pairs",
            expected = setOf(
                "id","report_id","submission1_id","submission2_id","similarity_score","jaccard_score","lcs_score","highlight_data","ai_analysis","created_at"
            )
        )

        validateTable(
            db,
            table = "admin_audit_logs",
            expected = setOf(
                "id","adminUserId","action","targetType","targetId","timestamp","result","details"
            )
        )

        validateTable(
            db,
            table = "admin_settings",
            expected = setOf(
                "key","value","type","updatedAt","updatedBy"
            )
        )

        validateIndex(
            db,
            table = "submissions",
            indexName = "idx_submissions_assignment_status",
            indexColumns = listOf("assignment_id","status")
        )

        validateIndex(
            db,
            table = "similarity_pairs",
            indexName = "idx_similarity_report_score",
            indexColumns = listOf("report_id","similarity_score")
        )

        validateIndex(
            db,
            table = "assignments",
            indexName = "idx_assignments_teacher_status",
            indexColumns = listOf("teacher_id","status")
        )

        logger.info("DB_VALIDATION_END")
    }

    private fun validateTable(db: SupportSQLiteDatabase, table: String, expected: Set<String>) {
        val cursor = db.query("PRAGMA table_info($table)")
        val actual = mutableSetOf<String>()
        val nameIndex = cursor.getColumnIndex("name")
        while (cursor.moveToNext()) {
            actual.add(cursor.getString(nameIndex))
        }
        cursor.close()
        val missing = expected - actual
        val extra = actual - expected
        if (missing.isEmpty() && extra.isEmpty()) {
            logger.info("DB_TABLE_OK:$table")
        } else {
            if (missing.isNotEmpty()) logger.warning("DB_TABLE_MISSING:$table:${missing.joinToString(",")}")
            if (extra.isNotEmpty()) logger.warning("DB_TABLE_EXTRA:$table:${extra.joinToString(",")}")
        }
    }

    private fun validateIndex(db: SupportSQLiteDatabase, table: String, indexName: String, indexColumns: List<String>) {
        val idxList = db.query("PRAGMA index_list($table)")
        var found = false
        val nameIdx = idxList.getColumnIndex("name")
        while (idxList.moveToNext()) {
            val name = idxList.getString(nameIdx)
            if (name == indexName) {
                found = true
                break
            }
        }
        idxList.close()
        if (!found) {
            logger.warning("DB_INDEX_MISSING:$table:$indexName")
            return
        }
        val info = db.query("PRAGMA index_info($indexName)")
        val colIdx = info.getColumnIndex("name")
        val cols = mutableListOf<String>()
        while (info.moveToNext()) {
            cols.add(info.getString(colIdx))
        }
        info.close()
        if (cols.map { it.lowercase() } == indexColumns.map { it.lowercase() }) {
            logger.info("DB_INDEX_OK:$indexName")
        } else {
            logger.warning("DB_INDEX_DIFF:$indexName:${cols.joinToString(",")}")
        }
    }
}

