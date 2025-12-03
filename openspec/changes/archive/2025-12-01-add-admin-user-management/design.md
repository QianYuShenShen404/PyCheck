# Design: Admin User Management System

## Context

The CodeChecker application needs administrative capabilities to support institutional deployment and compliance. The current system supports only Student and Teacher roles, lacking critical features for:

1. **Data Governance**: Managing data retention, cleanup, and archival
2. **User Administration**: Managing users at scale for educational institutions
3. **Compliance**: Audit trails for administrative actions
4. **System Configuration**: Adjusting system behavior for different use cases

This design addresses these needs while maintaining the offline-first, security-conscious architecture of the existing system.

## Goals / Non-Goals

### Goals
- Add ADMIN role with comprehensive management capabilities
- Implement data lifecycle management (cleanup, retention, backup)
- Provide audit trail for compliance and security monitoring
- Enable bulk user operations for institutional deployment
- Support configurable system settings without code changes

### Non-Goals
- Integration with external identity providers (LDAP, SAML, etc.)
- Cloud-based administration or remote management
- Multi-institution/multi-tenant support
- Automated scheduled jobs (manual operations only)
- Advanced analytics beyond basic lists and statistics
- Real-time notifications or alerting system

## Architectural Decisions

### 1. Admin Role Architecture

**Decision**: Add ADMIN as a third role in the existing Role enum

**Rationale**:
- Minimally invasive change to existing user model
- Maintains backward compatibility
- Allows role-based UI and business logic decisions
- Enables hierarchical permissions (Admin > Teacher > Student)

**Implementation**:
- Update `Role` enum to include ADMIN
- Update `UserEntity` to store ADMIN role as string
- Add `is_active` and `status` fields for user management

**Alternatives Considered**:
- Separate Admin table with references to users → More complex, unnecessary normalization
- Permission-based system with granular permissions → Over-engineering for current needs
- Keep admin functionality in Teacher role → Insufficient separation of concerns

### 2. Audit Logging Strategy

**Decision**: Use append-only audit log table for all admin actions

**Schema**:
```kotlin
@Entity(tableName = "admin_audit_logs")
data class AdminAuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val adminUserId: Long,
    val action: String, // e.g., "USER_DELETE", "DATA_CLEANUP"
    val targetType: String, // e.g., "USER", "REPORT", "SYSTEM"
    val targetId: String?,
    val timestamp: Long,
    val result: String, // "SUCCESS", "FAILED", "PARTIAL"
    val details: String? // JSON string with operation details
)
```

**Rationale**:
- Immutable log ensures compliance and forensic capability
- Append-only model prevents tampering
- JSON details allow flexible metadata storage
- Simple to implement and query

**Alternatives Considered**:
- Separate table per operation type → Complex joins, maintenance burden
- Write to external file → Loss of relational context, harder to query
- Event sourcing pattern → Over-engineering, complexity not justified

### 3. Admin Settings Storage

**Decision**: Use singleton key-value settings table with JSON values

**Schema**:
```kotlin
@Entity(tableName = "admin_settings")
data class AdminSettingEntity(
    @PrimaryKey val key: String, // e.g., "similarity_threshold"
    val value: String, // JSON serialized value
    val type: String, // "INT", "STRING", "BOOLEAN", "JSON"
    val updatedAt: Long,
    val updatedBy: Long
)
```

**Rationale**:
- Flexible schema supports multiple data types
- JSON values accommodate complex configurations
- Centralized settings easy to manage and query
- Version control via updatedAt/updatedBy fields

**Alternatives Considered**:
- Hardcode in SharedPreferences → Requires code changes to update
- Separate column per setting → Schema evolution pain, sparse tables
- Room @DatabaseView → Read-only, not suitable for mutable settings

### 4. Data Backup/Export Format

**Decision**: Use JSON for exports, support both CSV and JSON for imports

**Formats**:
- **Export**: Single JSON file per entity type (users.json, assignments.json, etc.)
- **Import**: JSON (primary) and CSV (for bulk import from spreadsheets)

**Rationale**:
- JSON preserves data types and relationships
- CSV compatible with Excel and institutional data exports
- Both formats readable and editable
- Simple to implement without external libraries

**Alternatives Considered**:
- Protocol Buffers/MessagePack → Binary format, harder to debug/edit
- XML → Verbose, overkill for this use case
- SQLite database export → Too granular, harder to import selectively

### 5. Database Migration Strategy

**Decision**: Use Room migrations with fallbackToDestructiveMigration for development

**Migration Plan**:
- Version 1 → 2: Add new columns, add new tables
- Future migrations: Add proper Migration objects
- Development: Keep fallbackToDestructiveMigration for rapid iteration

**Rationale**:
- Preserves user data in production
- Room Migration API well-documented and tested
- fallbackToDestructiveMigration acceptable for development only
- Can transition to proper migrations later

**Alternatives Considered**:
- Manual SQL migration → Error-prone, loses Room type safety
- Destructive migration always → Unacceptable data loss risk
- Never migrate → Stuck on schema version 1

### 6. Permission Enforcement

**Decision**: Check permissions at UI and business logic layers

**Enforcement Points**:
- UI: Hide/show admin screens based on role
- ViewModels: Check role before executing admin operations
- Repository: Validate permissions for data access
- Database: Use DAO queries that filter by user's role (where applicable)

**Rationale**:
- Defense in depth: multiple layers of checks
- UI checks improve UX (no broken features shown)
- Business logic checks ensure security
- Database filters optimize queries

**Alternatives Considered**:
- Database-level RLS (Row Level Security) → Not supported in SQLite
- Single check at entry point → Insufficient, easy to bypass
- Annotation-based permissions → Requires reflection, complex

### 7. Bulk Operations Performance

**Decision**: Use coroutines with chunking and progress callbacks

**Implementation**:
- Process records in chunks (e.g., 100 at a time)
- Emit progress updates via Flow or callback
- Use WITH (IO) dispatcher for database operations
- Show progress to user in real-time

**Rationale**:
- Prevents UI blocking
- Allows cancellation
- Progress feedback improves UX
- Chunking avoids memory issues with large datasets

**Alternatives Considered**:
- Single transaction for all → Memory issues, no progress feedback
- Multiple single-item transactions → Performance overhead
- Background service → Over-engineering for ad-hoc operations

## Risks / Trade-offs

### Risk 1: Admin Feature Abuse

**Risk**: Malicious or negligent admin could misuse features to harm data

**Mitigation**:
- Strong audit trail (all admin actions logged)
- Confirmation dialogs for destructive operations
- Soft delete for users (is_active flag) for recoverability
- Admin actions require explicit confirmation

**Trade-off**: Additional user friction for safety

### Risk 2: Database Migration Failure

**Risk**: Migration could fail and corrupt data

**Mitigation**:
- Backup database before migration
- Test migrations thoroughly
- Keep fallbackToDestructiveMigration for development
- Document rollback procedures

**Trade-off**: Slower development iteration (proper migrations vs destructive)

### Risk 3: Permission Escalation

**Risk**: Non-admin user gains admin access

**Mitigation**:
- Strict role checks in business logic
- Verify role from database, not cached values
- Audit log any role changes
- Multiple permission check points

**Trade-off**: Slight performance overhead for checks

### Risk 4: Large Data Export Performance

**Risk**: Exporting large datasets could timeout or crash

**Mitigation**:
- Stream data instead of loading all into memory
- Chunk processing
- Background coroutines
- Progress indicators

**Trade-off**: More complex implementation code

## Migration Plan

### Phase 1: Database Schema (Day 1-2)
1. Add ADMIN to Role enum
2. Add is_active and status to UserEntity
3. Create AdminAuditLogEntity and AdminSettingEntity
4. Add database migration (v1 → v2)
5. Update DAOs and repositories

### Phase 2: Core Admin Features (Day 3-5)
1. Implement admin use cases (user CRUD, settings management)
2. Create audit logging service
3. Update existing services to use configurable settings
4. Add permission checks to ViewModels

### Phase 3: UI Implementation (Day 6-8)
1. Create admin screens (dashboard, user management, settings)
2. Implement bulk import/export UI
3. Add admin navigation and role-based routing
4. Create confirmation dialogs for destructive operations

### Phase 4: Testing & Polish (Day 9-10)
1. Unit tests for admin use cases
2. Integration tests for database migration
3. UI tests for admin screens
4. Performance testing for bulk operations

### Rollback Plan
1. Revert to previous database version (with data loss if destructive migration used)
2. Hide admin features behind feature flag
3. Disable admin role creation until fixed
4. Document incident and lessons learned

## Open Questions

### Q1: Should Admin users see all data by default?
**Current Decision**: Yes, Admins have full visibility by design
**Rationale**: Administrative oversight requires complete data access

### Q2: How to handle first admin user creation?
**Current Decision**: Database seeding script or manual SQL insert
**Rationale**: No users exist yet, so role-based creation impossible
**Alternative**: First user to register becomes admin (rejected - security risk)

### Q3: Audit log retention period?
**Current Decision**: Keep indefinitely (configurable via admin settings)
**Rationale**: Compliance requirements may vary by institution
**Review**: May change based on institutional feedback

### Q4: Backup file location?
**Current Decision**: Export to user's accessible directory (Downloads folder)
**Rationale**: Follows Android storage best practices
**Alternative**: App-specific directory (less user-friendly for exports)

### Q5: Fast compare mode implementation?
**Current Decision**: Allow disabling identifier normalization
**Rationale**: Speed vs accuracy trade-off, configurable per deployment
**Implementation**: New algorithm mode flag in settings

## Data Models

### Admin User Model
```kotlin
data class AdminUser(
    val id: Long,
    val username: String,
    val displayName: String,
    val role: Role,
    val isActive: Boolean,
    val status: UserStatus,
    val createdAt: Long,
    val lastLoginAt: Long?
)

enum class UserStatus {
    ACTIVE,
    DISABLED,
    PENDING_VERIFICATION
}
```

### Audit Log Model
```kotlin
data class AdminAuditLog(
    val id: Long,
    val adminUserId: Long,
    val adminDisplayName: String,
    val action: String,
    val targetType: String,
    val targetId: String?,
    val timestamp: Long,
    val result: String,
    val details: String?
)
```

### Admin Settings Model
```kotlin
data class AdminSettings(
    val similarityThreshold: Int = 60,
    val reportRetentionDays: Int = 30,
    val submissionRetentionDays: Int = 180,
    val fastCompareMode: Boolean = false,
    val logLevel: LogLevel = LogLevel.INFO,
    val autoCleanupEnabled: Boolean = false,
    val maxSubmissionsPerAssignment: Int = 200
)

enum class LogLevel {
    DEBUG, INFO, WARN, ERROR
}
```

## Testing Strategy

### Unit Tests
- Admin use cases (user CRUD, settings management)
- Permission validation logic
- Audit logging service
- Data export/import parsers
- Database migration scripts

### Integration Tests
- Database migration (v1 → v2)
- Role-based access control
- Bulk operations
- Settings persistence and retrieval

### UI Tests
- Admin screens navigation
- Role-based screen visibility
- Confirmation dialogs
- Progress indicators
- Error handling and feedback

### Performance Tests
- Large dataset export (>1000 users)
- Bulk user import
- Database cleanup on large datasets
- Settings read/write performance

## Security Considerations

1. **Principle of Least Privilege**: Admin users can only perform operations they have permission for
2. **Audit Everything**: All admin actions are logged with details
3. **Confirmation Required**: Destructive operations require explicit confirmation
4. **Role Verification**: Always verify role from database, not cached values
5. **Data Validation**: Validate all inputs for bulk operations
6. **Error Handling**: Don't expose sensitive error details to UI
7. **Session Security**: Optional forced logout capability
8. **Backup Encryption**: Consider encrypting exported data with sensitive information
