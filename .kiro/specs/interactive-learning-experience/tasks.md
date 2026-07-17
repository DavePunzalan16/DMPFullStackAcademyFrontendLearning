# Implementation Plan: Interactive Learning Experience

## Overview

This implementation plan transforms DMP Full Stack Academy into an interactive learning platform with seeded YouTube course content, visual learning paths, an interactive lesson viewer, quiz player, in-browser code editor, certificate viewer, enhanced student dashboard, instructor course builder, admin panel, and gamification animations. The plan is organized into 50 tasks across 9 execution waves, building from data seeding through frontend features and integration.

## Tasks

- [ ] 1. Create DataSeeder ApplicationRunner class
  - Create `backend/src/main/java/com/dmpacademy/config/DataSeeder.java` implementing `ApplicationRunner`. Check if courses table is empty before seeding. Create a system instructor account (DMP Academy) if not exists. Use `@Transactional` for the entire seed operation.
  - _Requirements: 1.5_

- [ ] 2. Seed categories and Git Fundamentals course
  - In DataSeeder, create categories (Frontend, Backend, DevOps, Fundamentals). Create "Git Fundamentals" course (category: Fundamentals, difficulty: BEGINNER, status: PUBLISHED) with 2 modules: "Git Basics" (init, add, commit, status, log) and "Git Branching & Collaboration" (branch, merge, remote, pull request). 8 lessons total with real YouTube video IDs, titles, and text content summaries. Assign sequential orderIndex values.
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.6_

- [ ] 3. Seed HTML & CSS Fundamentals course
  - Create "HTML & CSS Fundamentals" course (category: Frontend, difficulty: BEGINNER, status: PUBLISHED) with 3 modules: "HTML Basics" (structure, elements, forms), "CSS Basics" (selectors, box model, layout), "Responsive Design" (flexbox, grid, media queries). 10 lessons with YouTube video IDs and text content. Assign sequential orderIndex.
  - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [ ] 4. Seed JavaScript Essentials course
  - Create "JavaScript Essentials" course (category: Frontend, difficulty: BEGINNER, status: PUBLISHED) with 3 modules: "JS Fundamentals" (variables, types, functions, control flow), "DOM & Events" (DOM manipulation, event handling), "Async JavaScript" (promises, async/await, fetch). 12 lessons with YouTube video IDs and text content.
  - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [ ] 5. Seed TypeScript Deep Dive course
  - Create "TypeScript Deep Dive" course (category: Frontend, difficulty: INTERMEDIATE, status: PUBLISHED) with 2 modules: "TypeScript Basics" (types, interfaces, generics), "Advanced TypeScript" (utility types, decorators, configuration). 8 lessons with YouTube video IDs and text content.
  - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [ ] 6. Seed React Development course
  - Create "React Development" course (category: Frontend, difficulty: INTERMEDIATE, status: PUBLISHED) with 3 modules: "React Fundamentals" (components, JSX, props, state), "Hooks & State Management" (useState, useEffect, custom hooks), "Advanced Patterns" (context, refs, performance). 10 lessons with YouTube video IDs and text content.
  - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [ ] 7. Seed Next.js Mastery course
  - Create "Next.js Mastery" course (category: Frontend, difficulty: ADVANCED, status: PUBLISHED) with 2 modules: "Next.js Fundamentals" (routing, pages, layouts, data fetching), "Advanced Next.js" (API routes, middleware, deployment, optimization). 10 lessons with YouTube video IDs and text content.
  - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [ ] 8. Seed sample quizzes for each module
  - Create one quiz per module (12+ quizzes total). Each quiz has 5 multiple-choice questions with 4 options, one correct answer, and a 70% passing threshold. Cover key concepts from each module's lessons.
  - _Requirements: 1.2, 1.3_

- [ ] 9. Seed sample coding challenges
  - Create 2 coding challenges per course (12 total). Each challenge has: title, description, difficulty, initial code template (JavaScript), and 3 test cases with input/expected output. Challenges should cover practical concepts from the course (e.g., array manipulation for JS, component logic for React).
  - _Requirements: 1.3_

- [ ] 10. Seed learning paths
  - Create 3 learning paths: "Full-Stack Developer Path" (all 6 courses in order), "Frontend Developer Path" (HTML/CSS → JS → TS → React → Next.js), "Fundamentals Path" (Git → HTML/CSS → JS). Each path has title, description, and ordered course references via LearningPathCourse entities.
  - _Requirements: 1.7_

- [ ] 11. Create learning paths listing page
  - Create `frontend/src/app/paths/page.tsx`. Fetch all learning paths from `GET /api/v1/learning-paths`. Display each path as a card with title, description, course count, and estimated duration (sum of lesson counts × 10 min). Add enrollment progress bar for authenticated users. Style with glass-card design consistent with existing pages.
  - _Requirements: 2.1, 2.3_

- [ ] 12. Create single learning path detail page
  - Create `frontend/src/app/paths/[pathId]/page.tsx`. Fetch path details from `GET /api/v1/learning-paths/{id}`. Display path header with title and overall progress. Show ordered course list as vertical track items with visual connections (line between items). Each course shows: title, module count, lesson count, completion status (locked/in-progress/complete), and "Start" or "Continue" button.
  - _Requirements: 2.2, 2.3, 2.4, 2.5, 2.6_

- [ ] 13. Add learning path progress backend endpoint
  - Create `GET /api/v1/learning-paths/{id}/progress` in `LearningPathController`. For the authenticated student, return an object with overall completion percentage and per-course completion status (percentage, enrolled boolean). Query enrollments and lesson progress for each course in the path.
  - _Requirements: 2.3_

- [ ] 14. Add learning paths navigation link
  - Add "Paths" link to the Navbar component. Update the course catalog page to include a banner/link promoting learning paths. Ensure the paths page is accessible from the student dashboard quick actions.
  - _Requirements: 2.1_

- [ ] 15. Create course lessons tree API endpoint
  - Create `GET /api/v1/courses/{courseId}/lessons-tree` in a new or existing controller. Return all modules with their lessons (id, title, orderIndex, completed status for authenticated user) in a nested structure. Use JOIN FETCH to avoid N+1 queries. Include module title and orderIndex.
  - _Requirements: 3.2_

- [ ] 16. Build lesson sidebar component
  - Create `frontend/src/components/learn/LessonSidebar.tsx`. Fetch the lessons tree for the current course. Display modules as collapsible sections with lessons listed underneath. Show checkmark (✓) for completed lessons, arrow (►) for current lesson. Highlight current lesson. Make sidebar scrollable with fixed position. Support mobile collapse (hamburger toggle).
  - _Requirements: 3.2, 3.6_

- [ ] 17. Enhance lesson viewer layout with sidebar
  - Refactor `frontend/src/app/learn/[courseId]/[lessonId]/page.tsx` to use a two-column layout: sidebar (250px fixed width) + main content (flex-1). Integrate LessonSidebar on the left. Keep existing YouTube embed, lesson notes, and progress bar. Add responsive behavior: sidebar hidden on mobile with toggle button.
  - _Requirements: 3.1, 3.2, 3.6, 3.7_

- [ ] 18. Add next/previous lesson navigation
  - Create `LessonNavigation` component. After marking complete, show "Next Lesson →" button that navigates to the next lesson in order (within same module, or first lesson of next module). Show "← Previous Lesson" for going back. If last lesson in course, show "Course Complete!" message and link to certificate/course page.
  - _Requirements: 3.5_

- [ ] 19. Create XP reward animation component
  - Create `frontend/src/components/gamification/XpPopup.tsx` using Framer Motion. On trigger, animate "+10 XP" text floating upward and fading out over 3 seconds. Use spring animation. Support configurable XP amount. Integrate into lesson viewer's "Mark Complete" success handler. Respect `prefers-reduced-motion`.
  - _Requirements: 3.3, 3.4, 10.1, 10.6_

- [ ] 20. Create quiz player page shell
  - Create `frontend/src/app/quiz/[quizId]/page.tsx`. Fetch quiz data from `GET /api/v1/lessons/{lessonId}/quiz` (or create a `GET /api/v1/quizzes/{quizId}` endpoint if needed). Implement three states: StartScreen, QuestionFlow, ResultsScreen. Manage current question index and selected answers in local state.
  - _Requirements: 4.1_

- [ ] 21. Build quiz start screen component
  - Create `QuizStartScreen` component showing quiz title, number of questions, passing score threshold, and a "Begin Quiz" button. Display any relevant metadata (time estimate, attempts). Style with glass-card design and gradient accent.
  - _Requirements: 4.1_

- [ ] 22. Build question display with option selection
  - Create `QuizQuestion` component. Display: progress bar (question N of M), question text, and 4 answer options as selectable cards. Highlight selected option with primary color border. Show "Next" button (disabled until selection). Use smooth transitions between questions (Framer Motion AnimatePresence).
  - _Requirements: 4.1, 4.2, 4.3_

- [ ] 23. Build quiz results screen
  - Create `QuizResultScreen` component. Display: score as fraction (8/10) and percentage (80%), pass/fail badge with color coding (green pass, red fail), XP earned (if passed). Show "Retry" button if failed, "Continue" button if passed. Trigger XpPopup animation on pass. Submit answers via `POST /api/v1/quizzes/{quizId}/submit`.
  - _Requirements: 4.4, 4.5, 4.6, 4.8_

- [ ] 24. Add quiz navigation guard
  - Implement `beforeunload` event listener and Next.js route change interception while quiz is in progress. Show confirmation dialog: "You have an unfinished quiz. Are you sure you want to leave?" Allow dismissal. Remove guard on quiz completion.
  - _Requirements: 4.7_

- [ ] 25. Create challenge editor page with split-pane layout
  - Create `frontend/src/app/challenge/[challengeId]/page.tsx`. Fetch challenge data from `GET /api/v1/challenges/{challengeId}`. Implement split-pane layout using CSS grid (50/50 default, resizable). Left panel: problem description. Right panel: code editor area. Add responsive behavior (stack vertically on mobile).
  - _Requirements: 5.1_

- [ ] 26. Integrate Monaco Editor
  - Install `@monaco-editor/react`. Create `frontend/src/components/challenge/CodeEditor.tsx` wrapping Monaco with: dark theme (vs-dark), language set from challenge, initial value from challenge template, auto-resize to fill container. Use dynamic import (`next/dynamic` with `ssr: false`) to avoid SSR issues.
  - _Requirements: 5.1, 5.2_

- [ ] 27. Build problem description panel
  - Create `frontend/src/components/challenge/ProblemPanel.tsx`. Display: challenge title, difficulty badge (Easy/Medium/Hard with color), description (rendered markdown), and test cases section. Show test input and expected output for visible test cases. Some test cases can be hidden (show "Hidden Test" placeholder).
  - _Requirements: 5.1, 5.4_

- [ ] 28. Build editor toolbar with run and reset
  - Create `frontend/src/components/challenge/EditorToolbar.tsx`. Include: language selector dropdown (JavaScript, Python), "Reset" button (restores initial template with confirmation), "Run ▶" button (primary style, triggers submission). Disable Run button while executing. Show loading spinner during execution.
  - _Requirements: 5.2, 5.8, 5.9_

- [ ] 29. Build test results output panel
  - Create `frontend/src/components/challenge/TestResults.tsx`. Display after code execution: list of test cases with pass (✓ green) or fail (✗ red) indicators. For failed tests, show expected output vs actual output. Show summary line: "3/5 tests passing". If all pass, show success state with XP animation trigger.
  - _Requirements: 5.3, 5.4, 5.5_

- [ ] 30. Implement code submission and execution flow
  - Wire up the Run button to `POST /api/v1/challenges/{challengeId}/submit` with `{ code, language }`. Handle loading state (disable button, show spinner). Parse response `ExecutionResultResponse` and update TestResults panel. On all tests passing, trigger XP reward and mark challenge complete.
  - _Requirements: 5.3, 5.5, 5.6, 5.7, 5.9_

- [ ] 31. Create certificate viewer page
  - Create `frontend/src/app/certificates/[certificateId]/page.tsx`. Fetch certificate from `GET /api/v1/certificates/{id}/verify`. Display a beautifully styled certificate card: student name (large, centered), course title, completion date (formatted), certificate ID, and DMP Academy branding. Add "Copy Link" button and "Share" button.
  - _Requirements: 6.1, 6.5_

- [ ] 32. Create public verification page
  - Create `frontend/src/app/verify/[certificateId]/page.tsx`. No authentication required. Fetch from same verify endpoint. Display certificate details with a "Verified ✓" badge prominently shown. Include platform branding. Handle invalid certificate ID with "Certificate not found" error page.
  - _Requirements: 6.2, 6.3, 6.4_

- [ ] 33. Implement copy-link and share functionality
  - Add "Copy Link" button that copies the public verification URL (`/verify/{id}`) to clipboard using `navigator.clipboard`. Show toast confirmation "Link copied!". Add optional social share buttons (LinkedIn, Twitter) with pre-filled text about course completion.
  - _Requirements: 6.5_

- [ ] 34. Add auto-certificate generation on course completion
  - Create or enhance a backend event listener that triggers when a student's lesson progress reaches 100% for a course AND all quizzes are passed. Call `CertificateService.generateCertificate()`. Emit notification to the student. Add endpoint `GET /api/v1/courses/{courseId}/certificate` to check if certificate exists for current user.
  - _Requirements: 6.6_

- [ ] 35. Create enhanced dashboard data endpoint
  - Create or enhance `GET /api/v1/dashboard/enhanced` to return: enrolled courses with progress percentages, current XP and level info (current XP, XP to next level, level number), current streak days, earned badges (last 6), active quests with progress, recent activity (last 10 items). Aggregate efficiently to minimize queries.
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6_

- [ ] 36. Build XP progress ring component
  - Create `frontend/src/components/dashboard/XpProgressRing.tsx`. Display a circular SVG progress ring showing XP progress toward next level. Center text shows current level. Below shows "1250 / 2000 XP". Use primary color for fill, dark background for track. Animate on mount.
  - _Requirements: 7.6_

- [ ] 37. Build enrolled courses progress section
  - Create `frontend/src/components/dashboard/EnrolledCourses.tsx`. Display enrolled courses as cards in a grid (2-3 columns). Each card shows: course thumbnail/icon, title, progress bar with percentage, "Continue" button linking to the next incomplete lesson. Show "No courses" state with CTA to catalog.
  - _Requirements: 7.1, 7.7_

- [ ] 38. Build recent activity feed component
  - Create `frontend/src/components/dashboard/ActivityFeed.tsx`. Display last 10 activities in a timeline-style list. Each item has: icon (📖 lesson, ✅ quiz, 🏅 badge, 💻 challenge), description text, and relative timestamp ("2 hours ago"). Fetch from `GET /api/v1/activity/recent`.
  - _Requirements: 7.2_

- [ ] 39. Build active quests and badge showcase
  - Create `frontend/src/components/dashboard/QuestPanel.tsx` showing active quests with title, progress bar (e.g., "3/5 lessons"), and XP reward. Create `frontend/src/components/dashboard/BadgeShowcase.tsx` displaying earned badges in a grid (icon + name). Limit to 6 most recent with "View all" link.
  - _Requirements: 7.3, 7.4_

- [ ] 40. Build streak indicator with fire animation
  - Create `frontend/src/components/dashboard/StreakIndicator.tsx`. Display streak count with a fire emoji/icon that has a CSS animation (flickering). Show "🔥 7 day streak" with encouraging text. If streak is 0, show "Start your streak today!" message. Use Framer Motion for mount animation.
  - _Requirements: 7.5_

- [ ] 41. Integrate all dashboard components
  - Refactor `frontend/src/app/dashboard/page.tsx` to use the new components. Layout: top row (XP ring + streak + stats), middle (enrolled courses grid), bottom split (quests panel left, activity feed right). Fetch all data from enhanced endpoint. Keep loading skeleton states. Maintain responsive grid layout.
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 7.6, 7.7_

- [ ] 42. Create instructor dashboard with course list
  - Create `frontend/src/app/instructor/dashboard/page.tsx`. Fetch instructor's courses from `GET /api/v1/courses?instructor=me`. Display courses as cards showing title, status (DRAFT/PUBLISHED badge), lesson count, student count. Add "Create New Course" button. Include empty state for no courses.
  - _Requirements: 8.1_

- [ ] 43. Build create course modal/form
  - Create `frontend/src/components/instructor/CreateCourseForm.tsx`. Modal or page with form fields: title (required), description (textarea), category (dropdown from API), difficulty (BEGINNER/INTERMEDIATE/ADVANCED), thumbnail URL. Submit via `POST /api/v1/courses`. On success, navigate to course editor.
  - _Requirements: 8.1_

- [ ] 44. Build course editor with module management
  - Create `frontend/src/app/instructor/courses/[courseId]/edit/page.tsx`. Tabbed interface: Info, Modules, Publish. Modules tab shows ordered list of modules with "Add Module" button. Each module is expandable showing its lessons. Support reordering modules (drag or up/down arrows). Add/edit module form: title, description.
  - _Requirements: 8.2, 8.7_

- [ ] 45. Build lesson creation form with YouTube URL extraction
  - Create `frontend/src/components/instructor/LessonForm.tsx`. Fields: title, YouTube URL (auto-extract video ID using regex), text content (markdown textarea), premium toggle. On YouTube URL input, extract video ID and show preview thumbnail. Submit creates lesson via `POST /api/v1/modules/{moduleId}/lessons`.
  - _Requirements: 8.3, 8.9_

- [ ] 46. Build quiz creation form
  - Create `frontend/src/components/instructor/QuizForm.tsx`. Allow adding multiple questions. Each question has: text, 4 answer options (text + "correct" checkbox), explanation (optional). Set passing score percentage (slider, default 70%). Submit via `POST /api/v1/lessons/{lessonId}/quiz`. Validate at least 3 questions.
  - _Requirements: 8.4_

- [ ] 47. Build coding challenge creation form
  - Create `frontend/src/components/instructor/ChallengeForm.tsx`. Fields: title, description (markdown), difficulty, language selector, initial code template (code editor), test cases (add/remove, each with input + expected output). Validate at least 2 test cases. Submit via existing challenge creation endpoint.
  - _Requirements: 8.5_

- [ ] 48. Build publish flow with validation and admin panel
  - Create `frontend/src/components/instructor/PublishPanel.tsx`. Display validation checklist: ✓ Has at least 1 module, ✓ Each module has at least 1 lesson, ✓ Course has title and description. Show red ✗ for unmet requirements. "Publish" button only enabled when all checks pass. Submit `PUT /api/v1/courses/{id}` with status PUBLISHED. Also create admin layout (`frontend/src/app/admin/layout.tsx`) with sidebar navigation, role-based route protection, and admin pages for user management, course moderation, category management, quest creation, review moderation, announcements, and backend `AdminController` endpoints.
  - _Requirements: 8.6, 8.8, 9.1, 9.2, 9.3, 9.4, 9.5, 9.6, 9.7, 9.8_

- [ ] 49. Create GamificationProvider context and animations
  - Create `frontend/src/components/gamification/GamificationProvider.tsx`. React context that exposes: `showXpPopup(amount)`, `showLevelUp(level)`, `showBadgeToast(badge)`. Wrap the app layout with this provider. Manage animation queue to prevent overlapping. Build level-up celebration animation (`LevelUpCelebration.tsx`) with full-screen overlay, confetti effect, and auto-dismiss after 4s. Build badge unlock toast (`BadgeToast.tsx`) with slide-in from top-right, auto-dismiss after 5s. Build streak fire animation (`StreakFire.tsx`) with CSS keyframe flickering. Integrate provider into `app/layout.tsx` and connect to mutation success callbacks.
  - _Requirements: 10.1, 10.2, 10.3, 10.4, 10.5, 10.6_

- [ ] 50. Final checkpoint — Ensure all tests pass and integration is complete
  - Ensure all tests pass, ask the user if questions arise. Verify all gamification animations are connected across lesson viewer, quiz player, and code editor pages. Confirm reduced motion preferences are respected throughout.
  - _Requirements: 10.5, 10.6_

## Notes

- Each task references specific requirements for traceability
- Tasks are organized in execution waves from data seeding through frontend features
- The implementation uses Java (Spring Boot) for the backend and TypeScript (Next.js) for the frontend
- Checkpoints ensure incremental validation at key integration points
- All gamification animations must respect `prefers-reduced-motion` browser preferences
- Monaco Editor must be loaded via dynamic import to avoid SSR issues in Next.js

## Task Dependency Graph

```json
{
  "waves": [
    { "id": 0, "tasks": ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"] },
    { "id": 1, "tasks": ["11", "12", "13", "14"] },
    { "id": 2, "tasks": ["15", "16", "17", "18", "19"] },
    { "id": 3, "tasks": ["20", "21", "22", "23", "24"] },
    { "id": 4, "tasks": ["25", "26", "27", "28", "29", "30"] },
    { "id": 5, "tasks": ["31", "32", "33", "34"] },
    { "id": 6, "tasks": ["35", "36", "37", "38", "39", "40", "41"] },
    { "id": 7, "tasks": ["42", "43", "44", "45", "46", "47", "48"] },
    { "id": 8, "tasks": ["49", "50"] }
  ]
}
```
