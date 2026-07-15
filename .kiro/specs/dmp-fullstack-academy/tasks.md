# Implementation Plan:

## Overview

This plan implements the DMP Full Stack Academy gamified LMS platform across 42 tasks. Tasks are ordered by dependency — infrastructure first, then backend feature modules, followed by frontend implementation. Each task produces testable, deployable increments.

## Tasks

- [ ] 1. Project Scaffolding & Infrastructure Setup: Create the backend Maven project (Spring Boot 3.3.x, Java 21) with pom.xml including Spring Web, Spring Security, Spring Data JPA, Flyway, PostgreSQL driver, Validation, springdoc-openapi, JUnit 5, Mockito, Testcontainers. Create frontend Next.js 14+ project with TypeScript strict mode, Tailwind CSS, shadcn/ui, TanStack Query, React Hook Form, Zod, Zustand, ESLint, Prettier, Husky. Create docker-compose.yml with PostgreSQL 16 (5432), Spring Boot (8080), Next.js (3000) with health checks. Create base package structures for both backend and frontend per design doc. Create Dockerfiles and .env.local.example.
  - Requirements: R24, R25, R28

- [ ] 2. Common Infrastructure & Shared Utilities: Create BaseEntity mapped superclass (UUID id, createdAt). Create ErrorResponse and PageResponse DTOs. Create GlobalExceptionHandler @RestControllerAdvice. Create custom exception classes. Create YouTubeUrlParser and HtmlSanitizer utilities. Create OpenApiConfig and AsyncConfig. Create frontend api-client.ts, utils.ts, types/api.ts, and auth-store.ts (Zustand).
  - Requirements: R23, R25, R26, R27

- [ ] 3. Database Migrations — Core Tables: Create Flyway migrations V1-V15 covering all tables: users, refresh_tokens, categories, courses, modules, lessons, enrollments, lesson_progress, quizzes, quiz_questions, quiz_answer_options, quiz_attempts, quiz_best_scores, coding_challenges, challenge_test_cases, challenge_submissions, challenge_completions, xp_transactions, level_thresholds, badges, user_badges, quests, quest_objectives, student_quest_progress, student_objective_progress, notifications, certificates, course_reviews, learning_paths, learning_path_courses, course_analytics, platform_analytics. Seed level thresholds and initial badges.
  - Requirements: R24

- [ ] 4. User Entity & Security Configuration: Create User JPA entity with all columns, @Version for optimistic locking, Role enum, AccountStatus enum. Create UserRepository with findByEmailIgnoreCase. Create SecurityConfig (filter chain, CORS, public endpoints). Create JwtConfig, JwtService (generate/validate tokens), JwtAuthenticationFilter (reads cookie, sets SecurityContext). Write unit tests for JWT service and filter.
  - Requirements: R2, R3, R26

- [ ] 5. User Registration: Create RegisterRequest record with Bean Validation (email, password 8-128 chars with complexity, displayName 2-50 chars). Create AuthService.register() with email uniqueness check, BCrypt hashing (cost 10), STUDENT role assignment, xp=0, level=1. Create AuthController POST /api/v1/auth/register returning 201. Create UserMapper. Write unit and integration tests.
  - Requirements: R1

- [ ] 6. User Authentication (Login, Refresh, Logout): Create RefreshToken entity and repository. Create LoginAttemptService (5 failures in 15min → lock 30min). Implement AuthService.login() (credentials check, lockout check, issue tokens in HTTP-only secure cookies with SameSite=Strict). Implement refresh with token rotation and replay detection. Implement logout (revoke token, clear cookies). Write unit and integration tests for all auth flows.
  - Requirements: R2, R26

- [ ] 7. Role-Based Authorization & User Management: Create UserService with getCurrentUser, listUsers (ADMIN paginated), updateUserRole (prevent self-change, prevent last-admin removal, revoke refresh tokens). Create UserController with GET /me, GET /users, PUT /users/{id}/role. Add @PreAuthorize annotations. Write unit and integration tests.
  - Requirements: R3, R19

- [ ] 8. Domain Event Infrastructure: Create abstract DomainEvent base class. Create concrete event classes (XpAwardedEvent, CourseCompletedEvent, LessonCompletedEvent, QuizPassedEvent, ChallengeCompletedEvent, BadgeAwardedEvent, CertificateIssuedEvent, StreakMilestoneEvent, CourseEnrolledEvent). Configure AsyncConfig with ThreadPoolTaskExecutor. Create placeholder @TransactionalEventListener @Async listener classes.
  - Requirements: R27

- [ ] 9. Category Management: Create Category entity, CategoryRepository (existsByNameIgnoreCase). Create CategoryService with create (case-insensitive uniqueness) and list. Create CategoryController POST /api/v1/categories (ADMIN) and GET (public). Write unit and integration tests.
  - Requirements: R5

- [ ] 10. Course Management (CRUD): Create Course entity with CourseStatus/Difficulty enums, soft-delete flag, FK to Category and User. Create CourseRepository with custom queries. Create CourseService with create (DRAFT), update (ownership check), publish (validate modules/lessons exist), softDelete (ADMIN), list published, list by instructor. Create CourseController with all endpoints. Write unit and integration tests.
  - Requirements: R4

- [ ] 11. Module Management: Create Module entity with UNIQUE(course_id, order_index). Create ModuleRepository. Create ModuleService with add (auto order_index, ownership), update, delete (cascade + reorder), reorder (validate IDs). Create ModuleController. Write unit and integration tests.
  - Requirements: R7

- [ ] 12. Lesson Management: Create Lesson entity with UNIQUE(module_id, order_index). Create LessonRepository. Create LessonService with create (YouTube URL validation and video ID extraction, ownership), update, delete (reorder), getLesson (enrollment + premium check). Create LessonController. Write unit and integration tests.
  - Requirements: R8

- [ ] 13. Course Enrollment: Create Enrollment entity with UNIQUE(student_id, course_id). Create EnrollmentRepository. Create EnrollmentService with enroll (check published, check not enrolled, check premium, increment enrollment_count, publish event), listEnrollments (paginated). Create EnrollmentController. Write unit and integration tests.
  - Requirements: R6

- [ ] 14. Progress Tracking & Lesson Completion: Create LessonProgress entity with UNIQUE(student_id, lesson_id). Create ProgressService with markLessonComplete (idempotent, calculate completion %, update enrollment, check 100% → publish CourseCompletedEvent), getProgress. Create ProgressController. Integrate XpAwardService and StreakService calls. Write unit and integration tests.
  - Requirements: R15, R11

- [ ] 15. XP Award Service & Level System: Create XpTransaction entity with UNIQUE(student_id, source_type, source_id). Create LevelThreshold entity. Create XpAwardService.awardXp() with idempotency check, atomic XP update, level recalculation, event publishing. Create GamificationController GET /api/v1/gamification/xp. Configure XP amounts in application properties. Write unit and integration tests.
  - Requirements: R11

- [ ] 16. Streak System: Create StreakService.updateStreak() with UTC calendar day logic (same day no-op, yesterday increment, else reset to 1), longest streak tracking, milestone detection (7/30/100 → publish StreakMilestoneEvent). Add GET /api/v1/gamification/streak endpoint. Integrate calls from Progress/Quiz/Challenge services. Write unit and integration tests.
  - Requirements: R12

- [ ] 17. Quiz System: Create Quiz, QuizQuestion, QuizAnswerOption, QuizAttempt, QuizBestScore entities and repositories. Create QuizService with createQuiz (validate structure, ownership), getQuiz (enrollment check, hide answers), submitQuiz (calculate score, store attempt, update best score, first pass → award XP + streak). Create QuizController. Write unit and integration tests.
  - Requirements: R9

- [ ] 18. Coding Challenge System: Create CodingChallenge, ChallengeTestCase, ChallengeSubmission, ChallengeCompletion entities and repositories. Create CodeExecutionService (Docker sandbox, timeout). Create ChallengeService with createChallenge, getChallenge (enrollment), submitCode (execute, store, first completion → award XP + streak). Create ChallengeController. Write unit and integration tests.
  - Requirements: R10

- [ ] 19. Badge System: Create Badge and UserBadge entities/repositories. Create BadgeService.evaluateAndAwardBadge(). Create BadgeEventListener listening for streak/course/XP events. Add GET /api/v1/gamification/badges endpoint. Write unit and integration tests.
  - Requirements: R13

- [ ] 20. Quest System: Create Quest, QuestObjective, StudentQuestProgress, StudentObjectiveProgress entities/repositories. Create QuestService with create (ADMIN, validate), listActive, getWithProgress, checkCompletion (award XP). Create QuestEventListener updating objective progress on activity events. Create QuestController. Write unit and integration tests.
  - Requirements: R14

- [ ] 21. Certificate Generation: Create Certificate entity with UNIQUE(student_id, course_id). Create CertificateService with generate (on course completion, duplicate prevention), list (paginated), verify (public, 404 if not found). Create CertificateEventListener on CourseCompletedEvent. Create CertificateController. Write unit and integration tests.
  - Requirements: R16

- [ ] 22. Notification System: Create Notification entity/repository. Create NotificationService with create, list (paginated, desc by date), markAsRead (ownership check), getUnreadCount, createAnnouncement (ADMIN, for all active users). Create NotificationEventListener on badge/certificate events. Create NotificationController. Write unit and integration tests.
  - Requirements: R17

- [ ] 23. Course Reviews: Create CourseReview entity with UNIQUE(student_id, course_id). Create ReviewService with submit (enrollment check, duplicate prevention, PENDING status), listApproved (paginated), moderate (ADMIN: approve/reject/remove, recalculate avg rating). Create ReviewController. Write unit and integration tests.
  - Requirements: R22

- [ ] 24. Learning Paths: Create LearningPath and LearningPathCourse entities/repositories. Create LearningPathService with create (ADMIN, validate courses published), list (paginated, only with published courses), getById, updateOrder. Create LearningPathController. Write unit and integration tests.
  - Requirements: R5

- [ ] 25. Course Search & Filtering: Add search repository method with JPQL (case-insensitive LIKE on title/description, filter by category/difficulty, only PUBLISHED non-deleted). Implement CourseService.searchCourses() with logical AND filtering. Add GET /api/v1/courses/search endpoint. Write unit and integration tests.
  - Requirements: R21

- [ ] 26. Analytics System: Create CourseAnalytics and PlatformAnalytics entities/repositories. Create AnalyticsEventListener updating aggregates on enrollment/completion/quiz/certificate events. Create AnalyticsService with getCourseAnalytics (ownership check), getLessonAnalytics, getStudentActivity (paginated), getPlatformAnalytics (ADMIN). Create AnalyticsController. Write unit and integration tests.
  - Requirements: R18, R19

- [ ] 27. Student Dashboard API: Create DashboardResponse record aggregating XP, level, streak, enrolled courses with progress, recent 10 activities, active quests. Create DashboardService using optimized queries (@EntityGraph, limited results). Add GET /api/v1/dashboard/student. Write unit and integration tests ensuring single-call response under 2 seconds.
  - Requirements: R20

- [ ] 28. Admin Dashboard API: Add admin endpoints: GET /api/v1/dashboard/admin/analytics (platform totals + DAU), GET /api/v1/dashboard/admin/courses (all courses including drafts with enrollment counts). Implement in AnalyticsService with @PreAuthorize ADMIN. Write integration tests.
  - Requirements: R19

- [ ] 29. Frontend — Auth Features: Create auth feature module (types, api, hooks). Create login-form and register-form components with React Hook Form + Zod. Create login and register pages. Create auth guard layouts for student/instructor/admin. Implement token refresh in api-client. Create use-current-user hook populating Zustand store.
  - Requirements: R1, R2, R25

- [ ] 30. Frontend — Shared Components & Layout: Initialize shadcn/ui components. Create navbar (role-aware), sidebar, footer, loading-spinner, error-boundary, page-header, data-table, youtube-player, xp-progress-bar. Ensure semantic HTML, ARIA labels, keyboard navigation, design tokens.
  - Requirements: R25

- [ ] 31. Frontend — Course Catalog & Search: Create course feature module (types, api, hooks). Create course-card, course-grid, course-filters components. Create catalog page with search/filter/pagination. Create public course detail page. Create learning paths page. Responsive layout with loading/empty states.
  - Requirements: R21, R25

- [ ] 32. Frontend — Student Dashboard: Create gamification feature module. Create xp-card, streak-card, badge-grid, quest-card components. Create student dashboard page aggregating all data. Mobile-first responsive layout with empty state handling.
  - Requirements: R20, R25

- [ ] 33. Frontend — Course Learning Experience: Create lesson feature module (types, api, hooks). Create lesson-sidebar (module/lesson tree with checkmarks), lesson-viewer (text + YouTube + complete button). Create enrolled course page, lesson page, progress page. Implement enrollment hook. Handle premium lesson gating.
  - Requirements: R6, R8, R15, R25

- [ ] 34. Frontend — Quiz Player: Create quiz feature module (types, api, hooks). Create quiz-player component (question navigation, radio answers, submit). Create quiz-results component (score, per-question feedback). Integrate from lesson page. Handle re-submission, accessible radio groups.
  - Requirements: R9, R25

- [ ] 35. Frontend — Coding Challenge Workspace: Create challenge feature module (types, api, hooks). Create code-editor component (Monaco wrapper). Create challenge-workspace (split pane: problem + editor). Create test-results component. Integrate from lesson page. Handle timeout errors, responsive layout.
  - Requirements: R10, R25

- [ ] 36. Frontend — Instructor Dashboard & Course Management: Create instructor analytics module. Create instructor dashboard, course list, create/edit course forms, module/lesson management page (drag-and-drop reorder), course analytics page, student activity table. Implement publish flow with validation feedback.
  - Requirements: R4, R7, R8, R18, R25

- [ ] 37. Frontend — Instructor Quiz & Challenge Creation: Create quiz-form component (dynamic questions/options, validation). Create challenge-form component (Monaco editor for starter code, test cases). Integrate from lesson management. Zod validation matching backend constraints.
  - Requirements: R9, R10, R25

- [ ] 38. Frontend — Admin Dashboard: Create admin dashboard page (platform analytics cards). Create user management page (role change with confirmation). Create courses, categories, learning-paths, quests, reviews moderation, announcements pages. Handle last-admin protection error.
  - Requirements: R5, R14, R17, R19, R22, R25

- [ ] 39. Frontend — Notifications, Badges, Certificates & Reviews: Create notification feature module with bell component and list page. Create badges page, certificates page, quest progress page. Create public certificate verification page. Add review submission to course page. Display reviews on course detail.
  - Requirements: R13, R16, R17, R22, R25

- [ ] 40. Frontend — Landing Page & Public Pages: Create landing page with hero, features, CTA. Create 404 and error pages. Responsive design. WCAG 2.1 AA compliance audit on all public pages (contrast, headings, alt text, focus).
  - Requirements: R25

- [ ] 41. OpenAPI Documentation & API Annotations: Add @Tag, @Operation, @ApiResponse, @Schema annotations to all controllers and DTOs. Verify Swagger UI at /swagger-ui.html and /v3/api-docs in dev profile.
  - Requirements: R23, R28

- [ ] 42. Integration Testing & Docker Compose Validation: Configure Testcontainers base test class. Verify all tests pass (mvn verify). Test Docker Compose full stack startup with health checks. Verify frontend lint and build. Create README.md with setup instructions.
  - Requirements: R28

## Task Dependency Graph

```json
{
  "waves": [
    {"wave": 1, "tasks": [1]},
    {"wave": 2, "tasks": [2]},
    {"wave": 3, "tasks": [3]},
    {"wave": 4, "tasks": [4]},
    {"wave": 5, "tasks": [5, 8, 9]},
    {"wave": 6, "tasks": [6, 7, 10]},
    {"wave": 7, "tasks": [11, 13, 25]},
    {"wave": 8, "tasks": [12, 24]},
    {"wave": 9, "tasks": [14, 17, 18]},
    {"wave": 10, "tasks": [15]},
    {"wave": 11, "tasks": [16, 19, 20]},
    {"wave": 12, "tasks": [21, 23, 26]},
    {"wave": 13, "tasks": [22, 27]},
    {"wave": 14, "tasks": [28]},
    {"wave": 15, "tasks": [29]},
    {"wave": 16, "tasks": [30]},
    {"wave": 17, "tasks": [31, 32, 36, 38, 39, 40]},
    {"wave": 18, "tasks": [33, 34, 35, 37]},
    {"wave": 19, "tasks": [41]},
    {"wave": 20, "tasks": [42]}
  ]
}
```

## Notes

- Tasks 1-3 establish the foundation and must be completed first in sequence.
- Backend tasks (4-28) can be parallelized once their direct dependencies are met.
- Frontend tasks (29-40) depend on their corresponding backend APIs being available.
- Task 42 is the final validation gate before the platform is considered complete.
- Each task should result in a passing build (`mvn verify` for backend, `npm run build` for frontend).
- Integration tests use Testcontainers — no external PostgreSQL needed for testing.
