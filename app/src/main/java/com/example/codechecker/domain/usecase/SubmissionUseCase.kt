package com.example.codechecker.domain.usecase

import android.content.Context
import android.net.Uri
import com.example.codechecker.domain.model.Submission
import com.example.codechecker.domain.repository.SubmissionRepository
import com.example.codechecker.util.FileUtils
import com.example.codechecker.util.MD5Utils
import com.example.codechecker.util.ValidationUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubmissionUseCase @Inject constructor(
    private val submissionRepository: SubmissionRepository,
    private val fileUtils: FileUtils,
    private val md5Utils: MD5Utils,
    private val validationUtils: ValidationUtils
) {
    suspend fun submitCode(
        assignmentId: Long,
        studentId: Long,
        fileName: String,
        codeContent: String,
        studentNumber: String = "",
        studentName: String = ""
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
                studentNumber = studentNumber,
                studentName = studentName
            )
            val submissionId = submissionRepository.submitCode(submission)
            Result.success(submissionId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitFiles(
        assignmentId: Long,
        fileUris: List<Uri>,
        context: Context
    ) {
        // Implementation for submitting multiple files
        fileUris.forEach { uri ->
            val fileName = fileUtils.getFileNameFromUri(uri)
            val content = fileUtils.readFileFromUri(uri)
            // Submit each file
        }
    }

    suspend fun getSubmissionCountByAssignment(assignmentId: Long): Int {
        return try {
            submissionRepository.getSubmissionCountByAssignment(assignmentId)
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getSubmittedStudentCountByAssignment(assignmentId: Long): Int {
        return try {
            submissionRepository.getSubmittedStudentCountByAssignment(assignmentId)
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getSubmissionsByUser(userId: Long): List<Submission> {
        return try {
            submissionRepository.getSubmissionsByUser(userId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllSubmissionsByAssignment(assignmentId: Long): List<Submission> {
        return try {
            submissionRepository.getAllSubmissionsByAssignment(assignmentId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getSubmissionById(submissionId: Long): Submission? {
        return try {
            submissionRepository.getSubmissionById(submissionId)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun hasStudentSubmittedAssignment(studentId: Long, assignmentId: Long): Boolean {
        return try {
            submissionRepository.hasStudentSubmittedAssignment(studentId, assignmentId)
        } catch (e: Exception) {
            false
        }
    }
}
