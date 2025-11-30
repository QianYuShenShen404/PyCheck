package com.example.codechecker.data.repository

import com.example.codechecker.data.local.dao.AssignmentDao
import com.example.codechecker.data.mapper.AssignmentMapper
import com.example.codechecker.domain.model.Assignment
import com.example.codechecker.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AssignmentRepository
 */
@Singleton
class AssignmentRepositoryImpl @Inject constructor(
    private val assignmentDao: AssignmentDao
) : AssignmentRepository {

    override suspend fun createAssignment(assignment: Assignment): Long {
        val assignmentEntity = AssignmentMapper.toEntity(assignment)
        return assignmentDao.insertAssignment(assignmentEntity)
    }

    override suspend fun updateAssignment(assignment: Assignment) {
        val assignmentEntity = AssignmentMapper.toEntity(assignment)
        assignmentDao.updateAssignment(assignmentEntity)
    }

    override suspend fun deleteAssignment(assignment: Assignment) {
        val assignmentEntity = AssignmentMapper.toEntity(assignment)
        assignmentDao.deleteAssignment(assignmentEntity)
    }

    override suspend fun getAssignmentById(assignmentId: Long): Assignment? {
        val assignmentEntity = assignmentDao.getAssignmentById(assignmentId) ?: return null
        return AssignmentMapper.toDomain(assignmentEntity)
    }

    override fun getAssignmentsByTeacherFlow(teacherId: Long): Flow<List<Assignment>> {
        return assignmentDao.getAssignmentsByTeacherFlow(teacherId).map { entities ->
            AssignmentMapper.toDomainList(entities)
        }
    }

    override fun getActiveAssignmentsFlow(): Flow<List<Assignment>> {
        return assignmentDao.getActiveAssignmentsFlow().map { entities ->
            AssignmentMapper.toDomainList(entities)
        }
    }

    override suspend fun getAllAssignments(): List<Assignment> {
        val entities = assignmentDao.getAllAssignments()
        return AssignmentMapper.toDomainList(entities)
    }

    override suspend fun getAssignmentsByTeacher(teacherId: Long): List<Assignment> {
        val entities = assignmentDao.getAssignmentsByTeacher(teacherId)
        return AssignmentMapper.toDomainList(entities)
    }
}
