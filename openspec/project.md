# Project Context

## Purpose

CodeChecker is an Android application designed for Beihang University's programming courses to detect Python code plagiarism. It enables teachers to create assignments, students to submit Python code, and provides automated similarity detection using tokenization algorithms (Jaccard + LCS similarity calculation). The application supports offline operation with local SQLite storage and features code comparison views with syntax highlighting.

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: Clean Architecture (MVVM pattern)
- **Database**: Room (SQLite) with offline-first design
- **Dependency Injection**: Hilt
- **Async Programming**: Kotlin Coroutines & Flow
- **JSON Serialization**: Kotlinx Serialization
- **Navigation**: Jetpack Navigation Compose
- **Android SDK**: Min API 28 (Android 9.0), Target API 34
- **Additional Libraries**:
  - Accompanist (permissions, system UI controller)
  - DataStore (user preferences)
  - Truth/Mockito (testing)

## Project Conventions

### Code Style

- **Kotlin Coding Conventions**: Follow official Kotlin style guide
- **Naming Conventions**:
  - Database tables: snake_case (e.g., `similarity_pairs`)
  - Kotlin classes: PascalCase (e.g., `SimilarityEntity`)
  - Domain models: PascalCase (e.g., `Similarity`)
  - API responses: camelCase (e.g., `similarityScore`)
- **Architecture Layers**:
  - `data/`: Data sources (local database, mappers, repositories)
  - `domain/`: Business logic (models, use cases, repository interfaces)
  - `ui/`: UI layer (screens, viewmodels, components)

### Architecture Patterns

- **Clean Architecture**: Clear separation between data, domain, and UI layers
- **Repository Pattern**: DataRepositoryImpl implements domain repository interfaces
- **MVVM**: ViewModels handle UI state and business logic coordination
- **Use Cases**: Domain-level business logic encapsulation
- **Dependency Injection**: Hilt modules for object graph management
- **Database Design**:
  - Room entities with proper relationships
  - DAOs for database operations
  - Mappers for entity-domain conversion
  - Database callbacks for migrations

### Testing Strategy

- **Unit Tests**: JUnit 4 + Truth for domain logic and utilities
- **Mocking**: Mockito & Mockito-Kotlin for dependency mocking
- **Coroutine Testing**: kotlinx-coroutines-test for async operations
- **Database Testing**: Room Testing utilities
- **Code Coverage**: Target >80% for algorithm modules
- **Test Naming**: Given/When/Then scenario format

### Git Workflow

- **Branch Naming**:
  - Feature branches: `feature/[feature-name]` or `###-[change-description]`
  - Hotfix branches: `hotfix/[issue-description]`
- **Commit Messages**: Present tense, imperative mood
  - Format: `type(scope): description`
  - Types: feat, fix, docs, test, refactor, chore
  - Example: `feat(auth): add biometric login support`
- **Pull Requests**: Require code review, all tests must pass
- **Change Management**: Use OpenSpec for feature proposals and tracking

### OpenSpec Workflow

This project uses OpenSpec for spec-driven development:

- **Specs** (`openspec/specs/`): Current implemented capabilities (source of truth)
- **Changes** (`openspec/changes/`): Proposed feature changes
- **Archive** (`openspec/changes/archive/`): Completed changes

**Workflow**:
1. Create change proposal with `proposal.md`, `tasks.md`, and spec deltas
2. Validate with `openspec validate [change-id] --strict`
3. Get approval before implementation
4. Implement tasks sequentially from `tasks.md`
5. Archive change after deployment

### Additional Conventions

- **Password Security**: SHA-256 hashing with salt
- **Code Processing**: Remove comments/blank lines, optional identifier normalization
- **Similarity Algorithm**: Weighted score (0.4 * Jaccard + 0.6 * LCS)
- **Progress Tracking**: Real-time UI updates during plagiarism checks
- **Theme Support**: Dark/Light mode with system font scaling (100%-200%)
- **Error Handling**: Comprehensive error messages with user feedback
- **Data Retention**: Fixed-term storage (1 year default) with user deletion capability

## Domain Context

### Core Concepts

- **Users**: Students and teachers with role-based permissions
- **Assignments**: Teacher-created tasks with configurable submission limits and Python version compatibility
- **Submissions**: Student code files with MD5 hashing for duplicate detection
- **Plagiarism Detection**: Token-based similarity analysis with configurable identifier normalization
- **Reports**: Generated similarity analysis with distribution charts and warning lists
- **Code Comparison**: Side-by-side view with syntax-highlighted matching regions

### Business Rules

- User registration requires unique username system-wide
- Students can only view their own submission results
- Teacher assignment submission limits: Small (200), Large (500), Unlimited
- Python version compatibility: 2.x, 3.x, or Both (intelligent detection)
- High similarity threshold: 60% triggers warning
- Offline capability: All features work without internet connection
- Data persistence: Local SQLite with no cloud sync

### Educational Context

- Primary users: Beihang University programming course students and instructors
- Use case: Programming assignment plagiarism detection
- File formats: Python .py files only
- Typical file size: <1MB per file
- Expected submissions: Up to 200 per assignment
- Processing time target: 100 submissions in <30 seconds

## Important Constraints

- **Platform**: Android 9.0+ only (API 28+)
- **Storage**: Local SQLite database, no cloud storage
- **Network**: Offline-first, optional for AI features (future)
- **Security**: Password hashing required, role-based access control
- **Performance**: 100 submissions <30 seconds processing time
- **UI/UX**: Must support Material Design 3, dark/light themes
- **Accessibility**: System font scaling 100%-200%
- **Language**: Chinese UI only (no i18n)
- **Data Retention**: 1-year default retention with auto-deletion
- **Testing**: Algorithm module test coverage >80%

## External Dependencies

- **Google Play Services**: Not required (offline app)
- **AndroidX Libraries**: Core Android components (lifecycle, navigation, compose, etc.)
- **Dagger/Hilt**: Dependency injection framework
- **Room**: SQLite ORM with migration support
- **Material Design 3**: UI components and theming
- **Kotlin Standard Library**: Core language features and collections
- **Coroutines**: Async/await pattern for background tasks
- **Serialization**: JSON parsing for data exchange (future AI features)
- **Accompanist**: System UI controller and permissions handling

### Future External Integrations (Phase 8)

- **AI Services**: DeepSeek, Alibaba Qwen, ModelScope APIs (configurable by user)
- **Cloud Storage**: Optional (not in MVP)

## File Structure Reference

```
app/src/main/java/com/example/codechecker/
├── algorithm/           # Plagiarism detection core
│   ├── tokenizer/      # Python tokenization
│   ├── similarity/     # Jaccard/LCS calculations
│   └── engine/         # Plagiarism detection engine
├── data/
│   ├── local/          # Room database, entities, DAOs
│   ├── mapper/         # Entity-Domain mappers
│   ├── preference/     # DataStore preferences
│   └── repository/     # Repository implementations
├── domain/
│   ├── model/          # Domain models
│   ├── repository/     # Repository interfaces
│   └── usecase/        # Business logic use cases
├── di/                 # Hilt dependency injection modules
├── ui/
│   ├── components/     # Reusable UI components
│   ├── navigation/     # Navigation graphs
│   ├── screens/        # Individual screens (by feature)
│   └── theme/          # Material Design theming
└── util/               # Utilities (crypto, validation, etc.)
```

## OpenSpec Integration

- All new features must have an OpenSpec change proposal
- Use `openspec/` directory for proposals and specs
- Validate proposals: `openspec validate [change-id] --strict`
- Current capabilities documented in `specs/001-code-checker/spec.md`
- Active changes tracked in `changes/` directory
- Archive completed changes with `openspec archive [change-id]`

## Notes

- This is a student/academic project for code plagiarism detection
- Offline-first design suitable for campus network environments
- Algorithm is Token-based (not AST-based) for performance
- UI optimized for mobile screen sizes (phone/tablet)
- No user analytics or telemetry by design (privacy-focused)
