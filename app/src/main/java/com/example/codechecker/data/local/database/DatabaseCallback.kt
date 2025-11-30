package com.example.codechecker.data.local.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
    }
}
