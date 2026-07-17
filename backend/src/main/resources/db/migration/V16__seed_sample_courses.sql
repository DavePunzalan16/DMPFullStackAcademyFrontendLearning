-- Seed an ADMIN user for managing content
INSERT INTO users (id, email, password_hash, display_name, role, xp_total, level, streak_count, longest_streak, account_status, failed_login_attempts, version, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000001', 'admin@dmpacademy.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DMP Admin', 'ADMIN', 0, 1, 0, 0, 'ACTIVE', 0, 0, NOW(), NOW());

-- Seed an INSTRUCTOR user
INSERT INTO users (id, email, password_hash, display_name, role, xp_total, level, streak_count, longest_streak, account_status, failed_login_attempts, version, created_at, updated_at)
VALUES ('00000000-0000-0000-0000-000000000002', 'instructor@dmpacademy.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'DMP Instructor', 'INSTRUCTOR', 0, 1, 0, 0, 'ACTIVE', 0, 0, NOW(), NOW());

-- Categories
INSERT INTO categories (id, name, created_at) VALUES
('10000000-0000-0000-0000-000000000001', 'Git & Version Control', NOW()),
('10000000-0000-0000-0000-000000000002', 'HTML & CSS', NOW()),
('10000000-0000-0000-0000-000000000003', 'JavaScript', NOW()),
('10000000-0000-0000-0000-000000000004', 'TypeScript', NOW()),
('10000000-0000-0000-0000-000000000005', 'React', NOW()),
('10000000-0000-0000-0000-000000000006', 'Next.js', NOW()),
('10000000-0000-0000-0000-000000000007', 'Tailwind CSS', NOW()),
('10000000-0000-0000-0000-000000000008', 'Java & Spring Boot', NOW()),
('10000000-0000-0000-0000-000000000009', 'PostgreSQL & SQL', NOW()),
('10000000-0000-0000-0000-000000000010', 'Docker', NOW()),
('10000000-0000-0000-0000-000000000011', 'Authentication & Security', NOW());

-- Course 1: Git & Version Control
INSERT INTO courses (id, title, description, category_id, difficulty, status, is_premium, instructor_id, deleted, enrollment_count, created_at, updated_at)
VALUES ('20000000-0000-0000-0000-000000000001', 'Git & GitHub Mastery', 'Master version control with Git and GitHub. Learn branching, merging, pull requests, and collaboration workflows used by professional developers.', '10000000-0000-0000-0000-000000000001', 'BEGINNER', 'PUBLISHED', false, '00000000-0000-0000-0000-000000000002', false, 0, NOW(), NOW());

INSERT INTO modules (id, course_id, title, order_index, created_at) VALUES
('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001', 'Git Fundamentals', 1, NOW()),
('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000001', 'GitHub Workflows', 2, NOW());

INSERT INTO lessons (id, module_id, title, text_content, video_url, video_id, is_premium, order_index, created_at) VALUES
('40000000-0000-0000-0000-000000000001', '30000000-0000-0000-0000-000000000001', 'Introduction to Git', 'Learn what Git is, why it matters, and how to set it up on your machine.', 'https://www.youtube.com/watch?v=RGOj5yH7evk', 'RGOj5yH7evk', false, 1, NOW()),
('40000000-0000-0000-0000-000000000002', '30000000-0000-0000-0000-000000000001', 'Git Basics - Init, Add, Commit', 'Master the fundamental Git commands for tracking your code changes.', 'https://www.youtube.com/watch?v=8JJ101D3knE', '8JJ101D3knE', false, 2, NOW()),
('40000000-0000-0000-0000-000000000003', '30000000-0000-0000-0000-000000000002', 'GitHub - Push, Pull, Collaboration', 'Learn how to collaborate with others using GitHub repositories.', 'https://www.youtube.com/watch?v=nhNq2kIvi9s', 'nhNq2kIvi9s', false, 1, NOW());

-- Course 2: HTML & CSS Fundamentals
INSERT INTO courses (id, title, description, category_id, difficulty, status, is_premium, instructor_id, deleted, enrollment_count, created_at, updated_at)
VALUES ('20000000-0000-0000-0000-000000000002', 'HTML & CSS Complete Course', 'Build beautiful, responsive websites from scratch. Learn semantic HTML, modern CSS, Flexbox, Grid, and responsive design principles.', '10000000-0000-0000-0000-000000000002', 'BEGINNER', 'PUBLISHED', false, '00000000-0000-0000-0000-000000000002', false, 0, NOW(), NOW());

INSERT INTO modules (id, course_id, title, order_index, created_at) VALUES
('30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000002', 'HTML Basics', 1, NOW()),
('30000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000002', 'CSS Styling', 2, NOW()),
('30000000-0000-0000-0000-000000000005', '20000000-0000-0000-0000-000000000002', 'Responsive Design', 3, NOW());

INSERT INTO lessons (id, module_id, title, text_content, video_url, video_id, is_premium, order_index, created_at) VALUES
('40000000-0000-0000-0000-000000000004', '30000000-0000-0000-0000-000000000003', 'HTML Structure & Elements', 'Learn the building blocks of every website — HTML tags, elements, and document structure.', 'https://www.youtube.com/watch?v=kUMe1FH4CHE', 'kUMe1FH4CHE', false, 1, NOW()),
('40000000-0000-0000-0000-000000000005', '30000000-0000-0000-0000-000000000003', 'Forms & Input Elements', 'Master HTML forms, input types, and form validation.', 'https://www.youtube.com/watch?v=kUMe1FH4CHE', 'kUMe1FH4CHE', false, 2, NOW()),
('40000000-0000-0000-0000-000000000006', '30000000-0000-0000-0000-000000000004', 'CSS Fundamentals', 'Selectors, properties, the box model, and how CSS styles flow.', 'https://www.youtube.com/watch?v=OXGznpKZ_sA', 'OXGznpKZ_sA', false, 1, NOW()),
('40000000-0000-0000-0000-000000000007', '30000000-0000-0000-0000-000000000005', 'Flexbox & Grid Layout', 'Modern CSS layout systems for building responsive designs.', 'https://www.youtube.com/watch?v=OXGznpKZ_sA', 'OXGznpKZ_sA', false, 1, NOW());

-- Course 3: JavaScript Fundamentals
INSERT INTO courses (id, title, description, category_id, difficulty, status, is_premium, instructor_id, deleted, enrollment_count, created_at, updated_at)
VALUES ('20000000-0000-0000-0000-000000000003', 'JavaScript — Zero to Hero', 'From variables to async/await. Master JavaScript fundamentals, DOM manipulation, ES6+ features, and build real projects.', '10000000-0000-0000-0000-000000000003', 'BEGINNER', 'PUBLISHED', false, '00000000-0000-0000-0000-000000000002', false, 0, NOW(), NOW());

INSERT INTO modules (id, course_id, title, order_index, created_at) VALUES
('30000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000003', 'JS Basics', 1, NOW()),
('30000000-0000-0000-0000-000000000007', '20000000-0000-0000-0000-000000000003', 'Data Structures & Algorithms', 2, NOW());

INSERT INTO lessons (id, module_id, title, text_content, video_url, video_id, is_premium, order_index, created_at) VALUES
('40000000-0000-0000-0000-000000000008', '30000000-0000-0000-0000-000000000006', 'Variables, Types & Operators', 'JavaScript fundamentals — let, const, data types, and operators.', 'https://www.youtube.com/watch?v=PkZNo7MFNFg', 'PkZNo7MFNFg', false, 1, NOW()),
('40000000-0000-0000-0000-000000000009', '30000000-0000-0000-0000-000000000006', 'Functions & Scope', 'Function declarations, arrow functions, closures, and scope.', 'https://www.youtube.com/watch?v=PkZNo7MFNFg', 'PkZNo7MFNFg', false, 2, NOW()),
('40000000-0000-0000-0000-000000000010', '30000000-0000-0000-0000-000000000007', 'Arrays, Objects & Algorithms', 'Data structures in JS and solving algorithmic problems.', 'https://www.youtube.com/watch?v=t2CEgPsws3U', 't2CEgPsws3U', false, 1, NOW());

-- Course 4: React
INSERT INTO courses (id, title, description, category_id, difficulty, status, is_premium, instructor_id, deleted, enrollment_count, created_at, updated_at)
VALUES ('20000000-0000-0000-0000-000000000004', 'React — Modern Frontend Development', 'Build dynamic single-page applications with React. Hooks, state management, routing, and real-world project patterns.', '10000000-0000-0000-0000-000000000005', 'INTERMEDIATE', 'PUBLISHED', false, '00000000-0000-0000-0000-000000000002', false, 0, NOW(), NOW());

INSERT INTO modules (id, course_id, title, order_index, created_at) VALUES
('30000000-0000-0000-0000-000000000008', '20000000-0000-0000-0000-000000000004', 'React Fundamentals', 1, NOW()),
('30000000-0000-0000-0000-000000000009', '20000000-0000-0000-0000-000000000004', 'Hooks & State', 2, NOW());

INSERT INTO lessons (id, module_id, title, text_content, video_url, video_id, is_premium, order_index, created_at) VALUES
('40000000-0000-0000-0000-000000000011', '30000000-0000-0000-0000-000000000008', 'Components & JSX', 'React components, JSX syntax, and rendering.', 'https://www.youtube.com/watch?v=bMknfKXIFA8', 'bMknfKXIFA8', false, 1, NOW()),
('40000000-0000-0000-0000-000000000012', '30000000-0000-0000-0000-000000000009', 'useState & useEffect', 'Managing state and side effects in functional components.', 'https://www.youtube.com/watch?v=bMknfKXIFA8', 'bMknfKXIFA8', false, 1, NOW());

-- Course 5: Spring Boot (INTERMEDIATE)
INSERT INTO courses (id, title, description, category_id, difficulty, status, is_premium, instructor_id, deleted, enrollment_count, created_at, updated_at)
VALUES ('20000000-0000-0000-0000-000000000005', 'Java Spring Boot — Backend Mastery', 'Build production-ready REST APIs with Spring Boot 3, Spring Security, JPA, and PostgreSQL. The complete backend developer path.', '10000000-0000-0000-0000-000000000008', 'INTERMEDIATE', 'PUBLISHED', false, '00000000-0000-0000-0000-000000000002', false, 0, NOW(), NOW());

INSERT INTO modules (id, course_id, title, order_index, created_at) VALUES
('30000000-0000-0000-0000-000000000010', '20000000-0000-0000-0000-000000000005', 'Spring Boot Basics', 1, NOW()),
('30000000-0000-0000-0000-000000000011', '20000000-0000-0000-0000-000000000005', 'REST APIs & JPA', 2, NOW());

INSERT INTO lessons (id, module_id, title, text_content, video_url, video_id, is_premium, order_index, created_at) VALUES
('40000000-0000-0000-0000-000000000013', '30000000-0000-0000-0000-000000000010', 'Spring Boot Project Setup', 'Setting up a Spring Boot project with Spring Initializr.', 'https://www.youtube.com/watch?v=9SGDpanrc8U', '9SGDpanrc8U', false, 1, NOW()),
('40000000-0000-0000-0000-000000000014', '30000000-0000-0000-0000-000000000011', 'Building REST Controllers', 'Creating REST endpoints with Spring Web.', 'https://www.youtube.com/watch?v=9SGDpanrc8U', '9SGDpanrc8U', false, 1, NOW());

-- Course 6: Docker (ADVANCED)
INSERT INTO courses (id, title, description, category_id, difficulty, status, is_premium, instructor_id, deleted, enrollment_count, created_at, updated_at)
VALUES ('20000000-0000-0000-0000-000000000006', 'Docker & Containerization', 'Containerize your applications with Docker. Learn images, containers, Docker Compose, and deployment strategies.', '10000000-0000-0000-0000-000000000010', 'ADVANCED', 'PUBLISHED', false, '00000000-0000-0000-0000-000000000002', false, 0, NOW(), NOW());

INSERT INTO modules (id, course_id, title, order_index, created_at) VALUES
('30000000-0000-0000-0000-000000000012', '20000000-0000-0000-0000-000000000006', 'Docker Fundamentals', 1, NOW());

INSERT INTO lessons (id, module_id, title, text_content, video_url, video_id, is_premium, order_index, created_at) VALUES
('40000000-0000-0000-0000-000000000015', '30000000-0000-0000-0000-000000000012', 'What is Docker? Containers Explained', 'Understanding containers, images, and why Docker matters.', 'https://www.youtube.com/watch?v=pg19Z8LL06w', 'pg19Z8LL06w', false, 1, NOW()),
('40000000-0000-0000-0000-000000000016', '30000000-0000-0000-0000-000000000012', 'Dockerfile & Docker Compose', 'Building images and orchestrating multi-container apps.', 'https://www.youtube.com/watch?v=pg19Z8LL06w', 'pg19Z8LL06w', false, 2, NOW());

-- Sample Quiz for Git lesson
INSERT INTO quizzes (id, lesson_id, title, passing_score, created_at) VALUES
('50000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000001', 'Git Basics Quiz', 70, NOW());

INSERT INTO quiz_questions (id, quiz_id, question_text, order_index) VALUES
('60000000-0000-0000-0000-000000000001', '50000000-0000-0000-0000-000000000001', 'What command initializes a new Git repository?', 1),
('60000000-0000-0000-0000-000000000002', '50000000-0000-0000-0000-000000000001', 'What does "git add" do?', 2),
('60000000-0000-0000-0000-000000000003', '50000000-0000-0000-0000-000000000001', 'What is a commit in Git?', 3);

INSERT INTO quiz_answer_options (id, question_id, option_text, is_correct, order_index) VALUES
('70000000-0000-0000-0000-000000000001', '60000000-0000-0000-0000-000000000001', 'git init', true, 1),
('70000000-0000-0000-0000-000000000002', '60000000-0000-0000-0000-000000000001', 'git start', false, 2),
('70000000-0000-0000-0000-000000000003', '60000000-0000-0000-0000-000000000001', 'git new', false, 3),
('70000000-0000-0000-0000-000000000004', '60000000-0000-0000-0000-000000000001', 'git create', false, 4),
('70000000-0000-0000-0000-000000000005', '60000000-0000-0000-0000-000000000002', 'Stages changes for the next commit', true, 1),
('70000000-0000-0000-0000-000000000006', '60000000-0000-0000-0000-000000000002', 'Deletes files from the repo', false, 2),
('70000000-0000-0000-0000-000000000007', '60000000-0000-0000-0000-000000000002', 'Pushes code to GitHub', false, 3),
('70000000-0000-0000-0000-000000000008', '60000000-0000-0000-0000-000000000003', 'A snapshot of your staged changes', true, 1),
('70000000-0000-0000-0000-000000000009', '60000000-0000-0000-0000-000000000003', 'A branch of code', false, 2),
('70000000-0000-0000-0000-000000000010', '60000000-0000-0000-0000-000000000003', 'A backup file', false, 3);

-- Sample Coding Challenge for JavaScript
INSERT INTO coding_challenges (id, lesson_id, title, description, starter_code, language, timeout_seconds, created_at) VALUES
('80000000-0000-0000-0000-000000000001', '40000000-0000-0000-0000-000000000008', 'FizzBuzz Challenge', 'Write a function that prints numbers from 1 to n. For multiples of 3 print "Fizz", for multiples of 5 print "Buzz", for both print "FizzBuzz".', 'function fizzBuzz(n) {\n  // Your code here\n}\n\nfizzBuzz(15);', 'javascript', 10, NOW());

INSERT INTO challenge_test_cases (id, challenge_id, input, expected_output, is_hidden, order_index) VALUES
('90000000-0000-0000-0000-000000000001', '80000000-0000-0000-0000-000000000001', '5', '1\n2\nFizz\n4\nBuzz', false, 1),
('90000000-0000-0000-0000-000000000002', '80000000-0000-0000-0000-000000000001', '15', '1\n2\nFizz\n4\nBuzz\nFizz\n7\n8\nFizz\nBuzz\n11\nFizz\n13\n14\nFizzBuzz', true, 2);
