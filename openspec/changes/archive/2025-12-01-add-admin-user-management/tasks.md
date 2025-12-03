# Implementation Tasks: Add Admin User Management System

## Phase 1: Database Schema & Core Infrastructure (Days 1-3)

### 1.1 Update User Model and Database Schema
- [x] 1.1.1 Add ADMIN to Role enum in domain/model/User.kt
- [x] 1.1.2 Add is_active and status fields to UserEntity
- [x] 1.1.3 Create AdminAuditLogEntity with all required fields
- [x] 1.1.4 Create AdminSettingEntity for system configuration
- [x] 1.1.5 Update AppDatabase to include new entities
- [x] 1.1.6 Create database migration v1 → v2
- [x] 1.1.7 Update UserDao to support new fields and queries
- [x] 1.1.8 Create AdminAuditLogDao for audit operations
- [x] 1.1.9 Create AdminSettingDao for settings management
- [x] 1.1.10 Test database migration with sample data

**Verification**: Run database, verify migration succeeds, new tables/columns exist

### 1.2 Update User Repository and Mappers
- [x] 1.2.1 Update UserMapper to handle is_active and status fields
- [x] 1.2.2 Update UserRepository interface with admin operations (create, update, delete, list)
- [x] 1.2.3 Implement admin operations in UserRepositoryImpl
- [x] 1.2.4 Update mappers for new User fields
- [x] 1.2.5 Add password reset functionality to repository

**Verification**: Unit tests pass for user repository operations

### 1.3 Create Domain Models for Admin Features
- [x] 1.3.1 Create AdminAuditLog domain model
- [x] 1.3.2 Create AdminSetting domain model
- [x] 1.3.3 Create UserStatus enum
- [x] 1.3.4 Create AdminSettings data class
- [x] 1.3.5 Create LogLevel enum

**Verification**: Models compile, serialization works correctly

### 1.4 Update User Registration Logic
- [x] 1.4.1 Modify registration to handle ADMIN role
- [x] 1.4.2 Update AuthUseCase to support role validation
- [x] 1.4.3 Add password hashing on registration
- [x] 1.4.4 Update LoginViewModel to handle admin role
- [x] 1.4.5 Test registration with all three roles

**Verification**: Can register and login as Student, Teacher, and Admin

## Phase 2: Admin Use Cases and Business Logic (Days 4-6)

### 2.1 Implement Admin User Management Use Cases
- [x] 2.1.1 Create GetAllUsersUseCase
- [x] 2.1.2 Create CreateUserUseCase
- [x] 2.1.3 Create UpdateUserUseCase
- [x] 2.1.4 Create DeleteUserUseCase (soft delete)
- [x] 2.1.5 Create ChangeUserRoleUseCase
- [x] 2.1.6 Create ResetPasswordUseCase
- [x] 2.1.7 Create DisableUserUseCase
- [x] 2.1.8 Create EnableUserUseCase

**Verification**: Unit tests pass for all use cases

### 2.2 Implement Admin Settings Management Use Cases
- [x] 2.2.1 Create GetAdminSettingsUseCase
- [x] 2.2.2 Create UpdateAdminSettingsUseCase
- [x] 2.2.3 Create ResetSettingsToDefaultUseCase
- [x] 2.2.4 Create ExportSettingsUseCase
- [x] 2.2.5 Create ImportSettingsUseCase
- [x] 2.2.6 Add settings validation logic

**Verification**: Settings persist correctly, validation works

### 2.3 Implement Data Management Use Cases
- [x] 2.3.1 Extend DataCleanupManager to use configurable retention
- [x] 2.3.2 Create ExportDataUseCase (JSON/CSV)
- [x] 2.3.3 Create ImportDataUseCase (JSON/CSV)
- [x] 2.3.4 Create DatabaseBackupUseCase
- [x] 2.3.5 Create DatabaseRestoreUseCase
- [x] 2.3.6 Create StorageStatisticsUseCase
- [x] 2.3.7 Add progress callbacks for long operations

**Verification**: Export/import works, cleanup functions correctly

### 2.4 Implement Audit Logging Service
- [x] 2.4.1 Create AuditLogger service
- [x] 2.4.2 Create log() method for all admin actions
- [x] 2.4.3 Create GetAuditLogsUseCase with filtering
- [x] 2.4.4 Add audit logging to all admin use cases
- [x] 2.4.5 Create SecurityMonitoringService
- [x] 2.4.6 Implement risk scanning for high similarity
- [x] 2.4.7 Add session management for forced logout

**Verification**: All admin actions logged, filters work correctly

### 2.5 Update Core Algorithm Settings
- [x] 2.5.1 Modify SimilarityCalculator to read threshold from settings
- [x] 2.5.2 Update PlagiarismEngine to use configurable settings
- [x] 2.5.3 Add "fast compare mode" to algorithm
- [x] 2.5.4 Add identifier normalization toggle
- [x] 2.5.5 Update report generation to use configured threshold

**Verification**: Algorithm uses settings, threshold changes affect results

## Phase 3: UI Implementation - Admin Screens (Days 7-10)

### 3.1 Create Admin Navigation and Dashboard
- [x] 3.1.1 Add Admin role to navigation routing
- [x] 3.1.2 Create AdminDashboardScreen
- [x] 3.1.3 Create AdminDashboardViewModel
- [x] 3.1.4 Add summary cards (users, assignments, reports)
- [x] 3.1.5 Add quick action buttons
- [x] 3.1.6 Show recent audit log entries
- [x] 3.1.7 Add role-based UI hiding for non-admin users

**Verification**: Admin can access dashboard, non-admin users cannot

### 3.2 Create User Management Screen
- [x] 3.2.1 Create UserManagementScreen
- [x] 3.2.2 Create UserManagementViewModel
- [x] 3.2.3 Display paginated user list with filtering
- [x] 3.2.4 Add "Create User" dialog
- [x] 3.2.5 Add "Edit User" dialog
- [x] 3.2.6 Add role change functionality (basic UI)
- [x] 3.2.7 Add disable/enable user buttons
- [x] 3.2.8 Add soft delete functionality
- [x] 3.2.9 Add password reset dialog
- [x] 3.2.10 Add export users button (CSV/JSON)
- [x] 3.2.11 Add import users button (CSV/JSON)
- [x] 3.2.12 Add confirmation dialogs for destructive operations

**Verification**: All CRUD operations work, bulk import/export functions

### 3.3 Create Data Management Screen
- [x] 3.3.1 Create DataManagementScreen
- [x] 3.3.2 Create DataManagementViewModel
- [x] 3.3.3 Display storage statistics
- [x] 3.3.4 Add retention policy configuration
- [x] 3.3.5 Add manual cleanup with progress bar
- [x] 3.3.6 Add cleanup preview functionality
- [x] 3.3.7 Add export data section (JSON/CSV)
- [x] 3.3.8 Add import data section
- [x] 3.3.9 Add database backup/restore section
- [x] 3.3.10 Add batch report management
- [x] 3.3.11 Add pre-migration check

**Verification**: All data operations work with progress feedback

### 3.4 Create System Settings Screen
- [x] 3.4.1 Create SystemSettingsScreen (basic stub)
- [x] 3.4.2 Create SystemSettingsViewModel
- [x] 3.4.3 Add similarity threshold slider
- [x] 3.4.4 Add retention policy inputs
- [x] 3.4.5 Add fast compare mode toggle
- [x] 3.4.6 Add logging level selector
- [x] 3.4.7 Add system parameter inputs
- [x] 3.4.8 Add settings export/import
- [x] 3.4.9 Add reset to defaults buttons
- [x] 3.4.10 Add settings validation feedback
- [x] 3.4.11 Display settings change history

**Verification**: Settings persist, validation works, changes apply

### 3.5 Create Audit Logs Screen
- [x] 3.5.1 Create AuditLogsScreen
- [x] 3.5.2 Create AuditLogsViewModel
- [x] 3.5.3 Display paginated audit log entries
- [x] 3.5.4 Add date range filter
- [x] 3.5.5 Add action type filter
- [x] 3.5.6 Add search functionality
- [x] 3.5.7 Add expandable details for each entry
- [x] 3.5.8 Add export logs button
- [x] 3.5.9 Add audit log retention settings

**Verification**: Logs display correctly, filters work

### 3.6 Create Security & Risk Alerts Screen
- [x] 3.6.1 Create SecurityScreen
- [x] 3.6.2 Create SecurityViewModel
- [x] 3.6.3 Display risk alerts list
- [x] 3.6.4 Add alert filtering by severity/type
- [x] 3.6.5 Add dismiss alert functionality
- [x] 3.6.6 Add active sessions list
- [x] 3.6.7 Add force logout buttons
- [x] 3.6.8 Add bulk logout functionality
- [x] 3.6.9 Add security settings configuration

**Verification**: Security features work, alerts display correctly

## Phase 4: Integration & Testing (Days 11-12)

### 4.1 Update Existing Code to Use Configurable Settings
- [x] 4.1.1 Update DataCleanupManager to use settings
- [x] 4.1.2 Update all references to hardcoded thresholds
- [x] 4.1.3 Update logging to use configured log level
- [x] 4.1.4 Ensure fast compare mode affects algorithm
- [x] 4.1.5 Verify retention settings apply to cleanup

**Verification**: Settings changes affect all dependent code

### 4.2 Permission and Security Testing
- [x] 4.2.1 Verify Student cannot access admin features
- [x] 4.2.2 Verify Teacher cannot access admin features
- [x] 4.2.3 Verify Admin can access all features
- [x] 4.2.4 Test forced logout works
- [x] 4.2.5 Verify disabled users cannot login
- [x] 4.2.6 Test unauthorized access attempts are logged

**Verification**: All permission checks pass, security enforced

### 4.3 Performance and Bulk Operations Testing
- [x] 4.3.1 Test bulk import of 100+ users
- [x] 4.3.2 Test bulk export of large datasets
- [x] 4.3.3 Test database cleanup on large dataset
- [x] 4.3.4 Verify progress indicators work
- [x] 4.3.5 Test concurrent admin operations

**Verification**: Bulk operations complete without UI freezing

### 4.4 Database Migration Testing
- [x] 4.4.1 Test migration v1 → v2 with sample data
- [x] 4.4.2 Test rollback procedure
- [x] 4.4.3 Test database restore from backup
- [x] 4.4.4 Verify data integrity after migration
- [x] 4.4.5 Test fallbackToDestructiveMigration behavior

**Verification**: Migration works correctly, no data loss

### 4.5 End-to-End Testing
- [x] 4.5.1 Create admin user and test all admin features
- [x] 4.5.2 Test complete user lifecycle (create → disable → delete)
- [x] 4.5.3 Test configuration changes and algorithm updates
- [x] 4.5.4 Test data export/import workflow
- [x] 4.5.5 Test audit logging for all operations
- [x] 4.5.6 Test security features (forced logout, risk alerts)
- [x] 4.5.7 Verify all confirmation dialogs appear
- [x] 4.5.8 Test error handling and user feedback

**Verification**: Complete admin workflow functional

### 4.6 Unit Tests
- [x] 4.6.1 Write unit tests for admin use cases (80% coverage target)
- [x] 4.6.2 Write unit tests for audit logging service
- [x] 4.6.3 Write unit tests for settings management
- [x] 4.6.4 Write unit tests for data export/import
- [x] 4.6.5 Write unit tests for permission checks

**Verification**: All unit tests pass, coverage >80%

### 4.7 Integration Tests
- [x] 4.7.1 Test database migration integration
- [x] 4.7.2 Test repository and DAO integration
- [x] 4.7.3 Test ViewModel and UseCase integration
- [x] 4.7.4 Test settings persistence across app restarts

**Verification**: Integration tests pass

### 4.8 UI Tests
- [x] 4.8.1 Write UI tests for admin navigation
- [x] 4.8.2 Write UI tests for user management screen
- [x] 4.8.3 Write UI tests for data management screen
- [x] 4.8.4 Write UI tests for system settings screen
- [x] 4.8.5 Write UI tests for audit logs screen
- [x] 4.8.6 Verify all dialogs and confirmation screens

**Verification**: UI tests pass, all screens render correctly

### 4.9 Documentation and Polish
- [x] 4.9.1 Add inline code comments for complex logic
- [x] 4.9.2 Update AGENTS.md if needed
- [x] 4.9.3 Add error messages for all failure scenarios
- [x] 4.9.4 Test on different screen sizes (phone/tablet)
- [x] 4.9.5 Verify Material Design 3 compliance
- [x] 4.9.6 Test dark/light theme compatibility
- [x] 4.9.7 Add loading states for all async operations

**Verification**: Code review passes, documentation complete

### 4.10 Final Validation
- [x] 4.10.1 Run openspec validate add-admin-user-management --strict
- [x] 4.10.2 Verify all requirements have scenarios
- [x] 4.10.3 Check all spec files are properly formatted
- [x] 4.10.4 Run detekt code analysis
- [x] 4.10.5 Run all tests (unit, integration, UI)
- [x] 4.10.6 Verify no breaking changes to existing features

**Verification**: All validation checks pass

## Dependencies and Parallel Work

### Can Be Done in Parallel:
- Database schema updates (Task 1.1) → UI screens (Tasks 3.1-3.6)
- Use case implementation (Tasks 2.1-2.5) → UI screens
- Audit logging (Task 2.4) → All other admin features

### Critical Path:
1. Database schema must be complete before use cases
2. Use cases must be complete before UI integration
3. Admin features must be complete before permission testing
4. All features complete before end-to-end testing

## Success Criteria

- [x] All tasks completed with verification steps passing
- [x] No regressions in existing Student/Teacher functionality
- [x] Admin can manage users, data, settings, and view audits
- [x] All security features working (permissions, forced logout, risk alerts)
- [x] Database migration successful with no data loss
- [x] Bulk operations handle 100+ records without performance issues
- [x] Test coverage >80% for admin modules
- [x] OpenSpec validation passes with --strict flag

## Notes

- Estimated total effort: 12 days
- Focus on security and permission enforcement
- Comprehensive testing critical due to admin capabilities
- Document all admin actions for audit trail
- Consider gradual rollout (disable admin features initially, enable after testing)
