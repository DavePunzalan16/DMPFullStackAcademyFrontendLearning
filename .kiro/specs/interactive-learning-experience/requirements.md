# Requirements Document

## Introduction

This document specifies the requirements for transforming the DMP Full Stack Academy platform into a production-quality interactive learning experience comparable to CodeSignal and Codecademy. The enhancement covers seeded YouTube-based course content across 11 learning tracks, visual learning paths with progress tracking, an interactive lesson viewer, a question-by-question quiz player, an in-browser code editor with test execution, certificate viewing and public verification, an enhanced student progress dashboard with gamification, an instructor course builder CMS, an admin management panel, and gamification animations throughout the platform.

## Glossary

- **Platform**: The DMP Full Stack Academy web application (Next.js frontend + Spring Boot backend)
- **Lesson_Viewer**: The interactive page where students watch YouTube videos, read lesson notes, and mark lessons complete
- **Quiz_Player**: The interactive component that presents quiz questions one at a time with scoring and XP rewards
- **Code_Editor**: The in-browser split-pane coding workspace where students write and execute code against test cases
- **Learning_Path_Page**: A visual page showing an ordered sequence of courses forming a track (e.g., Full-Stack Developer)
- **Certificate_Viewer**: The page displaying a generated certificate with student name, course, date, and unique verification ID
- **Student_Dashboard**: The enhanced student-facing dashboard showing progress, streaks, badges, quests, and XP
- **Course_Builder**: The instructor-facing CMS for creating and managing courses, modules, lessons, quizzes, and challenges
- **Admin_Panel**: The administrator-facing panel for user management, content moderation, and system configuration
- **Seed_Data_Service**: The backend service that populates the database with pre-configured YouTube course content
- **Gamification_Engine**: The backend services responsible for awarding XP, tracking streaks, issuing badges, and managing quests
- **XP_Animation**: A visual pop-up that appears when a student earns experience points
- **Level_Up_Celebration**: A full-screen animation triggered when a student reaches a new level
- **Badge_Toast**: A toast notification that appears when a student unlocks a new badge
- **Streak_Indicator**: A visual fire animation showing the student's current learning streak

## Requirements

### Requirement 1: Seed YouTube Course Content

**User Story:** As a platform administrator, I want the database pre-populated with 58+ real YouTube lessons across 11 learning tracks organized into published courses, so that students have immediate access to high-quality learning content.

#### Acceptance Criteria

1. WHEN the application starts for the first time, THE Seed_Data_Service SHALL insert course records for Git Fundamentals, HTML & CSS Fundamentals, JavaScript Essentials, TypeScript Deep Dive, React Development, and Next.js Mastery into the database with status PUBLISHED
2. WHEN the application starts for the first time, THE Seed_Data_Service SHALL insert module records grouping lessons logically within each course (minimum 2 modules per course)
3. WHEN the application starts for the first time, THE Seed_Data_Service SHALL insert lesson records with valid YouTube video IDs, descriptive titles, and text content summaries for each of the 58+ lessons
4. WHEN the application starts for the first time, THE Seed_Data_Service SHALL assign sequential orderIndex values to all modules within a course and all lessons within a module
5. WHEN the seed data already exists in the database, THE Seed_Data_Service SHALL skip insertion without duplicating records
6. THE Seed_Data_Service SHALL create category records for Frontend, Backend, DevOps, and Fundamentals and associate each course with the appropriate category
7. WHEN the application starts for the first time, THE Seed_Data_Service SHALL create learning path records (Full-Stack Developer Path, Frontend Path, Backend Path) linking the appropriate courses in order

### Requirement 2: Visual Learning Paths

**User Story:** As a student, I want to see visual learning paths (tracks) that show me an ordered progression of courses with my completion status, so that I have a clear roadmap for my learning journey.

#### Acceptance Criteria

1. WHEN a student navigates to the learning paths page, THE Learning_Path_Page SHALL display all available learning paths with their title, description, total course count, and estimated duration
2. WHEN a student selects a learning path, THE Learning_Path_Page SHALL display the ordered list of courses in that path with each course showing its title, module count, lesson count, and thumbnail
3. WHILE a student is enrolled in courses within a learning path, THE Learning_Path_Page SHALL display a progress bar showing the percentage of courses completed within that path
4. WHEN a student completes all courses in a learning path, THE Learning_Path_Page SHALL display a completion badge and offer a path completion certificate
5. THE Learning_Path_Page SHALL display courses in the correct sequential order as defined by their orderIndex within the learning path
6. WHEN a student has not enrolled in any course within a path, THE Learning_Path_Page SHALL display an "Enroll" action for the first course in the sequence

### Requirement 3: Interactive Lesson Viewer

**User Story:** As a student, I want an interactive lesson page with a YouTube video embed, lesson notes, progress tracking, and XP rewards, so that I have an engaging and trackable learning experience.

#### Acceptance Criteria

1. WHEN a student opens a lesson, THE Lesson_Viewer SHALL display the YouTube video embed at the top, the lesson title, and the text content below the video
2. WHEN a student opens a lesson, THE Lesson_Viewer SHALL display a progress sidebar showing all lessons in the current module with checkmarks for completed lessons
3. WHEN a student clicks the "Mark Complete" button, THE Lesson_Viewer SHALL record the lesson as completed and award XP to the student
4. WHEN a lesson is successfully marked complete, THE Lesson_Viewer SHALL display an XP_Animation showing the points earned
5. WHEN a student completes the current lesson, THE Lesson_Viewer SHALL enable navigation to the next lesson in sequence
6. WHILE a student is viewing a lesson, THE Lesson_Viewer SHALL display the overall course progress bar with completion percentage and lesson count
7. IF the lesson video URL is invalid or unavailable, THEN THE Lesson_Viewer SHALL display a placeholder message indicating the video is not available while still showing the text content

### Requirement 4: Interactive Quiz Player

**User Story:** As a student, I want to take quizzes in a question-by-question flow with instant feedback and scoring, so that I can test my knowledge in an engaging way similar to CodeSignal assessments.

#### Acceptance Criteria

1. WHEN a student starts a quiz, THE Quiz_Player SHALL display questions one at a time with a progress bar showing current question number out of total questions
2. WHEN a student selects an answer option, THE Quiz_Player SHALL highlight the selected option with visual feedback before the student submits
3. WHEN a student submits an answer, THE Quiz_Player SHALL advance to the next question
4. WHEN a student completes all questions, THE Quiz_Player SHALL display a results screen showing the score as a fraction and percentage, pass or fail status based on the quiz passing threshold, and XP earned
5. IF a student fails the quiz, THEN THE Quiz_Player SHALL display a "Retry" button that allows the student to attempt the quiz again
6. WHEN a student passes a quiz, THE Quiz_Player SHALL record the best score and award XP through the Gamification_Engine
7. THE Quiz_Player SHALL prevent navigation away from the quiz without confirmation to avoid accidental progress loss
8. WHEN a student passes a quiz, THE Quiz_Player SHALL display the XP_Animation with the points earned

### Requirement 5: In-Browser Code Editor

**User Story:** As a student, I want an in-browser coding workspace with a problem description, code editor, and test case execution, so that I can practice coding challenges directly on the platform like CodeSignal.

#### Acceptance Criteria

1. THE Code_Editor SHALL display a split-pane layout with the problem description and test cases on the left panel, and a code editor with syntax highlighting on the right panel
2. THE Code_Editor SHALL support JavaScript and Python as programming languages with a language selector dropdown
3. WHEN a student clicks the "Run" button, THE Code_Editor SHALL send the code to the backend for execution against all test cases and display results within 10 seconds
4. WHEN test execution completes, THE Code_Editor SHALL display pass or fail status for each individual test case with expected output versus actual output for failed cases
5. WHEN all test cases pass, THE Code_Editor SHALL display a success state and award XP through the Gamification_Engine
6. IF code execution exceeds the timeout limit, THEN THE Code_Editor SHALL terminate the execution and display a timeout error message to the student
7. IF the submitted code produces a runtime error, THEN THE Code_Editor SHALL display the error message in a readable format to help the student debug
8. THE Code_Editor SHALL provide a "Reset" button that restores the initial code template for the challenge
9. WHILE the code is executing, THE Code_Editor SHALL display a loading indicator and disable the Run button to prevent duplicate submissions

### Requirement 6: Certificate Viewer and Verification

**User Story:** As a student, I want to view and share my earned certificates with a unique verification link, so that I can showcase my achievements to potential employers.

#### Acceptance Criteria

1. WHEN a student navigates to a certificate page, THE Certificate_Viewer SHALL display the student name, course title, completion date, and unique certificate ID in a visually designed layout
2. THE Certificate_Viewer SHALL provide a shareable URL that can be accessed without authentication for public verification
3. WHEN a visitor accesses the public verification URL, THE Certificate_Viewer SHALL display the certificate details confirming validity along with the student name and course title
4. IF a visitor accesses an invalid certificate URL, THEN THE Certificate_Viewer SHALL display a clear message indicating the certificate was not found
5. THE Certificate_Viewer SHALL provide a "Copy Link" button that copies the public verification URL to the clipboard
6. WHEN a student completes all lessons and quizzes in a course, THE Platform SHALL automatically generate a certificate for that student

### Requirement 7: Enhanced Student Progress Dashboard

**User Story:** As a student, I want a comprehensive dashboard showing my enrolled courses with progress, recent activity, quests, badges, streak, and XP progress, so that I can track my learning journey and stay motivated.

#### Acceptance Criteria

1. WHEN a student opens the dashboard, THE Student_Dashboard SHALL display all enrolled courses with progress bars showing completion percentage for each course
2. WHEN a student opens the dashboard, THE Student_Dashboard SHALL display a recent activity feed showing the last 10 learning actions (lessons completed, quizzes passed, badges earned)
3. WHEN a student opens the dashboard, THE Student_Dashboard SHALL display active quests with their progress and remaining requirements
4. WHEN a student opens the dashboard, THE Student_Dashboard SHALL display earned badges in a showcase grid with badge icons and earned dates
5. WHEN a student opens the dashboard, THE Student_Dashboard SHALL display the current streak as a Streak_Indicator with the number of consecutive active days
6. WHEN a student opens the dashboard, THE Student_Dashboard SHALL display an XP progress ring showing current XP, current level, and XP remaining until the next level
7. WHEN a student has no enrolled courses, THE Student_Dashboard SHALL display a call-to-action directing the student to the course catalog

### Requirement 8: Instructor Course Builder

**User Story:** As an instructor, I want a full course creation workflow where I can create courses, add modules, add lessons with YouTube URLs, add quizzes, and add coding challenges, so that I can build and publish complete courses through the platform.

#### Acceptance Criteria

1. WHEN an instructor starts course creation, THE Course_Builder SHALL provide a form to enter course title, description, category, difficulty level, and thumbnail URL
2. WHEN an instructor saves a course, THE Course_Builder SHALL allow adding modules with a title, description, and ordering within the course
3. WHEN an instructor adds a lesson to a module, THE Course_Builder SHALL provide fields for lesson title, YouTube video URL, text content, ordering, and premium status
4. WHEN an instructor adds a quiz to a lesson, THE Course_Builder SHALL provide a form to create questions with multiple answer options, marking one or more as correct, and setting a passing score percentage
5. WHEN an instructor adds a coding challenge, THE Course_Builder SHALL provide fields for challenge title, description, initial code template, language selection, and test cases with input and expected output
6. WHEN an instructor marks a course as published, THE Course_Builder SHALL make the course visible to students on the course catalog
7. WHILE a course is in draft status, THE Course_Builder SHALL allow the instructor to edit all content without affecting student access
8. THE Course_Builder SHALL validate that a course has at least one module with at least one lesson before allowing publication
9. WHEN an instructor provides a YouTube URL, THE Course_Builder SHALL extract and store the video ID automatically

### Requirement 9: Admin Panel

**User Story:** As an administrator, I want a management panel to manage users, moderate courses, manage categories, create quests, moderate reviews, and broadcast announcements, so that I can maintain platform quality and engagement.

#### Acceptance Criteria

1. WHEN an administrator navigates to user management, THE Admin_Panel SHALL display a paginated list of all users with their display name, email, role, and registration date
2. WHEN an administrator changes a user role, THE Admin_Panel SHALL update the user role to the selected value (STUDENT, INSTRUCTOR, or ADMIN)
3. WHEN an administrator navigates to course moderation, THE Admin_Panel SHALL display all courses with their status and allow approving, rejecting, or unpublishing courses
4. WHEN an administrator navigates to category management, THE Admin_Panel SHALL allow creating, editing, and deleting course categories
5. WHEN an administrator creates a quest, THE Admin_Panel SHALL provide fields for quest title, description, XP reward, required action type, required count, and duration
6. WHEN an administrator navigates to review moderation, THE Admin_Panel SHALL display reported or flagged reviews with options to approve or remove them
7. WHEN an administrator broadcasts an announcement, THE Admin_Panel SHALL send a notification to all active users through the notification system
8. THE Admin_Panel SHALL restrict access to users with the ADMIN role only

### Requirement 10: Gamification Animations

**User Story:** As a student, I want visual feedback animations when I earn XP, level up, maintain streaks, or unlock badges, so that my achievements feel rewarding and I stay motivated to continue learning.

#### Acceptance Criteria

1. WHEN a student earns XP from any action, THE Platform SHALL display an XP_Animation pop-up showing the points earned with an upward floating animation that fades after 3 seconds
2. WHEN a student reaches a new level, THE Platform SHALL display a Level_Up_Celebration animation with the new level number that appears for 4 seconds
3. WHEN a student maintains a learning streak, THE Student_Dashboard SHALL display a Streak_Indicator with a fire animation showing the streak day count
4. WHEN a student unlocks a new badge, THE Platform SHALL display a Badge_Toast notification showing the badge icon and name that appears for 5 seconds
5. WHILE animations are playing, THE Platform SHALL allow the student to dismiss them by clicking outside or pressing Escape
6. WHEN a student has enabled reduced motion preferences in their browser, THE Platform SHALL display static versions of all animations without movement effects
