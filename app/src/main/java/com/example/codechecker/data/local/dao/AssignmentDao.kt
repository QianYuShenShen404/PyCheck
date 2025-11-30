package com.example.codechecker.data.local.dao

import androidx.room.*
import com.example.codechecker.data.local.entity.AssignmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity): Long

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    @Delete
    suspend fun deleteAssignment(assignment: AssignmentEntity)

    @Query("SELECT * FROM assignments WHERE id = :assignmentId")
    suspend fun getAssignmentById(assignmentId: Long): AssignmentEntity?

    @Query("SELECT * FROM assignments WHERE teacher_id = :teacherId ORDER BY created_at DESC")
    fun getAssignmentsByTeacherFlow(teacherId: Long): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE teacher_id = :teacherId ORDER BY created_at DESC")
    suspend fun getAssignmentsByTeacher(teacherId: Long): List<AssignmentEntity>

    @Query("SELECT * FROM assignments WHERE status = 'ACTIVE' ORDER BY due_date ASC")
    fun getActiveAssignmentsFlow(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE status = 'ACTIVE' ORDER BY due_date ASC")
    suspend fun getActiveAssignments(): List<AssignmentEntity>

    @Query("SELECT * FROM assignments ORDER BY created_at DESC")
    suspend fun getAllAssignments(): List<AssignmentEntity>
}
