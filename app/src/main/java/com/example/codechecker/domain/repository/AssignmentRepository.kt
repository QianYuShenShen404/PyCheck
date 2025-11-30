package com.example.codechecker.domain.repository

import com.example.codechecker.domain.model.Assignment
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Assignment operations
 */
interface AssignmentRepository {

    /**
     * Create a new assignment
     */
    suspend fun createAssignment(assignment: Assignment): Long

    /**
     * Update an assignment
     */
    suspend fun updateAssignment(assignment: Assignment)

    /**
     * Delete an assignment
     */
    suspend fun deleteAssignment(assignment: Assignment)

    /**
     * Get assignment by ID
     */
    suspend fun getAssignmentById(assignmentId: Long): Assignment?

    /**
     * Get assignments by teacher
     */
    fun getAssignmentsByTeacherFlow(teacherId: Long): Flow<List<Assignment>>

    /**
     * Get active assignments
     */
    fun getActiveAssignmentsFlow(): Flow<List<Assignment>>

    /**
     * Get all assignments
     */
    suspend fun getAllAssignments(): List<Assignment>

    /**
     * Get assignments by teacher
     */
    suspend fun getAssignmentsByTeacher(teacherId: Long): List<Assignment>
}
