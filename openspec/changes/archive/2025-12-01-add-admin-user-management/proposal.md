# Change: Add Admin User Management System

## Why

The current CodeChecker application only supports Student and Teacher roles, limiting administrative capabilities for system management, data governance, and compliance. Educational institutions require administrative features to:

- Manage users at scale (create, disable, delete, role changes)
- Enforce data retention policies and perform cleanup
- Monitor system usage and audit compliance
- Export/import data for institutional reporting
- Configure system behavior (similarity thresholds, retention policies)
- Respond to security incidents (forced logout, risk scanning)

Without admin capabilities, the system cannot support proper data lifecycle management, compliance requirements, or operational needs of educational institutions.

## What Changes

### Core Changes

1. **User Role Extension**: Add `ADMIN` role to the existing Role enum, allowing administrative users to access system-wide management features

2. **User Management Capability**:
   - Create/disable/delete users
   - Role升降级 (promote/demote between Student/Teacher/Admin)
   - 重置密码 (reset any user's password)
   - 批量导入/导出用户 (CSV/JSON batch operations)

3. **Data Management Capability**:
   - 清理过期数据 (cleanup expired reports/submissions based on retention policies)
   - 一键重建数据库 (one-click database rebuild for migration recovery)
   - 导出/导入数据 (export/import users, assignments, submissions, reports, similarities)
   - 批量删除/归档报告 (batch delete/archive reports)

4. **System Settings Capability**:
   - 配置相似度阈值 (configure similarity detection threshold)
   - 配置保留策略天数 (configure retention policy days)
   - 开关快速比对模式 (toggle "fast compare" mode)
   - 设置日志级别 (set logging level: DEBUG/INFO/WARN/ERROR)

5. **Audit & Security Capability**:
   - 操作审计日志 (operation audit logs with admin actions)
   - 强制退出登录 (optional forced logout of any user)
   - 风险扫描 (risk scanning for assignments with abnormal high similarity rates)

### Database Schema Changes

**New Tables**:
- `admin_audit_logs`: Track all admin operations (action, target, timestamp, result)
- `admin_settings`: Store system configuration (similarity threshold, retention days, fast mode, log level)

**Modified Tables**:
- `users`: Add `is_active` boolean field for soft delete, add `status` field (ACTIVE/DISABLED)
- Update Role enum to include ADMIN

### UI Changes

**New Screens**:
- Admin Dashboard (main entry point for admin users)
- User Management Screen (list all users, create/edit/delete)
- Data Management Screen (cleanup, backup/restore, storage statistics)
- System Settings Screen (configure thresholds and policies)
- Audit Logs Screen (view operation history)
- Bulk Import/Export Screen (CSV/JSON operations)

**Modified Navigation**:
- Add Admin entry to navigation for users with ADMIN role
- Role-based screen access control throughout the app

### Security Changes

- Role-based access control for admin features
- Audit logging for all admin actions
- Permission checks before sensitive operations
- Optional session management for forced logout

## Impact

### Affected Specs
- **001-code-checker**: Core authentication and user management needs extension
- New spec: admin-user-management (user administration features)
- New spec: admin-data-management (data lifecycle and backup features)
- New spec: admin-system-settings (configuration management)
- New spec: admin-audit-security (compliance and monitoring)

### Affected Code
- `domain/model/User.kt` - Add ADMIN role to Role enum
- `data/local/entity/UserEntity.kt` - Add status and is_active fields
- `data/local/database/AppDatabase.kt` - Add new tables, update version
- `algorithm/similarity/SimilarityCalculator.kt` - Allow configurable threshold
- `util/DataCleanupManager.kt` - Expose configurable retention policies
- `di/DatabaseModule.kt` - Add migration support
- New UI screens in `ui/screens/admin/`
- New use cases in `domain/usecase/` for admin operations

### Breaking Changes
- **Database migration required**: Version 1 → 2
- **User entity changes**: New fields added (non-breaking with migration)
- **Role enum**: ADMIN added (backward compatible)

### Migration Path
1. Database migration: Add new tables and columns
2. Data migration: Update existing users with default is_active=true
3. No code changes required for existing Student/Teacher functionality

## Non-Goals

- User authentication integration with external systems (LDAP, Active Directory)
- Real-time notification system for admin alerts
- Multi-tenancy (single institution deployment)
- Cloud synchronization or remote administration
- Advanced reporting dashboard with charts and analytics (beyond basic lists)
- Scheduled automatic cleanup jobs (manual trigger only in Phase 1)
- Integration with institutional SIS (Student Information Systems)

## Risks

1. **Security Risk**: Admin features could be misused if not properly secured
   - **Mitigation**: Strict role-based access control, audit logging, permission checks

2. **Data Loss Risk**: Database rebuild or bulk delete operations could cause permanent data loss
   - **Mitigation**: Confirmation dialogs for destructive operations, backup recommendations

3. **Performance Risk**: Large data export/import operations could block UI
   - **Mitigation**: Use coroutines with progress reporting, background operations

4. **Complexity Risk**: Multiple admin features increase codebase complexity
   - **Mitigation**: Clear separation of concerns, comprehensive documentation

## Open Questions

1. Should admin users be able to view Student code submissions directly?
2. Should forced logout immediately invalidate the user's session?
3. How should we handle export/import of large datasets (>1000 users)?
4. Should audit logs have a retention period or be kept indefinitely?
5. What is the default similarity threshold and retention days for new deployments?

## Approval Required

This proposal requires approval before implementation as it involves:
- Database schema changes
- New user role and permissions
- Sensitive administrative operations
- Security and compliance implications

## Estimated Effort

- Database changes and migration: 2-3 days
- Core admin features: 5-7 days
- UI screens and testing: 3-4 days
- **Total**: 10-14 days implementation
