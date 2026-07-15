# Technical Design Document

## Overview

This document defines the technical architecture and design for DMP Full Stack Academy, a gamified full-stack learning management platform. The system follows a client-server architecture with a Spring Boot 3.3.x REST API backend and a Next.js 14+ frontend, communicating over HTTPS with JWT-based authentication via HTTP-only cookies.

## Architecture

### System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Client Layer                             │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │           Next.js 14+ (App Router, TypeScript)             │  │
│  │  ┌─────────┐  ┌─────────────┐  ┌──────────────────────┐  │  │
│  │  │ shadcn/ │  │  TanStack   │  │  React Hook Form     │  │  │
│  │  │   ui    │  │   Query     │  │  + Zod Validation    │  │  │
│  │  └─────────┘  └─────────────┘  └──────────────────────┘  │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────┬───────────────────────────────────────┘
                          │ HTTPS (JWT in HTTP-only cookies)
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway Layer                         │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │         Spring Security Filter Chain                       │  │
│  │  ┌──────────┐  ┌──────────────┐  ┌────────────────────┐  │  │
│  │  │  CORS    │  │  JWT Auth    │  │  Rate Limiting     │  │  │
│  │  │  Filter  │  │  Filter      │  │  (Login attempts)  │  │  │
│  │  └──────────┘  └──────────────┘  └────────────────────┘  │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Application Layer                           │
│  ┌────────┐ ┌────────┐ ┌────────┐ ┌────────┐ ┌─────────────┐  │
│  │  Auth  │ │ Course │ │  Quiz  │ │Progress│ │Gamification │  │
│  │ Module │ │ Module │ │ Module │ │ Module │ │   Module    │  │
│  └───┬────┘ └───┬────┘ └───┬────┘ └───┬────┘ └──────┬──────┘  │
│      │          │          │          │             │           │
│      └──────────┴──────────┴──────────┴─────────────┘           │
│                          │                                       │
│              ┌───────────▼────────────┐                          │
│              │  Domain Event Bus      │                          │
│              │  (ApplicationEvent     │                          │
│              │   Publisher)           │                          │
│              └───────────┬────────────┘                          │
│                          │                                       │
│      ┌───────────────────┼───────────────────┐                   │
│      ▼                   ▼                   ▼                   │
│  ┌────────┐      ┌─────────────┐     ┌──────────┐              │
│  │Notif.  │      │  Analytics  │     │  Badge   │              │
│  │Listener│      │  Listener   │     │ Listener │              │
│  └────────┘      └─────────────┘     └──────────┘              │
└─────────────────────────┬───────────────────────────────────────┘
                          ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Data Layer                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              Spring Data JPA / Hibernate                    │  │
│  └───────────────────────────┬───────────────────────────────┘  │
│                              ▼                                    │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                    PostgreSQL 16                            │  │
│  │              (Flyway-managed migrations)                    │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### Component Interaction Flow

```
Student Browser → Next.js App → API Client (fetch + cookies)
    → Spring Boot Controller (validation, @PreAuthorize)
    → Service Layer (business logic)
    → Repository Layer (JPA)
    → PostgreSQL

Cross-cutting flow:
    Service completes action → publishes DomainEvent
    → @TransactionalEventListener (AFTER_COMMIT)
    → NotificationListener creates notification
    → AnalyticsListener updates aggregates
    → BadgeListener evaluates criteria
```

## Data Models

### Entity Relationship Diagram

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│    users     │       │   courses    │       │  categories  │
├──────────────┤       ├──────────────┤       ├──────────────┤
│ id (PK)      │       │ id (PK)      │       │ id (PK)      │
│ email        │◄──┐   │ title        │──────►│ name         │
│ password_hash│   │   │ description  │       │ created_at   │
│ display_name │   │   │ category_id  │       └──────────────┘
│ role (ENUM)  │   │   │ difficulty   │
│ xp_total     │   │   │ status       │       ┌──────────────┐
│ level        │   │   │ is_premium   │       │   modules    │
│ streak_count │   │   │ instructor_id│───┐   ├──────────────┤
│ longest_streak│  │   │ deleted      │   │   │ id (PK)      │
│ last_activity│   │   │ created_at   │   │   │ course_id(FK)│
│ account_status│  │   │ updated_at   │   │   │ title        │
│ failed_logins│   │   └──────────────┘   │   │ order_index  │
│ locked_until │   │          ▲            │   │ created_at   │
│ created_at   │   │          │            │   └──────┬───────┘
└──────┬───────┘   │          │            │          │
       │           │   ┌──────┴───────┐    │          ▼
       │           │   │ enrollments  │    │   ┌──────────────┐
       │           │   ├──────────────┤    │   │   lessons    │
       │           │   │ id (PK)      │    │   ├──────────────┤
       │           └───│ student_id   │    │   │ id (PK)      │
       │               │ course_id    │    │   │ module_id(FK)│
       │               │ enrolled_at  │    │   │ title        │
       │               │ completed    │    │   │ text_content │
       │               │ completed_at │    │   │ video_url    │
       │               │ completion_%│    │   │ video_id     │
       │               └──────────────┘    │   │ is_premium   │
       │                                   │   │ order_index  │
       │                                   │   │ created_at   │
       ▼                                   │   └──────┬───────┘
┌──────────────┐                           │          │
│refresh_tokens│                           │     ┌────┴────┐
├──────────────┤                           │     ▼         ▼
│ id (PK)      │                           │ ┌────────┐ ┌──────────┐
│ user_id (FK) │                           │ │quizzes │ │challenges│
│ token_hash   │                           │ ├────────┤ ├──────────┤
│ expires_at   │                           │ │id (PK) │ │id (PK)   │
│ revoked      │                           │ │lesson_ │ │lesson_   │
│ created_at   │                           │ │  id(FK)│ │  id(FK)  │
└──────────────┘                           │ │title   │ │title     │
                                           │ │pass_%  │ │desc      │
                                           │ └────────┘ │starter   │
                                           │            │language  │
                                           │            │timeout_s │
                                           │            └──────────┘
```

### Database Tables

#### Core User & Auth Tables

**users**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK, DEFAULT gen_random_uuid() | Primary key |
| email | VARCHAR(255) | NOT NULL, UNIQUE | Login email (case-insensitive unique index) |
| password_hash | VARCHAR(255) | NOT NULL | BCrypt-hashed password |
| display_name | VARCHAR(50) | NOT NULL | User display name |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'STUDENT' | Enum: STUDENT, INSTRUCTOR, ADMIN |
| xp_total | INTEGER | NOT NULL, DEFAULT 0 | Cumulative XP earned |
| level | INTEGER | NOT NULL, DEFAULT 1 | Current level |
| streak_count | INTEGER | NOT NULL, DEFAULT 0 | Current streak days |
| longest_streak | INTEGER | NOT NULL, DEFAULT 0 | Best streak achieved |
| last_activity_date | DATE | NULL | Last UTC date activity was recorded |
| account_status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE, SUSPENDED, LOCKED |
| failed_login_attempts | INTEGER | NOT NULL, DEFAULT 0 | Consecutive failed logins |
| locked_until | TIMESTAMP | NULL | Account lockout expiry |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Registration timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**refresh_tokens**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| user_id | UUID | FK → users(id), NOT NULL | Token owner |
| token_hash | VARCHAR(255) | NOT NULL, UNIQUE | SHA-256 hash of the refresh token |
| expires_at | TIMESTAMP | NOT NULL | Token expiry (7 days from creation) |
| revoked | BOOLEAN | NOT NULL, DEFAULT FALSE | Whether token has been revoked |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Issuance timestamp |

#### Course Content Tables

**categories**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Category name (case-insensitive index) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |

**courses**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| title | VARCHAR(150) | NOT NULL | Course title |
| description | TEXT | NOT NULL | Course description |
| category_id | UUID | FK → categories(id), NOT NULL | Assigned category |
| difficulty | VARCHAR(20) | NOT NULL | BEGINNER, INTERMEDIATE, ADVANCED |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'DRAFT' | DRAFT, PUBLISHED |
| is_premium | BOOLEAN | NOT NULL, DEFAULT FALSE | Requires premium access |
| instructor_id | UUID | FK → users(id), NOT NULL | Course owner |
| deleted | BOOLEAN | NOT NULL, DEFAULT FALSE | Soft-delete flag |
| average_rating | DECIMAL(2,1) | NULL | Cached average of approved reviews |
| enrollment_count | INTEGER | NOT NULL, DEFAULT 0 | Cached enrollment count |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last update timestamp |

**modules**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| course_id | UUID | FK → courses(id), NOT NULL | Parent course |
| title | VARCHAR(150) | NOT NULL | Module title |
| order_index | INTEGER | NOT NULL | Position in course (1-based) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |
| | | UNIQUE(course_id, order_index) | Prevents duplicate ordering |

**lessons**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| module_id | UUID | FK → modules(id), NOT NULL | Parent module |
| title | VARCHAR(200) | NOT NULL | Lesson title |
| text_content | TEXT | NULL | Lesson text body |
| video_url | VARCHAR(500) | NULL | Original YouTube URL |
| video_id | VARCHAR(20) | NULL | Extracted YouTube video ID |
| is_premium | BOOLEAN | NOT NULL, DEFAULT FALSE | Premium-only access |
| order_index | INTEGER | NOT NULL | Position in module (1-based) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |
| | | UNIQUE(module_id, order_index) | Prevents duplicate ordering |

#### Assessment Tables

**quizzes**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| lesson_id | UUID | FK → lessons(id), NOT NULL, UNIQUE | One quiz per lesson |
| title | VARCHAR(200) | NOT NULL | Quiz title |
| passing_score | INTEGER | NOT NULL | Minimum percentage to pass (1-100) |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |

**quiz_questions**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| quiz_id | UUID | FK → quizzes(id), NOT NULL | Parent quiz |
| question_text | VARCHAR(2000) | NOT NULL | Question text |
| order_index | INTEGER | NOT NULL | Position in quiz |

**quiz_answer_options**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| question_id | UUID | FK → quiz_questions(id), NOT NULL | Parent question |
| option_text | VARCHAR(500) | NOT NULL | Answer text |
| is_correct | BOOLEAN | NOT NULL, DEFAULT FALSE | Whether this is the correct answer |
| order_index | INTEGER | NOT NULL | Display order |

**coding_challenges**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| lesson_id | UUID | FK → lessons(id), NOT NULL, UNIQUE | One challenge per lesson |
| title | VARCHAR(200) | NOT NULL | Challenge title |
| description | TEXT | NOT NULL | Problem description |
| starter_code | TEXT | NOT NULL | Template code |
| language | VARCHAR(50) | NOT NULL | Programming language |
| timeout_seconds | INTEGER | NOT NULL, DEFAULT 30 | Execution timeout |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |

**challenge_test_cases**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| challenge_id | UUID | FK → coding_challenges(id), NOT NULL | Parent challenge |
| input | TEXT | NOT NULL | Test input |
| expected_output | TEXT | NOT NULL | Expected result |
| is_hidden | BOOLEAN | NOT NULL, DEFAULT FALSE | Hidden from student |
| order_index | INTEGER | NOT NULL | Execution order |

#### Progress & Enrollment Tables

**enrollments**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Enrolled student |
| course_id | UUID | FK → courses(id), NOT NULL | Enrolled course |
| enrolled_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Enrollment date |
| completed | BOOLEAN | NOT NULL, DEFAULT FALSE | Course completed |
| completed_at | TIMESTAMP | NULL | Completion timestamp |
| completion_percentage | INTEGER | NOT NULL, DEFAULT 0 | Cached percentage 0-100 |
| | | UNIQUE(student_id, course_id) | One enrollment per student-course |

**lesson_progress**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Student |
| lesson_id | UUID | FK → lessons(id), NOT NULL | Completed lesson |
| completed_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Completion time |
| | | UNIQUE(student_id, lesson_id) | Prevents duplicates |

**quiz_attempts**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Student |
| quiz_id | UUID | FK → quizzes(id), NOT NULL | Attempted quiz |
| score | INTEGER | NOT NULL | Percentage score achieved |
| passed | BOOLEAN | NOT NULL | Whether passing threshold met |
| submitted_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Submission time |

**quiz_best_scores**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Student |
| quiz_id | UUID | FK → quizzes(id), NOT NULL | Quiz |
| best_score | INTEGER | NOT NULL | Highest score achieved |
| passed | BOOLEAN | NOT NULL | Whether ever passed |
| first_passed_at | TIMESTAMP | NULL | When first passed |
| | | UNIQUE(student_id, quiz_id) | One record per student-quiz |

**challenge_submissions**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Student |
| challenge_id | UUID | FK → coding_challenges(id), NOT NULL | Challenge |
| code | TEXT | NOT NULL | Submitted code |
| passed | BOOLEAN | NOT NULL | All tests passed |
| submitted_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Submission time |

**challenge_completions**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Student |
| challenge_id | UUID | FK → coding_challenges(id), NOT NULL | Challenge |
| completed_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | First completion |
| | | UNIQUE(student_id, challenge_id) | One completion record |

#### Gamification Tables

**xp_transactions**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Recipient |
| amount | INTEGER | NOT NULL | XP amount awarded |
| source_type | VARCHAR(50) | NOT NULL | LESSON, QUIZ, CHALLENGE, QUEST |
| source_id | UUID | NOT NULL | ID of the source entity |
| awarded_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Award timestamp |
| | | UNIQUE(student_id, source_type, source_id) | Prevents duplicate awards |

**level_thresholds**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| level | INTEGER | PK | Level number |
| xp_required | INTEGER | NOT NULL, UNIQUE | XP needed to reach this level |

**badges**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| name | VARCHAR(100) | NOT NULL, UNIQUE | Badge name |
| description | VARCHAR(500) | NOT NULL | Badge description |
| icon_ref | VARCHAR(255) | NOT NULL | Icon file reference |
| criteria_type | VARCHAR(50) | NOT NULL | STREAK_MILESTONE, COURSES_COMPLETED, XP_THRESHOLD |
| criteria_value | INTEGER | NOT NULL | Threshold value for criteria |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |

**user_badges**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Badge recipient |
| badge_id | UUID | FK → badges(id), NOT NULL | Awarded badge |
| awarded_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Award timestamp |
| | | UNIQUE(student_id, badge_id) | One award per badge per user |

**quests**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| title | VARCHAR(100) | NOT NULL | Quest title |
| description | VARCHAR(500) | NOT NULL | Quest description |
| xp_reward | INTEGER | NOT NULL | XP awarded on completion |
| start_date | DATE | NOT NULL | Quest availability start |
| end_date | DATE | NOT NULL | Quest expiry date |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |

**quest_objectives**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| quest_id | UUID | FK → quests(id), NOT NULL | Parent quest |
| objective_type | VARCHAR(50) | NOT NULL | LESSON_COMPLETION, QUIZ_PASS, CHALLENGE_COMPLETION, XP_EARNED |
| target_count | INTEGER | NOT NULL | Number required |
| description | VARCHAR(200) | NOT NULL | Human-readable description |
| order_index | INTEGER | NOT NULL | Display order |

**student_quest_progress**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Participant |
| quest_id | UUID | FK → quests(id), NOT NULL | Quest |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'ACTIVE' | ACTIVE, COMPLETED, EXPIRED |
| completed_at | TIMESTAMP | NULL | Completion timestamp |
| | | UNIQUE(student_id, quest_id) | One progress per student-quest |

**student_objective_progress**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Participant |
| objective_id | UUID | FK → quest_objectives(id), NOT NULL | Objective |
| current_count | INTEGER | NOT NULL, DEFAULT 0 | Progress toward target |
| completed | BOOLEAN | NOT NULL, DEFAULT FALSE | Objective met |
| | | UNIQUE(student_id, objective_id) | One progress per student-objective |

#### Communication & Review Tables

**notifications**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| user_id | UUID | FK → users(id), NOT NULL | Recipient |
| type | VARCHAR(50) | NOT NULL | BADGE_EARNED, CERTIFICATE_ISSUED, QUEST_AVAILABLE, ANNOUNCEMENT |
| title | VARCHAR(200) | NOT NULL | Notification title |
| reference_id | UUID | NULL | Related entity ID |
| is_read | BOOLEAN | NOT NULL, DEFAULT FALSE | Read status |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |

**certificates**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key (also serves as verification ID) |
| student_id | UUID | FK → users(id), NOT NULL | Certificate recipient |
| course_id | UUID | FK → courses(id), NOT NULL | Completed course |
| student_name | VARCHAR(50) | NOT NULL | Name at time of issuance |
| course_title | VARCHAR(150) | NOT NULL | Title at time of issuance |
| issued_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Issuance date |
| | | UNIQUE(student_id, course_id) | One cert per student-course |

**course_reviews**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| student_id | UUID | FK → users(id), NOT NULL | Reviewer |
| course_id | UUID | FK → courses(id), NOT NULL | Reviewed course |
| rating | INTEGER | NOT NULL | Rating 1-5 |
| comment | VARCHAR(1000) | NULL | Optional text review |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PENDING' | PENDING, APPROVED, REJECTED |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Submission date |
| | | UNIQUE(student_id, course_id) | One review per student-course |

#### Learning Path Tables

**learning_paths**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| name | VARCHAR(150) | NOT NULL | Learning path name |
| description | TEXT | NULL | Optional description |
| created_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Creation timestamp |

**learning_path_courses**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| learning_path_id | UUID | FK → learning_paths(id), NOT NULL | Parent path |
| course_id | UUID | FK → courses(id), NOT NULL | Included course |
| order_index | INTEGER | NOT NULL | Position in path |
| | | UNIQUE(learning_path_id, course_id) | No duplicate courses |
| | | UNIQUE(learning_path_id, order_index) | Unique ordering |

#### Analytics Tables

**course_analytics**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Primary key |
| course_id | UUID | FK → courses(id), NOT NULL, UNIQUE | Course reference |
| enrollment_count | INTEGER | NOT NULL, DEFAULT 0 | Total enrollments |
| avg_completion_pct | DECIMAL(5,2) | NOT NULL, DEFAULT 0 | Average completion % |
| avg_quiz_pass_rate | DECIMAL(5,2) | NOT NULL, DEFAULT 0 | Average quiz pass rate |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last aggregate refresh |

**platform_analytics**
| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| id | UUID | PK | Singleton row |
| total_users | INTEGER | NOT NULL, DEFAULT 0 | Registered user count |
| total_courses | INTEGER | NOT NULL, DEFAULT 0 | Published course count |
| total_certificates | INTEGER | NOT NULL, DEFAULT 0 | Certificates issued |
| avg_completion_pct | DECIMAL(5,2) | NOT NULL, DEFAULT 0 | Platform-wide average |
| updated_at | TIMESTAMP | NOT NULL, DEFAULT NOW() | Last refresh |

## Components and Interfaces

### API Endpoints

### Authentication & User Management

| Method | Path | Auth | Role | Description | Req |
|--------|------|------|------|-------------|-----|
| POST | /api/v1/auth/register | No | - | Register new user | R1 |
| POST | /api/v1/auth/login | No | - | Login, issue tokens | R2 |
| POST | /api/v1/auth/refresh | Cookie | - | Refresh access token | R2 |
| POST | /api/v1/auth/logout | Yes | Any | Invalidate refresh token | R2 |
| GET | /api/v1/users/me | Yes | Any | Get current user profile | R1 |
| GET | /api/v1/users | Yes | ADMIN | List all users (paginated) | R19 |
| PUT | /api/v1/users/{id}/role | Yes | ADMIN | Change user role | R3, R19 |

### Course Management

| Method | Path | Auth | Role | Description | Req |
|--------|------|------|------|-------------|-----|
| POST | /api/v1/courses | Yes | INSTRUCTOR | Create course (DRAFT) | R4 |
| GET | /api/v1/courses | No | - | List published courses (paginated, filterable) | R21 |
| GET | /api/v1/courses/{id} | No | - | Get course details | R4 |
| PUT | /api/v1/courses/{id} | Yes | INSTRUCTOR | Update own course | R4 |
| PUT | /api/v1/courses/{id}/publish | Yes | INSTRUCTOR | Publish course | R4 |
| DELETE | /api/v1/courses/{id} | Yes | ADMIN | Soft-delete course | R4 |
| GET | /api/v1/courses/search | No | - | Search/filter courses | R21 |
| GET | /api/v1/instructor/courses | Yes | INSTRUCTOR | List instructor's own courses | R4 |

### Categories & Learning Paths

| Method | Path | Auth | Role | Description | Req |
|--------|------|------|------|-------------|-----|
| POST | /api/v1/categories | Yes | ADMIN | Create category | R5 |
| GET | /api/v1/categories | No | - | List all categories | R5 |
| POST | /api/v1/learning-paths | Yes | ADMIN | Create learning path | R5 |
| GET | /api/v1/learning-paths | No | - | List learning paths (paginated) | R5 |
| GET | /api/v1/learning-paths/{id} | No | - | Get learning path with courses | R5 |
| PUT | /api/v1/learning-paths/{id} | Yes | ADMIN | Update learning path order | R5 |

### Modules & Lessons

| Method | Path | Auth | Role | Description | Req |
|--------|------|------|------|-------------|-----|
| POST | /api/v1/courses/{courseId}/modules | Yes | INSTRUCTOR | Add module | R7 |
| GET | /api/v1/courses/{courseId}/modules | Yes | STUDENT+ | List modules (ordered) | R7 |
| PUT | /api/v1/modules/{id} | Yes | INSTRUCTOR | Update module | R7 |
| PUT | /api/v1/courses/{courseId}/modules/reorder | Yes | INSTRUCTOR | Reorder modules | R7 |
| DELETE | /api/v1/modules/{id} | Yes | INSTRUCTOR | Delete module | R7 |
| POST | /api/v1/modules/{moduleId}/lessons | Yes | INSTRUCTOR | Create lesson | R8 |
| GET | /api/v1/lessons/{id} | Yes | STUDENT+ | Get lesson content | R8 |
| PUT | /api/v1/lessons/{id} | Yes | INSTRUCTOR | Update lesson | R8 |
| DELETE | /api/v1/lessons/{id} | Yes | INSTRUCTOR | Delete lesson | R8 |

### Enrollment & Progress

| Method | Path | Auth | Role | Description | Req |
|--------|------|------|------|-------------|-----|
| POST | /api/v1/courses/{courseId}/enroll | Yes | STUDENT | Enroll in course | R6 |
| GET | /api/v1/enrollments | Yes | STUDENT | List enrolled courses | R6 |
| POST | /api/v1/lessons/{lessonId}/complete | Yes | STUDENT | Mark lesson complete | R15 |
| GET | /api/v1/courses/{courseId}/progress | Yes | STUDENT | Get course progress | R15 |

### Assessments

| Method | Path | Auth | Role | Description | Req |
|--------|------|------|------|-------------|-----|
| POST | /api/v1/lessons/{lessonId}/quiz | Yes | INSTRUCTOR | Create quiz | R9 |
| GET | /api/v1/lessons/{lessonId}/quiz | Yes | STUDENT+ | Get quiz (without answers) | R9 |
| POST | /api/v1/quizzes/{quizId}/submit | Yes | STUDENT | Submit quiz answers | R9 |
| POST | /api/v1/lessons/{lessonId}/challenge | Yes | INSTRUCTOR | Create coding challenge | R10 |
| GET | /api/v1/lessons/{lessonId}/challenge | Yes | STUDENT+ | Get challenge details | R10 |
| POST | /api/v1/challenges/{challengeId}/submit | Yes | STUDENT | Submit code solution | R10 |

### Gamification

| Method | Path | Auth | Role | Description | Req |
|--------|------|------|------|-------------|-----|
| GET | /api/v1/gamification/xp | Yes | STUDENT | Get XP and level info | R11 |
| GET | /api/v1/gamification/streak | Yes | STUDENT | Get streak info | R12 |
| GET | /api/v1/gamification/badges | Yes | STUDENT | List earned badges | R13 |
| GET | /api/v1/quests | Yes | STUDENT | List active quests | R14 |
| GET | /api/v1/quests/{id} | Yes | STUDENT | Get quest with progress | R14 |
| POST | /api/v1/quests | Yes | ADMIN | Create quest | R14 |

### Dashboards & Analytics

| Method | Path | Auth | Role | Description | Req |
|--------|------|------|------|-------------|-----|
| GET | /api/v1/dashboard/student | Yes | STUDENT | Student dashboard aggregate | R20 |
| GET | /api/v1/dashboard/instructor/courses/{id}/analytics | Yes | INSTRUCTOR | Course analytics | R18 |
| GET | /api/v1/dashboard/instructor/courses/{id}/students | Yes | INSTRUCTOR | Student activity list | R18 |
| GET | /api/v1/dashboard/admin/analytics | Yes | ADMIN | Platform analytics | R19 |
| GET | /api/v1/dashboard/admin/courses | Yes | ADMIN | All courses list | R19 |

### Notifications, Certificates & Reviews

| Method | Path | Auth | Role | Description | Req |
|--------|------|------|------|-------------|-----|
| GET | /api/v1/notifications | Yes | Any | List notifications (paginated) | R17 |
| GET | /api/v1/notifications/unread-count | Yes | Any | Get unread count | R17 |
| PUT | /api/v1/notifications/{id}/read | Yes | Any | Mark as read | R17 |
| POST | /api/v1/notifications/announcements | Yes | ADMIN | Create announcement | R17 |
| GET | /api/v1/certificates | Yes | STUDENT | List own certificates | R16 |
| GET | /api/v1/certificates/{id}/verify | No | - | Public verification | R16 |
| POST | /api/v1/courses/{courseId}/reviews | Yes | STUDENT | Submit review | R22 |
| GET | /api/v1/courses/{courseId}/reviews | No | - | List approved reviews | R22 |
| PUT | /api/v1/reviews/{id}/moderate | Yes | ADMIN | Moderate review | R22 |

## Key Design Decisions

### 1. Authentication Strategy (R2, R26)

- **Access Token**: Short-lived (15 min) JWT stored in HTTP-only secure cookie with SameSite=Strict. Contains user ID and role claim. Not stored server-side.
- **Refresh Token**: Long-lived (7 days) JWT. Server stores SHA-256 hash in `refresh_tokens` table. On refresh, old token is revoked and new token pair issued (rotation pattern).
- **Token Format**: `{ sub: userId, role: "STUDENT", iat: timestamp, exp: timestamp }`
- **Logout**: Revokes refresh token in DB, clears both cookies.
- **Role changes / password changes**: Revoke all refresh tokens for the user via `UPDATE refresh_tokens SET revoked = true WHERE user_id = ?`.

### 2. Authorization Architecture (R3)

- Spring Security filter chain validates JWT from cookie on every request.
- Public endpoints (register, login, search, certificate verify) bypass authentication.
- `@PreAuthorize` annotations on service methods enforce role checks.
- Ownership checks (instructor owns course) done in service layer by comparing `course.instructorId` to authenticated user ID.

### 3. Package-by-Feature Structure (R24)

Each feature package follows the same internal structure:
```
com.dmpacademy.{feature}/
├── {Feature}.java              // JPA Entity
├── {Feature}Repository.java    // Spring Data JPA interface
├── {Feature}Service.java       // Business logic
├── {Feature}Controller.java    // REST controller
├── {Feature}Mapper.java        // Entity ↔ DTO mapping
└── dto/
    ├── {Feature}Request.java   // Immutable record for input
    └── {Feature}Response.java  // Immutable record for output
```

### 4. Domain Event Architecture (R27)

```java
// Base event class
public abstract class DomainEvent {
    private final UUID userId;
    private final UUID entityId;
    private final String eventType;
    private final Instant timestamp;
}

// Example events
public class XpAwardedEvent extends DomainEvent { int amount; String sourceType; }
public class CourseCompletedEvent extends DomainEvent { UUID courseId; }
public class BadgeAwardedEvent extends DomainEvent { UUID badgeId; }
public class CertificateIssuedEvent extends DomainEvent { UUID certificateId; }
```

- Events published via `ApplicationEventPublisher.publishEvent()` within service methods.
- Listeners annotated with `@TransactionalEventListener(phase = AFTER_COMMIT)` to ensure the source transaction committed successfully.
- Listeners annotated with `@Async` for non-blocking processing.
- Failed listeners log errors but do not affect source operation.

### 5. XP Award Service — Centralized Gamification (R11)

```java
@Service
public class XpAwardService {
    // Single entry point for all XP awards
    public void awardXp(UUID studentId, int amount, String sourceType, UUID sourceId) {
        // 1. Check for duplicate (UNIQUE constraint on student+source_type+source_id)
        // 2. Insert xp_transaction
        // 3. Update users.xp_total atomically
        // 4. Check level_thresholds, update users.level if needed
        // 5. Publish XpAwardedEvent
        // 6. Update streak
    }
}
```

This centralizes XP logic so lesson, quiz, and challenge modules simply call `xpAwardService.awardXp(...)` without duplicating level/streak logic.

### 6. Streak Evaluation (R12)

- Streak is evaluated on each learning activity completion.
- `users.last_activity_date` stores the last UTC date an activity was counted.
- On activity completion:
  - If `last_activity_date == today(UTC)` → no change (already counted today)
  - If `last_activity_date == yesterday(UTC)` → increment streak, update last_activity_date
  - If `last_activity_date < yesterday(UTC)` or NULL → reset streak to 1, update last_activity_date
- No background scheduler needed — streak is lazily evaluated on next activity.

### 7. Code Execution Sandbox (R10)

- Code execution uses a sandboxed Docker container per submission.
- Process: Student code → create temp file → execute in isolated container with resource limits (30s timeout, memory cap) → capture stdout/stderr → compare with expected output.
- Initial supported languages: Java, JavaScript, Python.
- Execution is synchronous from the student's perspective (blocking HTTP request with timeout).

### 8. Analytics Strategy (R18, R19)

- Analytics are pre-aggregated into denormalized tables (`course_analytics`, `platform_analytics`).
- Domain event listeners update these aggregates asynchronously after commits.
- Trade-off: eventual consistency (analytics may lag by seconds) in exchange for fast reads on dashboard endpoints.

### 9. Frontend State Architecture (R25)

```
TanStack Query (server state)
├── Course data, enrollments, progress
├── Quiz/challenge data and submissions
├── Notifications, badges, certificates
└── Dashboard aggregates

Zustand (client state)
├── Auth session (current user, role)
└── UI state (mobile menu open, theme)
```

- All API calls go through a typed `apiClient` wrapper that handles cookie-based auth automatically.
- TanStack Query handles caching, refetching, loading/error states.
- Mutations invalidate relevant queries on success.

### 10. Pagination Contract (R23)

All paginated endpoints return:
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8
}
```

Query params: `?page=0&size=20&sort=createdAt,desc`

## Backend Package Structure

```
backend/src/main/java/com/dmpacademy/
├── DmpAcademyApplication.java
├── config/
│   ├── SecurityConfig.java           // Spring Security filter chain, CORS
│   ├── JwtConfig.java                // JWT secret, expiry configuration
│   ├── OpenApiConfig.java            // Swagger/OpenAPI setup
│   └── AsyncConfig.java              // @Async thread pool configuration
├── common/
│   ├── BaseEntity.java               // Mapped superclass with id, createdAt
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java   // @ControllerAdvice
│   │   ├── ResourceNotFoundException.java
│   │   ├── DuplicateResourceException.java
│   │   ├── AccessDeniedException.java
│   │   └── ValidationException.java
│   ├── dto/
│   │   ├── ErrorResponse.java        // Standard error DTO
│   │   └── PageResponse.java         // Generic paginated response wrapper
│   └── util/
│       ├── YouTubeUrlParser.java     // Extract video ID from URL
│       └── HtmlSanitizer.java        // XSS prevention
├── auth/
│   ├── AuthController.java
│   ├── AuthService.java
│   ├── JwtService.java               // Token generation/validation
│   ├── JwtAuthenticationFilter.java  // OncePerRequestFilter
│   ├── RefreshToken.java             // Entity
│   ├── RefreshTokenRepository.java
│   ├── LoginAttemptService.java      // Rate limiting
│   └── dto/
│       ├── RegisterRequest.java
│       ├── LoginRequest.java
│       └── AuthResponse.java
├── user/
│   ├── User.java
│   ├── UserRepository.java
│   ├── UserService.java
│   ├── UserController.java
│   ├── UserMapper.java
│   ├── Role.java                     // Enum: STUDENT, INSTRUCTOR, ADMIN
│   ├── AccountStatus.java            // Enum: ACTIVE, SUSPENDED, LOCKED
│   └── dto/
│       ├── UserResponse.java
│       └── RoleUpdateRequest.java
├── course/
│   ├── Course.java
│   ├── CourseRepository.java
│   ├── CourseService.java
│   ├── CourseController.java
│   ├── CourseMapper.java
│   ├── CourseStatus.java             // Enum: DRAFT, PUBLISHED
│   ├── Difficulty.java               // Enum: BEGINNER, INTERMEDIATE, ADVANCED
│   ├── Category.java
│   ├── CategoryRepository.java
│   ├── CategoryController.java
│   └── dto/
│       ├── CourseCreateRequest.java
│       ├── CourseUpdateRequest.java
│       ├── CourseResponse.java
│       ├── CourseSearchRequest.java
│       └── CategoryRequest.java
├── module/
│   ├── Module.java
│   ├── ModuleRepository.java
│   ├── ModuleService.java
│   ├── ModuleController.java
│   ├── ModuleMapper.java
│   └── dto/
│       ├── ModuleRequest.java
│       ├── ModuleResponse.java
│       └── ReorderRequest.java
├── lesson/
│   ├── Lesson.java
│   ├── LessonRepository.java
│   ├── LessonService.java
│   ├── LessonController.java
│   ├── LessonMapper.java
│   └── dto/
│       ├── LessonCreateRequest.java
│       ├── LessonResponse.java
│       └── LessonStudentResponse.java
├── quiz/
│   ├── Quiz.java
│   ├── QuizQuestion.java
│   ├── QuizAnswerOption.java
│   ├── QuizAttempt.java
│   ├── QuizBestScore.java
│   ├── QuizRepository.java
│   ├── QuizService.java
│   ├── QuizController.java
│   ├── QuizMapper.java
│   └── dto/
│       ├── QuizCreateRequest.java
│       ├── QuizResponse.java
│       ├── QuizSubmissionRequest.java
│       └── QuizResultResponse.java
├── challenge/
│   ├── CodingChallenge.java
│   ├── ChallengeTestCase.java
│   ├── ChallengeSubmission.java
│   ├── ChallengeCompletion.java
│   ├── ChallengeRepository.java
│   ├── ChallengeService.java
│   ├── ChallengeController.java
│   ├── CodeExecutionService.java     // Sandbox execution
│   ├── ChallengeMapper.java
│   └── dto/
│       ├── ChallengeCreateRequest.java
│       ├── ChallengeResponse.java
│       ├── CodeSubmissionRequest.java
│       └── ExecutionResultResponse.java
├── gamification/
│   ├── XpAwardService.java           // Central XP logic
│   ├── StreakService.java
│   ├── LevelThreshold.java
│   ├── LevelThresholdRepository.java
│   ├── XpTransaction.java
│   ├── XpTransactionRepository.java
│   ├── GamificationController.java
│   └── dto/
│       ├── XpResponse.java
│       └── StreakResponse.java
├── badge/
│   ├── Badge.java
│   ├── UserBadge.java
│   ├── BadgeRepository.java
│   ├── UserBadgeRepository.java
│   ├── BadgeService.java
│   ├── BadgeEventListener.java       // @TransactionalEventListener
│   ├── BadgeController.java
│   └── dto/
│       └── BadgeResponse.java
├── quest/
│   ├── Quest.java
│   ├── QuestObjective.java
│   ├── StudentQuestProgress.java
│   ├── StudentObjectiveProgress.java
│   ├── QuestRepository.java
│   ├── QuestService.java
│   ├── QuestController.java
│   ├── QuestEventListener.java
│   └── dto/
│       ├── QuestCreateRequest.java
│       ├── QuestResponse.java
│       └── QuestProgressResponse.java
├── progress/
│   ├── LessonProgress.java
│   ├── LessonProgressRepository.java
│   ├── Enrollment.java
│   ├── EnrollmentRepository.java
│   ├── ProgressService.java
│   ├── EnrollmentService.java
│   ├── ProgressController.java
│   ├── EnrollmentController.java
│   └── dto/
│       ├── EnrollmentResponse.java
│       ├── ProgressResponse.java
│       └── DashboardResponse.java
├── certificate/
│   ├── Certificate.java
│   ├── CertificateRepository.java
│   ├── CertificateService.java
│   ├── CertificateEventListener.java // Listens for CourseCompletedEvent
│   ├── CertificateController.java
│   └── dto/
│       ├── CertificateResponse.java
│       └── CertificateVerifyResponse.java
├── notification/
│   ├── Notification.java
│   ├── NotificationRepository.java
│   ├── NotificationService.java
│   ├── NotificationEventListener.java
│   ├── NotificationController.java
│   └── dto/
│       ├── NotificationResponse.java
│       └── AnnouncementRequest.java
├── review/
│   ├── CourseReview.java
│   ├── CourseReviewRepository.java
│   ├── ReviewService.java
│   ├── ReviewController.java
│   └── dto/
│       ├── ReviewCreateRequest.java
│       ├── ReviewResponse.java
│       └── ModerationRequest.java
├── learningpath/
│   ├── LearningPath.java
│   ├── LearningPathCourse.java
│   ├── LearningPathRepository.java
│   ├── LearningPathService.java
│   ├── LearningPathController.java
│   └── dto/
│       ├── LearningPathRequest.java
│       └── LearningPathResponse.java
├── analytics/
│   ├── CourseAnalytics.java
│   ├── PlatformAnalytics.java
│   ├── CourseAnalyticsRepository.java
│   ├── PlatformAnalyticsRepository.java
│   ├── AnalyticsService.java
│   ├── AnalyticsEventListener.java
│   ├── AnalyticsController.java
│   └── dto/
│       ├── CourseAnalyticsResponse.java
│       ├── PlatformAnalyticsResponse.java
│       └── StudentActivityResponse.java
└── event/
    ├── DomainEvent.java              // Abstract base
    ├── XpAwardedEvent.java
    ├── CourseCompletedEvent.java
    ├── QuizPassedEvent.java
    ├── ChallengeCompletedEvent.java
    ├── LessonCompletedEvent.java
    ├── BadgeAwardedEvent.java
    ├── CertificateIssuedEvent.java
    ├── StreakMilestoneEvent.java
    └── CourseEnrolledEvent.java
```

## Frontend Architecture

### Directory Structure

```
frontend/src/
├── app/
│   ├── globals.css                    // Tailwind theme tokens
│   ├── layout.tsx                     // Root layout, font loading, providers
│   ├── (public)/
│   │   ├── page.tsx                   // Landing page
│   │   ├── login/page.tsx
│   │   ├── register/page.tsx
│   │   ├── courses/page.tsx           // Course catalog (search/filter)
│   │   ├── courses/[id]/page.tsx      // Public course detail
│   │   ├── learning-paths/page.tsx
│   │   └── certificates/[id]/verify/page.tsx
│   ├── (student)/
│   │   ├── layout.tsx                 // Auth guard (STUDENT role)
│   │   ├── dashboard/page.tsx
│   │   ├── courses/[id]/page.tsx      // Enrolled course view
│   │   ├── courses/[id]/lessons/[lessonId]/page.tsx
│   │   ├── courses/[id]/progress/page.tsx
│   │   ├── quests/page.tsx
│   │   ├── badges/page.tsx
│   │   ├── certificates/page.tsx
│   │   └── notifications/page.tsx
│   ├── (instructor)/
│   │   ├── layout.tsx                 // Auth guard (INSTRUCTOR role)
│   │   ├── dashboard/page.tsx
│   │   ├── courses/page.tsx           // Manage own courses
│   │   ├── courses/new/page.tsx
│   │   ├── courses/[id]/edit/page.tsx
│   │   ├── courses/[id]/modules/page.tsx
│   │   ├── courses/[id]/analytics/page.tsx
│   │   └── courses/[id]/students/page.tsx
│   └── (admin)/
│       ├── layout.tsx                 // Auth guard (ADMIN role)
│       ├── dashboard/page.tsx
│       ├── users/page.tsx
│       ├── courses/page.tsx
│       ├── categories/page.tsx
│       ├── learning-paths/page.tsx
│       ├── quests/page.tsx
│       ├── reviews/page.tsx
│       └── announcements/page.tsx
├── components/
│   ├── ui/                            // shadcn/ui primitives
│   │   ├── button.tsx
│   │   ├── input.tsx
│   │   ├── card.tsx
│   │   ├── dialog.tsx
│   │   ├── table.tsx
│   │   ├── badge.tsx
│   │   ├── progress.tsx
│   │   ├── tabs.tsx
│   │   ├── select.tsx
│   │   ├── textarea.tsx
│   │   ├── toast.tsx
│   │   └── pagination.tsx
│   └── shared/
│       ├── navbar.tsx
│       ├── sidebar.tsx
│       ├── footer.tsx
│       ├── loading-spinner.tsx
│       ├── error-boundary.tsx
│       ├── page-header.tsx
│       ├── data-table.tsx
│       ├── youtube-player.tsx         // YouTube embed component
│       ├── code-editor.tsx            // Monaco editor wrapper
│       └── xp-progress-bar.tsx
├── features/
│   ├── auth/
│   │   ├── components/
│   │   │   ├── login-form.tsx
│   │   │   └── register-form.tsx
│   │   ├── hooks/
│   │   │   └── use-auth.ts
│   │   ├── api.ts
│   │   └── types.ts
│   ├── course/
│   │   ├── components/
│   │   │   ├── course-card.tsx
│   │   │   ├── course-grid.tsx
│   │   │   ├── course-filters.tsx
│   │   │   ├── course-form.tsx
│   │   │   └── module-list.tsx
│   │   ├── hooks/
│   │   │   ├── use-courses.ts
│   │   │   └── use-enrollment.ts
│   │   ├── api.ts
│   │   └── types.ts
│   ├── lesson/
│   │   ├── components/
│   │   │   ├── lesson-viewer.tsx
│   │   │   ├── lesson-form.tsx
│   │   │   └── lesson-sidebar.tsx
│   │   ├── hooks/
│   │   │   └── use-lesson.ts
│   │   ├── api.ts
│   │   └── types.ts
│   ├── quiz/
│   │   ├── components/
│   │   │   ├── quiz-player.tsx
│   │   │   ├── quiz-results.tsx
│   │   │   └── quiz-form.tsx
│   │   ├── hooks/
│   │   │   └── use-quiz.ts
│   │   ├── api.ts
│   │   └── types.ts
│   ├── challenge/
│   │   ├── components/
│   │   │   ├── challenge-workspace.tsx
│   │   │   ├── test-results.tsx
│   │   │   └── challenge-form.tsx
│   │   ├── hooks/
│   │   │   └── use-challenge.ts
│   │   ├── api.ts
│   │   └── types.ts
│   ├── gamification/
│   │   ├── components/
│   │   │   ├── xp-card.tsx
│   │   │   ├── streak-card.tsx
│   │   │   ├── badge-grid.tsx
│   │   │   ├── level-indicator.tsx
│   │   │   └── quest-card.tsx
│   │   ├── hooks/
│   │   │   └── use-gamification.ts
│   │   ├── api.ts
│   │   └── types.ts
│   ├── notification/
│   │   ├── components/
│   │   │   ├── notification-bell.tsx
│   │   │   └── notification-list.tsx
│   │   ├── hooks/
│   │   │   └── use-notifications.ts
│   │   ├── api.ts
│   │   └── types.ts
│   └── analytics/
│       ├── components/
│       │   ├── stats-card.tsx
│       │   ├── enrollment-chart.tsx
│       │   └── completion-chart.tsx
│       ├── hooks/
│       │   └── use-analytics.ts
│       ├── api.ts
│       └── types.ts
├── lib/
│   ├── api-client.ts                  // Typed fetch wrapper
│   ├── auth-utils.ts                  // Token refresh logic
│   ├── utils.ts                       // cn(), formatDate, etc.
│   └── constants.ts                   // API base URL, page sizes
├── hooks/
│   ├── use-current-user.ts            // Global auth state
│   └── use-media-query.ts            // Responsive breakpoint hook
├── stores/
│   └── auth-store.ts                  // Zustand: user session, role
└── types/
    └── api.ts                         // Shared API response types
```

### Frontend Key Components

**API Client** (`lib/api-client.ts`):
- Wraps `fetch` with typed request/response generics
- Automatically includes credentials (cookies) via `credentials: 'include'`
- Handles 401 responses by attempting token refresh, then retrying
- Returns typed error objects on failure

**Auth Guard Layouts**:
- Route group layouts check user role from Zustand store
- Redirect unauthenticated users to `/login`
- Redirect wrong-role users to their appropriate dashboard

**YouTube Player** (`components/shared/youtube-player.tsx`):
- Server Component that renders YouTube iframe from video ID
- Responsive container with 16:9 aspect ratio
- Includes proper `title` attribute for accessibility

**Code Editor** (`components/shared/code-editor.tsx`):
- Client Component wrapping Monaco Editor
- Language-aware syntax highlighting
- Starter code pre-populated
- Submit button triggers API call

## Security Design

### JWT Cookie Configuration

```
Set-Cookie: access_token={jwt}; HttpOnly; Secure; SameSite=Strict; Path=/api; Max-Age=900
Set-Cookie: refresh_token={jwt}; HttpOnly; Secure; SameSite=Strict; Path=/api/v1/auth/refresh; Max-Age=604800
```

- `Path=/api` restricts access token to API routes
- `Path=/api/v1/auth/refresh` restricts refresh token to refresh endpoint only
- `SameSite=Strict` prevents CSRF from cross-origin requests

### Rate Limiting (Login)

- Track per-user failed attempts in `users.failed_login_attempts`
- On 5+ failures within 15 minutes: set `locked_until = now() + 30min`
- Return 429 with `Retry-After` header indicating lockout duration
- Successful login resets counter to 0

### Input Sanitization

- Use OWASP Java HTML Sanitizer for user-generated text fields
- Strip script tags, event handlers, and dangerous attributes
- Applied in service layer before persistence

## Flyway Migration Strategy

Migrations are numbered sequentially and never modified after application:

```
V1__create_users_and_auth.sql
V2__create_categories.sql
V3__create_courses.sql
V4__create_modules_and_lessons.sql
V5__create_enrollments_and_progress.sql
V6__create_quizzes.sql
V7__create_coding_challenges.sql
V8__create_gamification_tables.sql
V9__create_badges_and_quests.sql
V10__create_notifications_and_certificates.sql
V11__create_reviews.sql
V12__create_learning_paths.sql
V13__create_analytics_tables.sql
V14__seed_level_thresholds.sql
V15__seed_initial_badges.sql
```

## Testing Strategy

### Backend

- **Unit tests**: Service layer with mocked repositories (Mockito)
- **Integration tests**: Controller layer with Testcontainers (PostgreSQL 16)
- **Test structure**: Same package-by-feature layout under `src/test/java`
- **Coverage targets**: Service methods 80%+, critical paths (auth, XP) 90%+

### Frontend

- **Component tests**: React Testing Library for interactive components
- **Hook tests**: renderHook for TanStack Query hooks with MSW mock server
- **E2E**: Playwright for critical flows (register → enroll → complete lesson → XP)

## Performance Considerations

- **Database indexes**: On all FK columns, email (unique), plus composite indexes on (student_id, lesson_id) for progress lookups
- **Entity graphs**: `@EntityGraph` on Course fetches to include modules/lessons in one query
- **Batch fetching**: `@BatchSize(size = 20)` on collections to prevent N+1
- **Analytics caching**: Pre-aggregated in dedicated tables, not computed on read
- **Pagination**: All list endpoints paginated (max 100 per page)
- **Connection pooling**: HikariCP with appropriate pool size for concurrent users

## Error Handling

### Backend Error Response Contract

All errors return the standard `ErrorResponse` DTO:

```json
{
  "timestamp": "2026-07-16T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": [
    { "field": "email", "message": "must be a valid email address" },
    { "field": "password", "message": "must be between 8 and 128 characters" }
  ]
}
```

### Error Categories

| HTTP Status | Scenario | Handler |
|-------------|----------|---------|
| 400 | Validation failure (Bean Validation) | `@ExceptionHandler(MethodArgumentNotValidException)` |
| 401 | Missing/expired/invalid JWT | `JwtAuthenticationFilter` |
| 403 | Insufficient role / ownership check failed | `@ExceptionHandler(AccessDeniedException)` |
| 404 | Resource not found | `@ExceptionHandler(ResourceNotFoundException)` |
| 409 | Duplicate enrollment, duplicate review | `@ExceptionHandler(DuplicateResourceException)` |
| 429 | Account locked (rate limiting) | `LoginAttemptService` check |
| 500 | Unexpected server error | Catch-all handler, log stack trace, return generic message |

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Handles validation, not-found, access-denied, conflict, generic errors
    // Always returns ErrorResponse DTO
    // Logs 5xx errors at ERROR level
    // Logs 4xx errors at WARN level
}
```

### Frontend Error Handling

- TanStack Query `onError` callbacks display toast notifications for transient errors
- 401 responses trigger automatic token refresh; if refresh fails, redirect to login
- 403 responses show "Access Denied" page with link to appropriate dashboard
- Network failures show retry button within the component
- Form validation errors displayed inline adjacent to the relevant field

## Correctness Properties

### Property 1: XP Award Idempotency

**Validates: Requirements 11.1, 11.2, 11.3**

A student can receive XP for a given (source_type, source_id) pair exactly once. Enforced by UNIQUE constraint on `xp_transactions(student_id, source_type, source_id)`. Subsequent calls for the same activity are silently ignored without error.

### Property 2: Single Enrollment Per Course

**Validates: Requirements 6.5**

A student can be enrolled in a course at most once. Enforced by UNIQUE constraint on `enrollments(student_id, course_id)`. Duplicate enrollment attempts return 409 Conflict.

### Property 3: Module and Lesson Ordering Integrity

**Validates: Requirements 7.2, 8.4**

Within a course, no two modules share the same order_index. Within a module, no two lessons share the same order_index. Enforced by UNIQUE constraints on composite keys `(course_id, order_index)` and `(module_id, order_index)`. Reorder operations update indices within a transaction.

### Property 4: Role Exclusivity

**Validates: Requirements 3.1**

Each user has exactly one role at any time. The role is stored as a single-valued enum column, not a join table. There is no state where a user has zero roles or multiple roles simultaneously.

### Property 5: Certificate and Badge Uniqueness

**Validates: Requirements 16.3, 13.3**

At most one certificate per student-course pair (UNIQUE on `certificates(student_id, course_id)`). Each badge awarded at most once per student (UNIQUE on `user_badges(student_id, badge_id)`). Duplicate generation attempts are prevented at the database constraint level.

### Property 6: Refresh Token Rotation Safety

**Validates: Requirements 2.3, 2.7**

On token refresh, the old token is revoked and a new token pair is issued within the same database transaction. This ensures a compromised refresh token cannot be reused after legitimate refresh. If the old token is already revoked (replay attack), all tokens for that user are invalidated.

### Property 7: Event Publication After Commit

**Validates: Requirements 27.2, 27.5**

Domain events are published only after the originating transaction commits successfully, using `@TransactionalEventListener(phase = AFTER_COMMIT)`. Listeners never process events for rolled-back operations. Failed listeners log errors but do not affect the source operation or other listeners.

### Property 8: Atomic XP and Level Updates

**Validates: Requirements 11.4, 11.8**

XP updates use atomic SQL: `UPDATE users SET xp_total = xp_total + :amount WHERE id = :userId`. Level is recalculated within the same transaction. JPA `@Version` on User entity provides optimistic locking for concurrent modifications to prevent lost updates.

### Property 9: Streak Consistency

**Validates: Requirements 12.1, 12.2, 12.3**

Streak evaluation occurs within the same transaction as activity completion. The check `last_activity_date` comparison and update happen atomically, preventing race conditions where two concurrent completions could double-increment the streak.

### Property 10: Analytics Eventual Consistency

**Validates: Requirements 18.5, 27.4**

Analytics tables (`course_analytics`, `platform_analytics`) are updated asynchronously via domain event listeners. They may lag behind source-of-truth tables by seconds. All other data maintains strong consistency through PostgreSQL ACID transactions.
