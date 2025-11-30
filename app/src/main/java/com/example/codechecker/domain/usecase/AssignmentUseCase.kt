package com.example.codechecker.domain.usecase

import com.example.codechecker.domain.model.Assignment
import com.example.codechecker.domain.repository.AssignmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssignmentUseCase @Inject constructor(
    private val assignmentRepository: AssignmentRepository
) {
    suspend fun createAssignment(
        title: String,
        description: String,
        teacherId: Long,
        dueDate: Long?,
        submissionLimit: Int,
        pythonVersion: String
    ): Result<Long> {
        return try {
            val assignment = Assignment(
                title = title,
                description = description,
                teacherId = teacherId,
                dueDate = dueDate,
                submissionLimit = com.example.codechecker.domain.model.SubmissionLimit.fromValue(submissionLimit),
                pythonVersion = com.example.codechecker.domain.model.PythonVersion.fromValue(pythonVersion),
                status = com.example.codechecker.domain.model.AssignmentStatus.ACTIVE,
                createdAt = System.currentTimeMillis()
            )
            val assignmentId = assignmentRepository.createAssignment(assignment)
            Result.success(assignmentId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAssignmentById(assignmentId: Long): Assignment? {
        return try {
            assignmentRepository.getAssignmentById(assignmentId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAllAssignments(): List<Assignment> {
        return try {
            assignmentRepository.getAllAssignments()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAssignmentsByTeacher(teacherId: Long): List<Assignment> {
        return try {
            assignmentRepository.getAssignmentsByTeacher(teacherId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAssignmentsByTeacherFlow(teacherId: Long): kotlinx.coroutines.flow.Flow<List<Assignment>> {
        return assignmentRepository.getAssignmentsByTeacherFlow(teacherId)
    }
}
