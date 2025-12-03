package com.example.codechecker.data.local.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Database callback for initialization
 */
@Singleton
class DatabaseCallback @Inject constructor() : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        
        // Create indexes for better query performance
        db.execSQL("""
            CREATE INDEX IF NOT EXISTS idx_submissions_assignment_status
            ON submissions(assignment_id, status)
        """)
        
        db.execSQL("""
            CREATE INDEX IF NOT EXISTS idx_similarity_report_score
            ON similarity_pairs(report_id, similarity_score DESC)
        """)
        
        db.execSQL("""
            CREATE INDEX IF NOT EXISTS idx_assignments_teacher_status
            ON assignments(teacher_id, status)
        """)

        val cursor = db.query("SELECT COUNT(*) FROM users")
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()

        if (count == 0) {
            val username = "admin"
            val password = "BuaaAdmin#2025!"
            val displayName = "系统管理员"
            val role = "ADMIN"
            val createdAt = System.currentTimeMillis()
            val passwordHash = sha256(password)
            db.execSQL(
                "INSERT INTO users(username, passwordHash, displayName, role, createdAt, isActive, status) VALUES(?,?,?,?,?,?,?)",
                arrayOf(username, passwordHash, displayName, role, createdAt, 1, "ACTIVE")
            )
        }
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        val sb = StringBuilder(bytes.size * 2)
        for (b in bytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
}
