# Design Document: Interactive Learning Experience

## Overview

This design transforms DMP Full Stack Academy into a CodeSignal/Codecademy-level interactive learning platform. The existing Spring Boot backend entities and Next.js frontend provide the foundation — this design focuses on the interactive frontend experiences, data seeding, and integration between existing services.

## Architecture

### System Context

```
┌─────────────────────────────────────────────────────────────────┐
│                    Next.js 14 Frontend                            │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────────┐  │
│  │  Lesson  │ │   Quiz   │ │   Code   │ │   Learning Path   │  │
│  │  Viewer  │ │  Player  │ │  Editor  │ │      Pages        │  │
│  └──────────┘ └──────────┘ └──────────┘ └───────────────────┘  │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌───────────────────┐  │
│  │Dashboard │ │   Cert   │ │  Course  │ │    Admin Panel    │  │
│  │(Enhanced)│ │  Viewer  │ │  Builder │ │                   │  │
│  └──────────┘ └──────────┘ └──────────┘ └───────────────────┘  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │             Gamification Animation Layer                   │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
                              │ REST API
┌─────────────────────────────────────────────────────────────────┐
│                 Spring Boot 3.3.x Backend                        │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌─────────────┐  │
│  │   Course   │ │   Lesson   │ │    Quiz    │ │  Challenge  │  │
│  │  Service   │ │  Service   │ │  Service   │ │   Service   │  │
│  └────────────┘ └────────────┘ └────────────┘ └─────────────┘  │
│  ┌────────────┐ ┌────────────┐ ┌────────────┐ ┌─────────────┐  │
│  │Gamification│ │Certificate │ │  Progress  │ │ LearningPath│  │
│  │  Engine    │ │  Service   │ │  Service   │ │   Service   │  │
│  └────────────┘ └────────────┘ └────────────┘ └─────────────┘  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │              DataSeeder (ApplicationRunner)                  │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────────┐
│                     PostgreSQL 18                                 │
│  courses, modules, lessons, quizzes, challenges, enrollments,    │
│  progress, xp_transactions, streaks, badges, quests,            │
│  certificates, notifications, reviews, learning_paths, analytics │
└─────────────────────────────────────────────────────────────────┘
```

### Technology Decisions

| Concern | Decision | Rationale |
|---------|----------|-----------|
| Code Editor | Monaco Editor (via @monaco-editor/react) | Same editor as VS Code, excellent syntax highlighting, language support, lightweight |
| Animations | Framer Motion | Already popular in React ecosystem, declarative API, supports reduced motion |
| Quiz State | React useState + TanStack Query | Quiz is a linear flow, local state sufficient, server sync on submit |
| Data Seeding | Spring Boot ApplicationRunner | Runs once on startup, conditional on empty tables, transactional |
| YouTube Embed | iframe with youtube-nocookie.com | Privacy-enhanced, no extra dependencies |
| Toast Notifications | sonner (or custom) | Lightweight, accessible, fits existing patterns |

---

## Components and Interfaces

### 1. Data Seeder (Backend)

**File:** `backend/src/main/java/com/dmpacademy/config/DataSeeder.java`

**Approach:** An `ApplicationRunner` bean that checks if courses exist. If the `courses` table is empty, it seeds all 58+ lessons across 6 courses, 11 categories/tracks, and 3 learning paths.

```
DataSeeder implements ApplicationRunner
├── run(ApplicationArguments)
│   ├── Check if courses table is empty
│   ├── If not empty → return (idempotent)
│   ├── Create categories (Frontend, Backend, DevOps, Fundamentals)
│   ├── Create instructor user (DMP Academy system account)
│   ├── Create courses with modules and lessons (58+ lessons)
│   │   ├── Git Fundamentals (8 lessons, 2 modules)
│   │   ├── HTML & CSS Fundamentals (10 lessons, 3 modules)
│   │   ├── JavaScript Essentials (12 lessons, 3 modules)
│   │   ├── TypeScript Deep Dive (8 lessons, 2 modules)
│   │   ├── React Development (10 lessons, 3 modules)
│   │   └── Next.js Mastery (10 lessons, 2 modules)
│   ├── Create sample quizzes (1 per module)
│   ├── Create sample coding challenges (2 per course)
│   └── Create learning paths linking courses in order
```

**Key Data:**
- Each lesson has: title, videoId (YouTube), textContent (markdown summary), orderIndex
- Each course has: title, description, category, status=PUBLISHED, difficulty
- Learning paths: Full-Stack (all 6), Frontend (HTML/CSS, JS, TS, React, Next.js), Backend (JS, TS, Spring Boot references)

### 2. Learning Path Pages (Frontend)

**Routes:**
- `/paths` — List all learning paths
- `/paths/[pathId]` — Single path detail with course progression

**Components:**
```
/paths/page.tsx
├── PathCard (title, description, courseCount, progress bar)
└── Fetches: GET /api/v1/learning-paths

/paths/[pathId]/page.tsx
├── PathHeader (title, description, overall progress)
├── PathCourseList
│   └── PathCourseItem (course card + status: locked/in-progress/complete)
└── Fetches: GET /api/v1/learning-paths/{id}
             GET /api/v1/learning-paths/{id}/progress (authenticated)
```

**Backend additions:**
- `GET /api/v1/learning-paths/{id}/progress` — returns per-course completion for the authenticated student

### 3. Interactive Lesson Viewer (Frontend Enhancement)

**Route:** `/learn/[courseId]/[lessonId]` (existing, enhanced)

**Enhanced Layout:**
```
┌─────────────────────────────────────────────────────┐
│ Course Title                    Progress: 45% ██░░░ │
├──────────────┬──────────────────────────────────────┤
│  Sidebar     │   Main Content                       │
│  ─────────   │   ┌─────────────────────────────┐   │
│  Module 1    │   │   YouTube Video Embed        │   │
│  ✓ Lesson 1  │   │                              │   │
│  ✓ Lesson 2  │   └─────────────────────────────┘   │
│  ► Lesson 3  │                                      │
│    Lesson 4  │   Lesson Title                       │
│              │   ─────────────                      │
│  Module 2    │   📝 Lesson Notes                    │
│    Lesson 5  │   [text content]                     │
│    Lesson 6  │                                      │
│              │   ┌────────────────────────────┐     │
│              │   │ ← Back │ ✓ Mark Complete → │     │
│              │   └────────────────────────────┘     │
│              │   [+10 XP animation on complete]     │
└──────────────┴──────────────────────────────────────┘
```

**New components:**
- `LessonSidebar` — collapsible module/lesson tree with completion status
- `XpRewardPopup` — animated XP notification (Framer Motion)
- `LessonNavigation` — previous/next lesson buttons

**Backend additions:**
- `GET /api/v1/courses/{courseId}/lessons` — returns all lessons grouped by module with completion status

### 4. Interactive Quiz Player (Frontend)

**Route:** `/quiz/[quizId]`

**Flow:**
```
Start Screen → Question 1 → Question 2 → ... → Results Screen
     │              │              │                    │
     │         Option Select  Option Select        Score + XP
     │         → Next         → Next               Pass/Fail
     │                                             Retry button
```

**Components:**
```
QuizPlayerPage
├── QuizStartScreen (title, question count, "Begin" button)
├── QuizQuestion
│   ├── ProgressBar (question N of M)
│   ├── QuestionText
│   ├── OptionList → OptionItem (radio-style, highlight on select)
│   └── NextButton (disabled until selection)
├── QuizResultScreen
│   ├── ScoreDisplay (fraction + percentage + pass/fail)
│   ├── XpRewardPopup (if passed)
│   └── ActionButtons (Retry / Back to Lesson)
```

**State management:** Local useState for quiz progression. Submit all answers on completion via `POST /api/v1/quizzes/{quizId}/submit`.

### 5. In-Browser Code Editor (Frontend)

**Route:** `/challenge/[challengeId]`

**Layout:**
```
┌──────────────────────────────┬──────────────────────────────┐
│  Problem Description          │  Code Editor (Monaco)        │
│  ─────────────────────        │  ┌────────────────────────┐  │
│  Challenge Title              │  │ function solution() {  │  │
│  Difficulty: Medium           │  │   // your code here    │  │
│                               │  │ }                      │  │
│  Description text...          │  │                        │  │
│                               │  └────────────────────────┘  │
│  ─────────────────────        │                              │
│  Test Cases:                  │  [Language ▾] [Reset] [Run ▶]│
│  ┌─────────────────────┐     │                              │
│  │ Test 1: ✓ Passed    │     │  Output:                     │
│  │ Test 2: ✗ Failed    │     │  ┌────────────────────────┐  │
│  │   Expected: "hello" │     │  │ Test 1: ✓ Passed       │  │
│  │   Got: "Hello"      │     │  │ Test 2: ✗ Failed       │  │
│  │ Test 3: ○ Pending   │     │  │ 2/3 tests passing      │  │
│  └─────────────────────┘     │  └────────────────────────┘  │
└──────────────────────────────┴──────────────────────────────┘
```

**Components:**
```
ChallengeEditorPage
├── ProblemPanel
│   ├── ChallengeHeader (title, difficulty, language)
│   ├── ChallengeDescription (markdown rendered)
│   └── TestCaseList → TestCaseItem (input, expected, status)
├── EditorPanel
│   ├── MonacoEditor (syntax highlighting, auto-complete)
│   ├── EditorToolbar (language select, reset, run)
│   └── OutputPanel (test results after execution)
```

**Backend endpoint:** `POST /api/v1/challenges/{id}/submit` — existing `ChallengeController` handles this.

### 6. Certificate Viewer (Frontend)

**Routes:**
- `/certificates/[certificateId]` — Authenticated certificate view
- `/verify/[certificateId]` — Public verification page (no auth required)

**Design:**
```
┌─────────────────────────────────────────────────┐
│              🏆 Certificate of Completion         │
│                                                   │
│          This certifies that                      │
│                                                   │
│            [Student Name]                         │
│                                                   │
│    has successfully completed the course          │
│                                                   │
│        [Course Title]                             │
│                                                   │
│    Issued: [Date]                                 │
│    Certificate ID: [UUID]                         │
│                                                   │
│    [Copy Link]  [Download]                        │
└─────────────────────────────────────────────────┘
```

**Backend:** Existing `CertificateController` has `/api/v1/certificates/{id}/verify` (no auth).

### 7. Enhanced Student Dashboard (Frontend)

**Route:** `/dashboard` (existing, enhanced)

**Layout:**
```
┌─────────────────────────────────────────────────────────────┐
│  Welcome back, [Name]!                                       │
├──────────────────────────────┬──────────────────────────────┤
│  XP Progress Ring            │  Streak Fire + Badge Grid     │
│  Level 5 • 1250/2000 XP     │  🔥 7 day streak             │
├──────────────────────────────┴──────────────────────────────┤
│  My Courses (enrolled)                                       │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐                    │
│  │Course 1  │ │Course 2  │ │Course 3  │                    │
│  │███░░ 60% │ │██░░░ 40% │ │░░░░░ 10% │                    │
│  └──────────┘ └──────────┘ └──────────┘                    │
├─────────────────────────────────────────────────────────────┤
│  Active Quests                 │  Recent Activity            │
│  ┌─────────────────────────┐  │  • Completed Lesson 5       │
│  │ Complete 5 lessons ██░░ │  │  • Earned "Quick Learner"   │
│  │ Pass 3 quizzes    █░░░ │  │  • Passed JS Quiz           │
│  └─────────────────────────┘  │  • Started React Course     │
└─────────────────────────────────────────────────────────────┘
```

**Backend additions:**
- `GET /api/v1/dashboard/student` — enhanced to include recent activity, active quests, badges
- Or separate endpoints: `/api/v1/activity/recent`, `/api/v1/quests/active`, `/api/v1/badges/mine`

### 8. Instructor Course Builder (Frontend)

**Routes:**
- `/instructor/dashboard` — Course list + "Create Course" button
- `/instructor/courses/[courseId]/edit` — Course editor with tabbed sections
- `/instructor/courses/[courseId]/modules` — Module management
- `/instructor/courses/[courseId]/modules/[moduleId]/lessons` — Lesson management

**Flow:**
```
Create Course → Add Modules → Add Lessons → Add Quiz/Challenge → Publish
     ↕              ↕              ↕               ↕               ↕
  Form modal    Sortable list   Form + YT URL   Question builder  Validation
```

**Components:**
```
InstructorDashboard
├── CourseList (cards with status badges)
├── CreateCourseModal (title, desc, category, difficulty)
CourseEditor
├── CourseInfoTab (edit title, description, thumbnail)
├── ModulesTab
│   ├── ModuleList (drag-to-reorder)
│   ├── AddModuleForm
│   └── ModuleItem → LessonList
│       ├── AddLessonForm (title, YouTube URL, content)
│       ├── AddQuizForm (questions + options)
│       └── AddChallengeForm (description, template, test cases)
├── PublishTab (validation checklist + publish button)
```

### 9. Admin Panel (Frontend)

**Routes:**
- `/admin/dashboard` — Overview stats
- `/admin/users` — User management table
- `/admin/courses` — Course moderation
- `/admin/categories` — Category CRUD
- `/admin/quests` — Quest creation
- `/admin/reviews` — Review moderation
- `/admin/announcements` — Broadcast announcements

**Components:** Standard CRUD tables with pagination, search, and action buttons. Uses existing backend endpoints with `@PreAuthorize("hasRole('ADMIN')")`.

### 10. Gamification Animations (Frontend)

**Global Animation Layer:**

```
// components/gamification/GamificationProvider.tsx
// Wraps the app, listens for gamification events via React context

GamificationProvider
├── XpPopup (floating +10 XP, Framer Motion animate)
├── LevelUpModal (full-screen celebration, auto-dismiss 4s)
├── BadgeToast (slide-in toast with badge icon, 5s)
└── StreakFire (CSS fire animation on streak indicator)
```

**Trigger mechanism:** After successful mutations (lesson complete, quiz pass, challenge solve), the response includes XP earned and any new badges/levels. The mutation's `onSuccess` callback triggers the animation via context.

---

## API Additions

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/api/v1/courses/{id}/lessons-tree` | All lessons grouped by module with completion status |
| GET | `/api/v1/learning-paths/{id}/progress` | Per-course completion in a learning path |
| GET | `/api/v1/dashboard/enhanced` | Full dashboard data (XP, streak, badges, quests, activity) |
| GET | `/api/v1/activity/recent` | Last 10 learning activities for current user |
| GET | `/api/v1/quests/active` | Active quests with progress for current user |
| POST | `/api/v1/admin/announcements` | Broadcast notification to all users |
| PUT | `/api/v1/admin/users/{id}/role` | Change user role |
| PUT | `/api/v1/admin/courses/{id}/status` | Approve/reject/unpublish course |

---

## Data Flow

### Lesson Completion Flow
```
Student clicks "Mark Complete"
→ POST /lessons/{id}/complete
→ LessonProgressService records completion
→ XpAwardService awards XP (event-driven)
→ StreakService updates streak
→ BadgeEventListener checks badge criteria
→ Response: { xpEarned: 10, newLevel: null, newBadge: null, streak: 7 }
→ Frontend triggers XpPopup animation
→ Frontend refreshes progress sidebar
```

### Quiz Submission Flow
```
Student completes quiz
→ POST /quizzes/{id}/submit { answers: [...] }
→ QuizService scores answers
→ If passed: XpAwardService awards XP
→ Response: { score: 8, total: 10, passed: true, xpEarned: 25 }
→ Frontend shows ResultScreen + XpPopup
```

### Code Challenge Flow
```
Student clicks "Run"
→ POST /challenges/{id}/submit { code, language }
→ CodeExecutionService runs against test cases (5s timeout)
→ If all pass: XpAwardService awards XP
→ Response: { allPassed: true, results: [...], xpEarned: 50 }
→ Frontend shows test results + XpPopup if passed
```

---

## Data Models

The data model for this feature builds on the existing database schema already defined in the Spring Boot backend entities. The primary tables involved are:

| Table | Purpose |
|-------|---------|
| `courses` | Course metadata (title, description, category, difficulty, status) |
| `modules` | Logical groupings of lessons within a course (title, orderIndex) |
| `lessons` | Individual learning units (title, videoId, textContent, orderIndex) |
| `quizzes` | Quiz definitions with questions and options |
| `coding_challenges` | Challenge definitions with test cases and templates |
| `enrollments` | Student-course enrollment records |
| `lesson_progress` | Per-student lesson completion tracking |
| `xp_transactions` | XP award records (amount, source, timestamp) |
| `streaks` | Daily streak tracking per student |
| `badges` / `user_badges` | Badge definitions and student badge awards |
| `quests` | Quest definitions with progress tracking |
| `certificates` | Generated certificates (student, course, UUID, date) |
| `learning_paths` | Ordered sequences of courses forming a track |
| `learning_path_courses` | Join table linking paths to courses with orderIndex |
| `notifications` | System notifications and announcements |
| `reviews` | Course reviews with moderation status |
| `categories` | Course categorization (Frontend, Backend, DevOps, Fundamentals) |

No new tables are introduced by this feature. All interactive experiences (lesson viewer, quiz player, code editor, dashboard, etc.) consume and produce data through the existing entity relationships. The seed data service populates these existing tables with initial content.

---

## Correctness Properties

*A property is a characteristic or behavior that should hold true across all valid executions of a system — essentially, a formal statement about what the system should do. Properties serve as the bridge between human-readable specifications and machine-verifiable correctness guarantees.*

### Property 1: Seed Data Idempotency

*For any* number of times N the Seed_Data_Service runs, the resulting database state SHALL be identical to running it exactly once — no duplicate courses, modules, lessons, or learning paths are created.

**Validates: Requirements 1.5**

### Property 2: Sequential Order Index Integrity

*For any* course and its modules, and *for any* module and its lessons, the orderIndex values SHALL form a contiguous sequence starting from 0 (or 1) with no gaps and no duplicates.

**Validates: Requirements 1.4, 2.5**

### Property 3: Learning Path Progress Accuracy

*For any* student enrolled in courses within a learning path, the displayed progress percentage SHALL equal the number of fully completed courses divided by the total courses in the path, multiplied by 100.

**Validates: Requirements 2.3**

### Property 4: XP Award on Lesson Completion

*For any* lesson and *for any* student, marking the lesson as complete SHALL result in exactly one XP transaction being recorded for that lesson-student pair (no duplicate awards on repeated marking).

**Validates: Requirements 3.3**

### Property 5: Quiz Score Accuracy

*For any* quiz with N questions and *for any* set of student answers, the computed score SHALL equal the count of correct answers divided by N, and the pass/fail status SHALL be determined by comparing this ratio to the quiz's passing threshold.

**Validates: Requirements 4.4, 4.6**

### Property 6: Best Score Tracking

*For any* sequence of quiz attempts by a student, the stored score SHALL always equal the maximum score achieved across all attempts.

**Validates: Requirements 4.6**

### Property 7: YouTube Video ID Extraction

*For any* valid YouTube URL (watch format, short URL, or embed format), the Course_Builder extraction function SHALL produce the correct 11-character video ID, and formatting that ID back into a URL SHALL reference the same video.

**Validates: Requirements 8.9**

### Property 8: Publication Validation Gate

*For any* course configuration, the Course_Builder SHALL allow publication if and only if the course contains at least one module that itself contains at least one lesson.

**Validates: Requirements 8.8**

### Property 9: Certificate Generation Completeness

*For any* student who has completed all lessons and passed all quizzes in a course, the Platform SHALL have exactly one certificate record generated for that student-course pair.

**Validates: Requirements 6.6**

---

## Error Handling

| Scenario | Handling Strategy |
|----------|-------------------|
| **Invalid YouTube video URL** | Lesson viewer displays a placeholder message ("Video unavailable") while still rendering text content below (Req 3.7) |
| **Code execution timeout** | Backend terminates the process after 5 seconds; frontend displays "Execution timed out" error with suggestion to check for infinite loops (Req 5.6) |
| **Code runtime error** | Backend catches stderr output; frontend displays formatted error message with line number if available (Req 5.7) |
| **Invalid certificate UUID** | Public verification page returns 404 with a user-friendly "Certificate not found" message (Req 6.4) |
| **Seed data partial failure** | Entire seeding runs in a single `@Transactional` block — any failure rolls back all inserts to maintain consistency |
| **Quiz navigation interruption** | Browser `beforeunload` event prompts confirmation dialog before leaving the quiz page (Req 4.7) |
| **Duplicate XP award attempt** | Backend uses idempotency check (lesson_id + user_id unique constraint on progress) to prevent awarding XP twice for the same lesson |
| **Unauthorized admin/instructor access** | Spring Security `@PreAuthorize` annotations return 403 Forbidden; frontend redirects to appropriate dashboard |
| **Network failure during code submission** | Frontend displays a retry-able error toast; Run button re-enables so the student can try again |
| **Empty dashboard state** | When no courses are enrolled, dashboard shows a call-to-action directing to the course catalog (Req 7.7) |

---

## Testing Strategy

### Unit Tests
- Quiz score calculation logic
- YouTube video ID extraction from various URL formats
- Progress percentage computation
- Course publication validation rules
- XP award idempotency checks

### Property-Based Tests (using jqwik for Java / fast-check for TypeScript)
- **Minimum 100 iterations per property**
- Each property test tagged with: **Feature: interactive-learning-experience, Property {N}: {title}**
- Focus areas: seed idempotency, score calculation, order index integrity, URL parsing round-trip, publication validation

### Integration Tests
- Lesson completion → XP award → streak update flow
- Quiz submission → scoring → best score tracking
- Code execution → test case evaluation → XP award
- Certificate auto-generation on course completion
- Seed data service end-to-end (verify all 58+ lessons seeded)
- Public certificate verification endpoint (no auth)

### End-to-End Tests
- Full lesson completion flow (video → mark complete → XP animation → next lesson)
- Quiz player start-to-finish (start → answer questions → results)
- Code editor submit → test results display
- Learning path enrollment → course progression → path completion

---

## Security Considerations

- Code execution uses ProcessBuilder with strict timeouts (5s default)
- In production, code execution should use Docker containers for sandboxing
- Admin endpoints protected by `@PreAuthorize("hasRole('ADMIN')")`
- Instructor endpoints protected by `@PreAuthorize("hasRole('INSTRUCTOR')")`
- Public certificate verification endpoint requires no authentication
- YouTube embeds use `youtube-nocookie.com` for privacy

## Performance Considerations

- Lesson tree endpoint uses JOIN FETCH to avoid N+1 queries
- Dashboard endpoint aggregates data in a single query where possible
- Monaco Editor loaded lazily (dynamic import) to avoid blocking initial page load
- Learning path progress cached on enrollment changes
- Seed data runs in a single transaction to ensure consistency
