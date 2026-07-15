# Requirements Document

## Introduction

DMP Full Stack Academy is a gamified, full-stack learning management platform (LMS) that combines structured courses, gamification mechanics (XP, levels, streaks, badges, quests), and practical assessment (quizzes, coding challenges, browser playground, project submissions). The platform follows a freemium monetization model where free lessons are accessible to all registered users, while premium content requires paid enrollment. The primary value proposition is learn-by-doing with immediate feedback rather than passive video consumption.

## Glossary

- **Platform**: The DMP Full Stack Academy web application comprising a Spring Boot backend and Next.js frontend
- **Student**: A registered user with the STUDENT role who can enroll in courses, consume lessons, submit assessments, and track progress
- **Instructor**: A registered user with the INSTRUCTOR role who creates and manages course content and views course analytics
- **Admin**: A registered user with the ADMIN role who has full platform management access
- **Course**: A structured learning unit containing ordered modules, with metadata including title, description, category, difficulty level, and premium flag
- **Module**: An ordered subdivision of a course containing one or more lessons
- **Lesson**: An individual learning unit within a module containing text content and/or YouTube video embedding
- **Quiz**: A multiple-choice assessment associated with a lesson or module providing immediate feedback
- **Coding_Challenge**: A practical coding exercise with browser-based code execution and automated validation
- **XP**: Experience points awarded for completing learning activities
- **Level**: A progression tier calculated from accumulated XP
- **Streak**: A count of consecutive days a student completes at least one learning activity
- **Badge**: An achievement award granted for meeting specific criteria
- **Quest**: A time-bound challenge with defined objectives and XP rewards
- **Certificate**: A digital document generated upon course completion
- **Enrollment**: The association between a student and a course granting access to course content
- **Access_Token**: A short-lived JWT (15 minutes) used for API authentication
- **Refresh_Token**: A long-lived JWT (7 days) used to obtain new access tokens
- **XpAwardService**: The centralized gamification service responsible for awarding XP across all modules
- **Learning_Path**: An admin-curated ordered sequence of courses forming a complete curriculum
- **Category**: A classification label for organizing courses by topic area
- **Notification**: An in-app message delivered to a user about platform events

## Requirements

### Requirement 1: User Registration

**User Story:** As a visitor, I want to register an account on the platform, so that I can access courses and track my learning progress.

#### Acceptance Criteria

1. WHEN a visitor submits a registration form with valid email, password, and display name, THE Platform SHALL create a new user account with the STUDENT role and return the created user profile including email, display name, role, XP, and level
2. WHEN a visitor submits a registration form with an email already associated with an existing account (case-insensitive comparison), THE Platform SHALL reject the registration and return an error message indicating the email is already registered
3. THE Platform SHALL validate that the password meets minimum security requirements (at least 8 characters, no more than 128 characters, containing at least one uppercase letter, one lowercase letter, and one numeric character)
4. THE Platform SHALL validate the email format using Jakarta Bean Validation constraints
5. THE Platform SHALL validate that the display name is between 2 and 50 characters in length
6. IF a registration request contains invalid or missing required fields, THEN THE Platform SHALL return a 400 response with field-level validation error messages
7. WHEN a user account is created successfully, THE Platform SHALL assign initial XP of zero and Level of 1 to the new student

### Requirement 2: User Authentication

**User Story:** As a registered user, I want to log in and maintain a secure session, so that I can access my account and protected resources.

#### Acceptance Criteria

1. WHEN a user submits valid credentials (email and password), THE Platform SHALL issue an Access_Token (15-minute expiry) and a Refresh_Token (7-day expiry) in HTTP-only secure cookies
2. WHEN a user submits invalid credentials, THE Platform SHALL return a 401 response without revealing whether the email or password was incorrect
3. WHEN an Access_Token expires and a valid Refresh_Token is presented to the refresh endpoint, THE Platform SHALL issue a new Access_Token and a new Refresh_Token (rotation), invalidating the previously issued Refresh_Token
4. WHEN a Refresh_Token expires or is invalid, THE Platform SHALL return a 401 response requiring the user to re-authenticate
5. WHEN a user requests logout, THE Platform SHALL invalidate the Refresh_Token and clear authentication cookies
6. THE Platform SHALL include the authenticated user identifier and role in the Access_Token JWT claims for authorization decisions
7. WHEN a user's password is changed or their role is updated, THE Platform SHALL invalidate all existing Refresh_Tokens for that user, requiring re-authentication on subsequent token refresh attempts

### Requirement 3: Role-Based Authorization

**User Story:** As a platform operator, I want to enforce role-based access control, so that users can only perform actions permitted by their assigned role.

#### Acceptance Criteria

1. THE Platform SHALL enforce that each user has exactly one role from the set: STUDENT, INSTRUCTOR, ADMIN
2. WHEN an unauthenticated request targets any endpoint other than registration, login, public course search, and certificate verification, THE Platform SHALL return a 401 response with the standard error DTO
3. WHEN an authenticated user requests a resource not permitted by their role, THE Platform SHALL return a 403 response, where permissions are defined as: STUDENT may access enrollment, progress, assessment submissions, and personal dashboard; INSTRUCTOR may access course/module/lesson/quiz/challenge management for their own courses and instructor analytics; ADMIN may access all platform management including user management, category, learning path, quest management, and platform analytics
4. THE Platform SHALL enforce authorization at the service layer using Spring Security @PreAuthorize annotations
5. WHEN an Admin changes a user role, THE Platform SHALL persist the role change within the same request transaction, and any Access_Token issued after the change SHALL contain the new role, while previously issued Access_Tokens SHALL remain valid with the old role until they expire
6. THE Platform SHALL restrict role change operations (both promotion and demotion) exclusively to users with the ADMIN role
7. IF an Admin attempts to change their own role, THEN THE Platform SHALL reject the request and return a 403 response to prevent accidental loss of administrative access

### Requirement 4: Course Management

**User Story:** As an Instructor, I want to create and manage courses, so that I can publish structured learning content for students.

#### Acceptance Criteria

1. WHEN an Instructor submits a valid course creation request, THE Platform SHALL create a new course with status DRAFT associated with that Instructor
2. THE Platform SHALL require course title (3 to 150 characters), description (10 to 5000 characters), a reference to an existing category, and difficulty level (BEGINNER, INTERMEDIATE, ADVANCED) for course creation
3. IF a course creation or update request contains invalid or missing required fields, THEN THE Platform SHALL return a 400 response with field-level validation error messages identifying each invalid field
4. WHEN an Instructor updates a course they own, THE Platform SHALL apply the same field validation rules as creation, persist the changes, and return the updated course DTO
5. WHEN an Instructor attempts to modify a course they do not own, THE Platform SHALL return a 403 response
6. WHEN an Instructor publishes a course that contains at least one module with at least one lesson, THE Platform SHALL change the course status from DRAFT to PUBLISHED, making it visible to students
7. IF an Instructor attempts to publish a course that has no modules or no lessons within its modules, THEN THE Platform SHALL return a 400 response with an error message indicating insufficient content
8. THE Platform SHALL support an is_premium flag on each course indicating whether enrollment requires payment, defaulting to false on creation
9. WHEN an Admin requests deletion of a course, THE Platform SHALL soft-delete the course, hiding it from listings while preserving enrollment data
10. THE Platform SHALL return paginated course listings with a default page size of 20 and a maximum page size of 100
11. IF a course creation or update request references a category that does not exist, THEN THE Platform SHALL return a 400 response with an error message indicating the invalid category reference

### Requirement 5: Course Categories and Learning Paths

**User Story:** As an Admin, I want to organize courses into categories and learning paths, so that students can discover relevant content efficiently.

#### Acceptance Criteria

1. WHEN an Admin submits a category creation request with a unique name between 1 and 100 characters, THE Platform SHALL persist the category for use in course classification
2. IF an Admin submits a category creation request with a name that already exists (case-insensitive), THEN THE Platform SHALL reject the request and return an error response indicating the category name is already in use
3. THE Platform SHALL allow each course to be assigned exactly one category
4. WHEN an Admin creates a learning path with a name (1–150 characters), an optional description, and an ordered list of 1 to 50 published course references, THE Platform SHALL validate that all referenced courses exist and are in PUBLISHED status and persist the learning path
5. IF an Admin creates or updates a learning path referencing a course that does not exist or is not in PUBLISHED status, THEN THE Platform SHALL reject the request and return an error response identifying the invalid course references
6. WHEN a Student requests available learning paths, THE Platform SHALL return a paginated list of learning paths that contain at least one published course, with their associated courses returned in defined order
7. WHEN an Admin updates the course order within a learning path, THE Platform SHALL persist the new ordering and return the updated learning path

### Requirement 6: Course Enrollment

**User Story:** As a Student, I want to enroll in courses, so that I can access course content and track my progress.

#### Acceptance Criteria

1. WHEN a Student requests enrollment in a free (non-premium) published course, THE Platform SHALL create an enrollment record and return the enrollment details including the enrollment date, course identifier, and initial completion percentage of zero
2. WHEN a Student requests enrollment in a premium course and the Student has valid premium access eligibility, THE Platform SHALL create the enrollment record and grant access to all course content
3. IF a Student requests enrollment in a premium course and does not have premium access eligibility, THEN THE Platform SHALL reject the enrollment request and return a 403 response indicating premium access is required
4. IF a Student attempts to enroll in a course that does not exist or is not in PUBLISHED status, THEN THE Platform SHALL return a 404 response
5. WHEN a Student attempts to enroll in a course they are already enrolled in, THE Platform SHALL return a 409 conflict response
6. WHEN a Student requests their enrolled courses, THE Platform SHALL return a paginated list including course title, enrollment date, and completion percentage for each enrollment
7. THE Platform SHALL record the enrollment date for each student-course association

### Requirement 7: Module Management

**User Story:** As an Instructor, I want to organize course content into ordered modules, so that students can follow a structured learning sequence.

#### Acceptance Criteria

1. WHEN an Instructor adds a module to their course with a title (1 to 150 characters) and order index, THE Platform SHALL create the module within that course and return the created module DTO
2. THE Platform SHALL maintain module ordering within a course using a unique numeric order index starting from 1 with no gaps
3. WHEN an Instructor reorders modules within their course, THE Platform SHALL update all affected order indices to reflect the new sequence without duplicates
4. WHEN an Instructor deletes a module from their course, THE Platform SHALL remove the module and all its contained lessons, then reorder remaining modules to maintain consecutive indices
5. WHEN a Student requests modules for an enrolled course, THE Platform SHALL return modules in ascending order index sequence
6. IF an Instructor submits a module creation or update request with missing or invalid fields (blank title or title exceeding 150 characters), THEN THE Platform SHALL return a 400 response with field-level validation error messages
7. WHEN an Instructor attempts to add, update, reorder, or delete a module in a course they do not own, THE Platform SHALL return a 403 response
8. WHEN an Instructor updates a module title within their course, THE Platform SHALL persist the updated title and return the updated module DTO

### Requirement 8: Lesson Management

**User Story:** As an Instructor, I want to create lessons with text and video content within modules, so that students can learn through multiple content formats.

#### Acceptance Criteria

1. WHEN an Instructor creates a lesson within a module of their course, THE Platform SHALL persist the lesson with title (maximum 200 characters), text content (maximum 50,000 characters), optional YouTube video URL, and order index starting from 1
2. THE Platform SHALL validate that YouTube video URLs match accepted patterns (youtube.com/watch?v={id}, youtu.be/{id}, or youtube.com/embed/{id}) and reject URLs not matching with a validation error message indicating the accepted formats
3. WHEN a Student requests a lesson from an enrolled course, THE Platform SHALL return the lesson content including the video ID extracted from the YouTube URL sufficient for iframe embedding
4. THE Platform SHALL maintain lesson ordering within a module using a numeric order index and update affected indices when a lesson is added or removed
5. IF a Student without premium enrollment requests a premium-only lesson, THEN THE Platform SHALL return a 403 response indicating premium enrollment is required without revealing the lesson content
6. THE Platform SHALL support YouTube video embedding without hosting video content on the platform
7. IF an Instructor submits a lesson creation request with missing or invalid required fields (title is blank or exceeds 200 characters, order index is not a positive integer), THEN THE Platform SHALL return a 400 response with field-level validation error messages

### Requirement 9: Quiz System

**User Story:** As an Instructor, I want to create multiple-choice quizzes, so that students can test their understanding and receive immediate feedback.

#### Acceptance Criteria

1. WHEN an Instructor creates a quiz for a lesson in their course, THE Platform SHALL persist the quiz with a title (1-200 characters), a passing score threshold expressed as a percentage (1-100), and one or more questions (maximum 50 questions per quiz)
2. THE Platform SHALL require each quiz question to have question text (maximum 2000 characters), at least two and at most six answer options (each maximum 500 characters), and exactly one correct answer marked
3. WHEN a Student submits quiz answers for an enrolled course lesson, THE Platform SHALL calculate the score as the percentage of correctly answered questions (correct answers divided by total questions multiplied by 100, rounded down to the nearest integer) and return the score, pass/fail status, and per-question correct/incorrect indication in the HTTP response
4. WHEN a Student achieves a score at or above the passing threshold, THE Platform SHALL mark the quiz as passed for that student
5. IF a Student submits answers for a quiz already passed, THEN THE Platform SHALL accept the submission and update the stored score only if the new score is higher, without awarding additional XP
6. WHEN a Student passes a quiz for the first time, THE Platform SHALL trigger an XP award event via the XpAwardService
7. IF a Student submits answers that do not include a response for every question in the quiz, THEN THE Platform SHALL treat unanswered questions as incorrect and calculate the score accordingly

### Requirement 10: Coding Challenges

**User Story:** As an Instructor, I want to create coding challenges with automated validation, so that students can practice coding with immediate feedback.

#### Acceptance Criteria

1. WHEN an Instructor creates a coding challenge for a lesson in their course, THE Platform SHALL persist the challenge with title (maximum 200 characters), description (maximum 5000 characters), starter code template, programming language, and at least one test case
2. WHEN a Student submits code (maximum 50,000 characters) for a coding challenge in an enrolled course, THE Platform SHALL execute the code against defined test cases in a sandboxed environment and return results
3. WHEN code execution completes for a student submission, THE Platform SHALL return execution results including pass/fail status for each test case and any compilation or runtime error messages
4. WHEN all test cases pass for a student submission, THE Platform SHALL mark the coding challenge as completed for that student
5. WHEN a Student completes a coding challenge, THE Platform SHALL trigger an XP award event via the XpAwardService
6. IF code execution exceeds 30 seconds, THEN THE Platform SHALL terminate execution and return a timeout error to the student
7. IF a Student submits code for a coding challenge already marked as completed, THEN THE Platform SHALL execute and return results without altering the completion status or awarding additional XP
8. IF a Student submits code for a coding challenge in a course they are not enrolled in, THEN THE Platform SHALL return a 403 response

### Requirement 11: XP and Level System

**User Story:** As a Student, I want to earn XP and level up by completing activities, so that I feel motivated to continue learning.

#### Acceptance Criteria

1. WHEN a Student completes a lesson, THE XpAwardService SHALL award the configured XP amount for lesson completion exactly once per student-lesson pair, ignoring subsequent completions of the same lesson
2. WHEN a Student passes a quiz, THE XpAwardService SHALL award the configured XP amount for quiz completion exactly once per student-quiz pair, ignoring subsequent passing submissions of the same quiz
3. WHEN a Student completes a coding challenge, THE XpAwardService SHALL award the configured XP amount for challenge completion exactly once per student-challenge pair, ignoring subsequent completions of the same challenge
4. WHEN a Student accumulates XP crossing one or more level thresholds, THE Platform SHALL update the student level to the highest level whose XP threshold has been reached
5. THE Platform SHALL calculate levels based on a defined XP-to-level mapping table with a maximum level cap
6. WHEN XP is awarded, THE Platform SHALL publish a domain event via Spring ApplicationEventPublisher for cross-cutting concerns
7. THE Platform SHALL expose an endpoint returning the student current XP total, current level, and XP required for the next level, or an indication that the maximum level has been reached
8. IF the XpAwardService fails to persist an XP award due to a system error, THEN THE Platform SHALL not mark the associated activity as completed and SHALL return an error indication to the caller

### Requirement 12: Streak System

**User Story:** As a Student, I want to track my daily learning streak, so that I stay motivated to learn consistently.

#### Acceptance Criteria

1. WHEN a Student completes at least one learning activity (lesson, quiz, or challenge) in a UTC calendar day and the student has no prior streak record, THE Platform SHALL set the student streak count to 1
2. WHEN a Student completes at least one learning activity in a UTC calendar day and the student already completed an activity on the immediately preceding UTC calendar day, THE Platform SHALL increment the student streak count by 1
3. WHEN a Student completes an additional learning activity in the same UTC calendar day where their streak has already been counted, THE Platform SHALL maintain the current streak count without further increment
4. WHEN the start of a new UTC calendar day is reached and a Student had no completed learning activity during the previous UTC calendar day, THE Platform SHALL reset the student streak count to zero
5. WHEN the student streak count is updated, THE Platform SHALL compare the new streak count against the stored longest streak and persist the higher value as the longest streak
6. WHEN a Student streak count reaches a milestone (7, 30, 100 days), THE Platform SHALL trigger a badge award event
7. THE Platform SHALL expose an endpoint returning the student current streak, longest streak, and whether the student has completed at least one learning activity in the current UTC calendar day

### Requirement 13: Badge System

**User Story:** As a Student, I want to earn badges for achievements, so that I have visible recognition of my accomplishments.

#### Acceptance Criteria

1. THE Platform SHALL define badges with a name (maximum 100 characters), description (maximum 500 characters), icon reference, and a earning criteria type identifying the triggering event and threshold (e.g., streak milestone of 7 days, total courses completed reaching 5)
2. WHEN a domain event is published that matches a badge earning criteria (such as streak milestone reached, course completed, or XP threshold crossed), THE Platform SHALL evaluate and award the corresponding badge to the qualifying student with a timestamp
3. THE Platform SHALL prevent duplicate badge awards (each badge awarded at most once per student)
4. WHEN a Student requests their badges, THE Platform SHALL return a paginated list of earned badges including badge name, description, icon reference, earning criteria description, and award date
5. WHEN a badge is awarded, THE Platform SHALL publish a domain event via Spring ApplicationEventPublisher and create a notification for the student
6. IF badge evaluation fails due to a system error during domain event processing, THEN THE Platform SHALL log the failure and allow retry on the next relevant domain event without blocking the originating operation

### Requirement 14: Quest System

**User Story:** As a Student, I want to participate in quests with defined objectives, so that I have short-term goals driving my learning activity.

#### Acceptance Criteria

1. WHEN an Admin creates a quest, THE Platform SHALL persist the quest with title (max 100 characters), description (max 500 characters), one or more objectives (max 10), XP reward (1 to 10000), start date, and end date where end date must be after start date
2. WHILE a quest is within its active date range, THE Platform SHALL display the quest as available to all enrolled students who have not yet completed or had the quest expire
3. WHEN a Student completes all objectives of an active quest, THE Platform SHALL mark the quest as completed and award the defined XP via XpAwardService
4. THE Platform SHALL track individual objective progress for each student participating in a quest and expose it via the quest detail endpoint, returning each objective with its completion status and current progress count relative to the target count
5. WHEN a quest end date passes without completion, THE Platform SHALL mark the quest as expired for students who did not complete it
6. IF an Admin submits a quest creation request with missing required fields, end date before or equal to start date, or zero objectives, THEN THE Platform SHALL reject the request and return a validation error response
7. THE Platform SHALL define each quest objective with a type (lesson_completion, quiz_pass, challenge_completion, or xp_earned), a target count, and a description (max 200 characters)

### Requirement 15: Progress Tracking

**User Story:** As a Student, I want to track my progress through courses, so that I know how much I have completed and what remains.

#### Acceptance Criteria

1. WHEN a Student marks a lesson as complete in an enrolled course, THE Platform SHALL record the lesson completion with the student identifier, lesson identifier, and a timestamp, and SHALL ignore duplicate completion requests for the same student-lesson pair
2. THE Platform SHALL calculate course completion percentage as the number of completed lessons divided by the total number of lessons across all modules in the course, rounded down to the nearest integer (0-100), returning 0 for courses with no lessons
3. WHEN a Student requests their progress for an enrolled course, THE Platform SHALL return the completion percentage, count of completed lessons, total lesson count, list of completed lesson identifiers with completion timestamps, and list of remaining lesson identifiers
4. IF a Student requests progress for a course they are not enrolled in, THEN THE Platform SHALL return a 403 response
5. WHEN a Student completes all lessons in a course, THE Platform SHALL mark the course as completed for that student with a completion timestamp and publish a domain event via Spring ApplicationEventPublisher
6. THE Platform SHALL expose a student dashboard endpoint returning a paginated list of progress summaries across all enrolled courses, where each summary includes course identifier, course title, completion percentage, total lessons, completed lessons count, and enrollment date

### Requirement 16: Certificate Generation

**User Story:** As a Student, I want to receive a certificate upon completing a course, so that I have proof of my accomplishment.

#### Acceptance Criteria

1. WHEN a Student completes all lessons in a course (100% completion), THE Platform SHALL generate a digital certificate for that student-course pair
2. THE Platform SHALL include the student name, course title, completion date, and a unique certificate identifier (UUID) on each certificate
3. IF a certificate already exists for a given student-course pair, THEN THE Platform SHALL not generate a duplicate certificate
4. WHEN a Student requests their certificates, THE Platform SHALL return a paginated list of earned certificates including certificate identifier, course title, and completion date
5. WHEN a valid certificate identifier is submitted to the public verification endpoint, THE Platform SHALL return the student name, course title, completion date, and certificate identifier
6. IF an invalid or non-existent certificate identifier is submitted to the public verification endpoint, THEN THE Platform SHALL return a 404 response indicating the certificate was not found
7. WHEN a certificate is generated, THE Platform SHALL publish a domain event and create a notification for the student

### Requirement 17: In-App Notifications

**User Story:** As a user, I want to receive in-app notifications about important events, so that I stay informed about my learning activities and platform updates.

#### Acceptance Criteria

1. WHEN a domain event occurs that is relevant to a user (badge earned, certificate issued, quest available, admin announcement), THE Platform SHALL create a notification record for that user containing a notification type, a human-readable title, a reference identifier linking to the source entity, a creation timestamp, and a read status defaulting to unread
2. WHEN a user requests their notifications, THE Platform SHALL return a paginated list ordered by creation date descending with a default page size of 20
3. WHEN a user marks a notification as read, THE Platform SHALL update the read status for that notification
4. IF a user attempts to access or modify a notification that does not belong to them, THEN THE Platform SHALL return a 403 response
5. THE Platform SHALL expose an endpoint returning the count of unread notifications for the authenticated user
6. WHEN an Admin creates an announcement, THE Platform SHALL generate notifications for all users whose accounts are not deactivated or suspended
7. IF a user attempts to mark a notification that does not exist, THEN THE Platform SHALL return a 404 response

### Requirement 18: Instructor Dashboard and Analytics

**User Story:** As an Instructor, I want to view analytics for my courses, so that I can understand student engagement and improve content.

#### Acceptance Criteria

1. WHEN an Instructor requests analytics for their course, THE Platform SHALL return enrollment count, average completion percentage, and average quiz pass rate
2. WHEN an Instructor requests lesson-level analytics for their course, THE Platform SHALL return the completion count per lesson ordered by lesson sequence within each module
3. WHEN an Instructor requests student activity for their course, THE Platform SHALL return paginated student progress records containing student display name, enrollment date, completion percentage, last activity timestamp, and quizzes passed count
4. IF an Instructor requests analytics for a course they do not own, THEN THE Platform SHALL return a 403 response
5. THE Platform SHALL return analytics data as pre-calculated aggregates updated via domain events rather than computed on each request

### Requirement 19: Admin Dashboard

**User Story:** As an Admin, I want to manage users, content, and view platform-wide analytics, so that I can ensure the platform operates effectively.

#### Acceptance Criteria

1. WHEN an Admin requests the user list, THE Platform SHALL return a paginated list (default page size 20) of all users including each user's role, registration date, and account status (ACTIVE, SUSPENDED, or LOCKED)
2. WHEN an Admin updates a user role to a valid role (STUDENT, INSTRUCTOR, or ADMIN), THE Platform SHALL persist the role change before returning the response and reflect the change in subsequent token issuances
3. IF an Admin attempts to change the role of the last remaining ADMIN account, THEN THE Platform SHALL reject the request and return an error message indicating that at least one Admin must exist
4. WHEN an Admin requests platform analytics, THE Platform SHALL return total registered users, total courses, daily active users (users who completed at least one learning activity within the last 24 hours), average course completion percentage across all enrollments, and total certificate issuance count
5. WHEN an Admin requests course listings, THE Platform SHALL return a paginated list (default page size 20) of all courses regardless of status (including DRAFT) with enrollment counts for each course
6. THE Platform SHALL restrict all admin dashboard endpoints to users with the ADMIN role and return a 403 response for non-ADMIN authenticated requests

### Requirement 20: Student Dashboard

**User Story:** As a Student, I want a personalized dashboard showing my learning progress, so that I can quickly see my status and decide what to work on next.

#### Acceptance Criteria

1. WHEN a Student requests their dashboard, THE Platform SHALL return current XP, level, streak count, and XP progress toward next level
2. WHEN a Student requests their dashboard, THE Platform SHALL include a list of all enrolled courses with completion percentages, ordered by most recent enrollment date first
3. WHEN a Student requests their dashboard, THE Platform SHALL include the last 10 completed activities (lessons, quizzes, or challenges), each containing the activity type, title, associated course title, and completion timestamp, ordered by completion timestamp descending
4. WHEN a Student requests their dashboard, THE Platform SHALL include all active quests with progress represented as the count of completed objectives out of total objectives for each quest
5. WHEN a Student requests their dashboard, THE Platform SHALL return all dashboard data within a single API response within 2 seconds under normal load
6. IF a Student requests their dashboard and has no enrolled courses, no recent activity, or no active quests, THEN THE Platform SHALL return empty collections for those respective sections without error

### Requirement 21: Course Search and Filtering

**User Story:** As a Student, I want to search and filter courses, so that I can find relevant content matching my interests and skill level.

#### Acceptance Criteria

1. WHEN a Student searches courses with a text query, THE Platform SHALL return a paginated list of published courses whose title or description contains the query text (case-insensitive, partial match), with a default page size of 20 results, sorted by relevance to the query
2. WHEN a Student filters courses by category, THE Platform SHALL return only published courses belonging to the specified category
3. WHEN a Student filters courses by difficulty level, THE Platform SHALL return only published courses matching the specified difficulty level (Beginner, Intermediate, or Advanced)
4. THE Platform SHALL support combining search query with category and difficulty filters in a single request, applying all specified filters as a logical AND (results must satisfy every provided criterion)
5. THE Platform SHALL return each course in search results with title, description, category, difficulty level, enrollment count, and average rating metadata
6. IF a search or filter request yields no matching courses, THEN THE Platform SHALL return an empty result set with a total count of zero
7. WHEN a Student navigates through paginated search results, THE Platform SHALL return the total number of matching courses and total number of pages alongside the result set

### Requirement 22: Course Reviews

**User Story:** As a Student, I want to review courses I have completed, so that I can share feedback and help other students choose courses.

#### Acceptance Criteria

1. WHEN a Student submits a review for a course they have enrolled in, THE Platform SHALL persist the review with a rating (integer from 1 to 5 inclusive) and an optional text comment of at most 1000 characters, and SHALL assign the review a "pending" moderation status
2. THE Platform SHALL allow each student to submit at most one review per course
3. IF a Student attempts to submit a review for a course they are not enrolled in, THEN THE Platform SHALL reject the submission and return an error indicating the student is not authorized to review that course
4. IF a Student attempts to submit a second review for a course they have already reviewed, THEN THE Platform SHALL reject the submission and return an error indicating a review already exists for that course
5. WHEN an Admin approves a review, THE Platform SHALL make the review visible to all users; WHEN an Admin rejects a review, THE Platform SHALL hide the review from all users except the author; WHEN an Admin removes a review, THE Platform SHALL permanently delete the review
6. WHEN a review is approved or removed, THE Platform SHALL recalculate the average rating for the affected course based on all approved reviews and display the result rounded to one decimal place

### Requirement 23: API Design Standards

**User Story:** As a developer, I want consistent API design patterns, so that the API is predictable and easy to integrate with.

#### Acceptance Criteria

1. THE Platform SHALL version all REST endpoints under the /api/v1 path prefix
2. WHEN a client requests a paginated list endpoint, THE Platform SHALL accept optional query parameters for page number (0-based, default 0), page size (default 20, maximum 100), and sort field with direction, and SHALL return response metadata containing page number, page size, total elements, and total pages
3. IF an API request results in an error, THEN THE Platform SHALL return an error response using a consistent error DTO containing timestamp, HTTP status code, error message, and field-level validation errors when the error originates from request body validation failures
4. THE Platform SHALL map all JPA entities to response DTOs before returning from controllers
5. THE Platform SHALL delegate all business logic to service classes with controllers performing only request validation and delegation
6. THE Platform SHALL document all endpoints using springdoc-openapi annotations
7. THE Platform SHALL use conventional HTTP status codes for standard operations: 201 for resource creation, 200 for successful retrieval and updates, 204 for successful deletion, 400 for validation errors, 404 for missing resources, and 409 for conflict states

### Requirement 24: Database and Migration Management

**User Story:** As a developer, I want managed database schema evolution, so that database changes are versioned, repeatable, and safe.

#### Acceptance Criteria

1. THE Platform SHALL manage all database schema changes through Flyway migration scripts following the naming convention `V<version>__<description>.sql` for versioned migrations and `R__<description>.sql` for repeatable migrations
2. THE Platform SHALL use PostgreSQL 16 as the production database
3. THE Platform SHALL organize code in a package-by-feature structure where each feature module contains its own entities, repositories, services, and controllers
4. THE Platform SHALL prevent N+1 query issues by using JOIN FETCH queries, entity graphs, or batch fetching for all collection associations accessed outside the owning entity's transaction
5. THE Platform SHALL use Spring Data JPA repositories for all database access operations
6. IF a Flyway migration script fails during execution, THEN THE Platform SHALL halt application startup, log an error message indicating the failed migration version and cause, and leave the database in the state prior to the failed migration

### Requirement 25: Frontend Architecture

**User Story:** As a developer, I want a well-structured frontend application, so that the UI is maintainable, performant, and accessible.

#### Acceptance Criteria

1. THE Platform SHALL implement the frontend using Next.js 14+ with App Router and TypeScript strict mode enabled in tsconfig.json
2. THE Platform SHALL use Tailwind CSS with shadcn/ui component library, where all UI components reference shared design tokens defined in the Tailwind theme configuration rather than hardcoded values
3. THE Platform SHALL use TanStack Query for server state management and data fetching
4. THE Platform SHALL use React Hook Form with Zod schemas for form validation, where validation errors are displayed on field blur and on form submission, and each invalid field displays a corresponding error message adjacent to the field
5. THE Platform SHALL implement mobile-first responsive design using Tailwind breakpoint utilities with breakpoints at 768px (tablet) and 1024px (desktop), where layouts transition from single-column on mobile to multi-column on tablet and desktop
6. THE Platform SHALL meet WCAG 2.1 AA accessibility standards including semantic HTML elements (nav, main, section, article, footer), ARIA attributes on interactive components, full keyboard navigation for all interactive elements with visible focus indicators, and color contrast ratios of at least 4.5:1 for normal text and 3:1 for large text and UI components
7. IF a TanStack Query request fails, THEN THE Platform SHALL display an error message indicating the failure and provide a retry option, and WHILE a query is in progress, THE Platform SHALL display a loading indicator within the requesting component's region

### Requirement 26: Security Requirements

**User Story:** As a platform operator, I want robust security controls, so that user data and platform integrity are protected.

#### Acceptance Criteria

1. THE Platform SHALL store passwords using BCrypt hashing with a minimum cost factor of 10
2. THE Platform SHALL transmit Access_Token and Refresh_Token exclusively via HTTP-only secure cookies with the SameSite attribute set to strict
3. THE Platform SHALL validate all user input using Jakarta Bean Validation at the controller layer
4. IF a state-changing request (POST, PUT, DELETE, PATCH) fails CSRF token validation, THEN THE Platform SHALL reject the request and return a 403 response
5. IF more than 5 failed login attempts occur for an account within 15 minutes, THEN THE Platform SHALL lock the account for 30 minutes and return a 429 response indicating the remaining lockout duration
6. THE Platform SHALL strip potentially dangerous HTML tags and attributes from all user-generated content before storage to prevent XSS attacks while preserving safe content

### Requirement 27: Domain Event System

**User Story:** As a developer, I want a decoupled event system for cross-cutting concerns, so that modules remain loosely coupled while supporting notifications and analytics.

#### Acceptance Criteria

1. THE Platform SHALL publish domain events via Spring ApplicationEventPublisher for cross-cutting operations: XP awards, badge grants, course completions, and certificate issuance, where each event includes the acting user identifier, the target entity identifier, the event type, and a timestamp
2. WHEN a domain event is published, THE Platform SHALL allow multiple independent listeners to process the event asynchronously, and IF a listener fails during processing, THEN THE Platform SHALL log the failure and continue processing in other listeners without affecting the publishing operation
3. THE Platform SHALL use domain events to trigger notification creation such that the source module has no compile-time dependency on the notification module
4. THE Platform SHALL use domain events to update analytics aggregates such that business modules have no compile-time dependency on the analytics module
5. THE Platform SHALL publish domain events only after the source transaction commits successfully, ensuring listeners do not process events for rolled-back operations

### Requirement 28: Development Infrastructure

**User Story:** As a developer, I want to containerize local development, so that I can run the full platform locally with minimal setup.

#### Acceptance Criteria

1. THE Platform SHALL provide a Docker Compose configuration for local development including the Spring Boot application on port 8080, PostgreSQL 16 database on port 5432, and Next.js frontend on port 3000, with service dependencies configured so that the database starts before the backend application
2. THE Platform SHALL include Testcontainers configuration for integration tests using PostgreSQL 16, matching the production database version
3. THE Platform SHALL enforce code quality via ESLint, Prettier, and Husky pre-commit hooks in the frontend project, where a linting or formatting violation causes the commit to fail with a non-zero exit code
4. THE Platform SHALL include JUnit 5 and Mockito dependencies for backend unit and integration testing, executable via Maven Surefire (unit tests) and Failsafe (integration tests) plugins
5. WHILE the application is running with the development Spring profile active, THE Platform SHALL generate OpenAPI documentation accessible at /swagger-ui.html for interactive browsing and /v3/api-docs for the raw OpenAPI specification
6. THE Platform SHALL define Docker Compose health checks for the PostgreSQL service and Spring Boot application, such that dependent services do not start until their dependencies report healthy
