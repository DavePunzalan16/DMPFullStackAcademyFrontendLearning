package com.dmpacademy.config;

import com.dmpacademy.course.*;
import com.dmpacademy.lesson.Lesson;
import com.dmpacademy.lesson.LessonRepository;
import com.dmpacademy.learningpath.*;
import com.dmpacademy.module.Module;
import com.dmpacademy.module.ModuleRepository;
import com.dmpacademy.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Seeds the database with YouTube course content on first startup.
 * Idempotent — skips if courses already exist.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final LearningPathRepository learningPathRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (courseRepository.count() > 0) {
            log.info("DataSeeder: Courses already exist, skipping seed.");
            return;
        }

        log.info("DataSeeder: Seeding YouTube course content...");

        // Create system instructor
        User instructor = getOrCreateInstructor();

        // Create categories
        Category catFundamentals = createCategory("Fundamentals");
        Category catFrontend = createCategory("Frontend");
        Category catBackend = createCategory("Backend");
        Category catDevOps = createCategory("DevOps");

        // Create courses
        Course gitCourse = createGitCourse(instructor, catFundamentals);
        Course htmlCssCourse = createHtmlCssCourse(instructor, catFrontend);
        Course jsCourse = createJavaScriptCourse(instructor, catFrontend);
        Course tsCourse = createTypeScriptCourse(instructor, catFrontend);
        Course reactCourse = createReactCourse(instructor, catFrontend);
        Course nextjsCourse = createNextJsCourse(instructor, catFrontend);
        Course springCourse = createSpringBootCourse(instructor, catBackend);
        Course dockerCourse = createDockerCourse(instructor, catDevOps);

        // Create learning paths
        createLearningPaths(gitCourse, htmlCssCourse, jsCourse, tsCourse, reactCourse, nextjsCourse, springCourse, dockerCourse);

        log.info("DataSeeder: Seed complete! Created 8 courses with 60+ lessons.");
    }

    private User getOrCreateInstructor() {
        return userRepository.findByEmailIgnoreCase("instructor@dmpacademy.com")
                .orElseGet(() -> {
                    User user = User.builder()
                            .email("instructor@dmpacademy.com")
                            .passwordHash(passwordEncoder.encode("Instructor1234"))
                            .displayName("DMP Academy")
                            .role(Role.INSTRUCTOR)
                            .accountStatus(AccountStatus.ACTIVE)
                            .build();
                    return userRepository.save(user);
                });
    }

    private Category createCategory(String name) {
        return categoryRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseGet(() -> categoryRepository.save(Category.builder().name(name).build()));
    }

    // ─── GIT COURSE ───────────────────────────────────────────
    private Course createGitCourse(User instructor, Category category) {
        Course course = courseRepository.save(Course.builder()
                .title("Git & GitHub Mastery")
                .description("Master version control with Git and GitHub. Learn branching, merging, pull requests, and collaboration workflows used by professional developers worldwide.")
                .category(category).difficulty(Difficulty.BEGINNER).status(CourseStatus.PUBLISHED)
                .instructor(instructor).build());

        Module m1 = moduleRepository.save(Module.builder().course(course).title("Git Fundamentals").orderIndex(1).build());
        Module m2 = moduleRepository.save(Module.builder().course(course).title("GitHub & Collaboration").orderIndex(2).build());

        createLesson(m1, 1, "Git Full Course for Beginners", "RGOj5yH7evk", "Complete introduction to Git — installation, configuration, and your first repository.");
        createLesson(m1, 2, "Git Init, Add, Commit Explained", "8JJ101D3knE", "Learn the core Git workflow: initializing repos, staging changes, and committing snapshots.");
        createLesson(m1, 3, "Branching & Merging", "e9lnsKot_SQ", "Understand Git branches, how to create them, switch between them, and merge changes.");
        createLesson(m1, 4, "Git Log & History", "Uszj_k0DGtg", "Navigate your project history with git log, diff, and blame commands.");
        createLesson(m2, 1, "GitHub Push, Pull & Collaboration", "nhNq2kIvi9s", "Push your local code to GitHub and collaborate with pull requests.");
        createLesson(m2, 2, "Pull Requests & Code Review", "rgbCcBNZcdQ", "The professional workflow: creating PRs, reviewing code, and handling merge conflicts.");
        createLesson(m2, 3, "Git Workflow Best Practices", "Uszj_k0DGtg", "Feature branching, Git Flow, and trunk-based development strategies.");

        return course;
    }

    // ─── HTML & CSS COURSE ────────────────────────────────────
    private Course createHtmlCssCourse(User instructor, Category category) {
        Course course = courseRepository.save(Course.builder()
                .title("HTML & CSS Complete Course")
                .description("Build beautiful, responsive websites from scratch. Learn semantic HTML5, modern CSS3, Flexbox, Grid, animations, and responsive design principles.")
                .category(category).difficulty(Difficulty.BEGINNER).status(CourseStatus.PUBLISHED)
                .instructor(instructor).build());

        Module m1 = moduleRepository.save(Module.builder().course(course).title("HTML Fundamentals").orderIndex(1).build());
        Module m2 = moduleRepository.save(Module.builder().course(course).title("CSS Styling").orderIndex(2).build());
        Module m3 = moduleRepository.save(Module.builder().course(course).title("Responsive & Modern CSS").orderIndex(3).build());

        createLesson(m1, 1, "HTML Document Structure", "kUMe1FH4CHE", "The anatomy of an HTML document — doctype, head, body, and semantic elements.");
        createLesson(m1, 2, "HTML Elements & Attributes", "kUMe1FH4CHE", "Deep dive into HTML elements: headings, paragraphs, links, images, lists, and tables.");
        createLesson(m1, 3, "HTML Forms & Inputs", "fNcJuPIZ2WE", "Building interactive forms with input types, validation, and accessibility.");
        createLesson(m2, 1, "CSS Selectors & Properties", "OXGznpKZ_sA", "Targeting elements with selectors and applying visual styles.");
        createLesson(m2, 2, "The Box Model & Layout", "OXGznpKZ_sA", "Understanding margin, padding, border, and how elements occupy space.");
        createLesson(m2, 3, "Colors, Typography & Backgrounds", "OXGznpKZ_sA", "Styling text, choosing colors, and working with background properties.");
        createLesson(m3, 1, "Flexbox Layout", "fYq5PXgSsbE", "One-dimensional layouts made easy with CSS Flexbox.");
        createLesson(m3, 2, "CSS Grid", "jV8B24rSN5o", "Two-dimensional layouts with CSS Grid for complex page structures.");
        createLesson(m3, 3, "Responsive Design & Media Queries", "srvUrASNj0s", "Making websites work on all devices with responsive techniques.");

        return course;
    }

    // ─── JAVASCRIPT COURSE ────────────────────────────────────
    private Course createJavaScriptCourse(User instructor, Category category) {
        Course course = courseRepository.save(Course.builder()
                .title("JavaScript — Zero to Hero")
                .description("From variables to async/await. Master JavaScript fundamentals, DOM manipulation, ES6+ features, closures, promises, and build real interactive projects.")
                .category(category).difficulty(Difficulty.BEGINNER).status(CourseStatus.PUBLISHED)
                .instructor(instructor).build());

        Module m1 = moduleRepository.save(Module.builder().course(course).title("JS Fundamentals").orderIndex(1).build());
        Module m2 = moduleRepository.save(Module.builder().course(course).title("DOM & Events").orderIndex(2).build());
        Module m3 = moduleRepository.save(Module.builder().course(course).title("Async JavaScript").orderIndex(3).build());

        createLesson(m1, 1, "Variables, Types & Operators", "PkZNo7MFNFg", "let, const, data types, operators, and type coercion in JavaScript.");
        createLesson(m1, 2, "Functions & Scope", "PkZNo7MFNFg", "Function declarations, arrow functions, closures, and lexical scope.");
        createLesson(m1, 3, "Arrays & Objects", "PkZNo7MFNFg", "Working with arrays, objects, destructuring, and spread operator.");
        createLesson(m1, 4, "Control Flow & Loops", "PkZNo7MFNFg", "if/else, switch, for, while, and iteration methods.");
        createLesson(m2, 1, "DOM Selection & Manipulation", "5fb2aPlgoys", "querySelector, createElement, innerHTML, and DOM tree traversal.");
        createLesson(m2, 2, "Event Handling", "5fb2aPlgoys", "addEventListener, event objects, bubbling, delegation, and common events.");
        createLesson(m2, 3, "DOM Projects", "5fb2aPlgoys", "Building interactive UI components with vanilla JavaScript.");
        createLesson(m3, 1, "Promises & Async/Await", "PoRJizFvM7s", "Asynchronous programming with Promises, then/catch, and async/await.");
        createLesson(m3, 2, "Fetch API & HTTP Requests", "cuEtnrL9-H0", "Making API calls with fetch, handling JSON responses.");
        createLesson(m3, 3, "Error Handling & Best Practices", "cuEtnrL9-H0", "try/catch, error objects, and async error handling patterns.");

        return course;
    }

    // ─── TYPESCRIPT COURSE ────────────────────────────────────
    private Course createTypeScriptCourse(User instructor, Category category) {
        Course course = courseRepository.save(Course.builder()
                .title("TypeScript Deep Dive")
                .description("Level up your JavaScript with TypeScript. Learn type annotations, interfaces, generics, utility types, and how to write safer, more maintainable code.")
                .category(category).difficulty(Difficulty.INTERMEDIATE).status(CourseStatus.PUBLISHED)
                .instructor(instructor).build());

        Module m1 = moduleRepository.save(Module.builder().course(course).title("TypeScript Basics").orderIndex(1).build());
        Module m2 = moduleRepository.save(Module.builder().course(course).title("Advanced TypeScript").orderIndex(2).build());

        createLesson(m1, 1, "TypeScript Setup & First Types", "30LWjhZzg50", "Installing TypeScript, tsconfig.json, and basic type annotations.");
        createLesson(m1, 2, "Interfaces & Type Aliases", "30LWjhZzg50", "Defining contracts with interfaces and creating custom types.");
        createLesson(m1, 3, "Functions & Generics", "30LWjhZzg50", "Typed functions, optional parameters, and generic type parameters.");
        createLesson(m2, 1, "Utility Types", "30LWjhZzg50", "Partial, Required, Pick, Omit, Record, and other built-in utilities.");
        createLesson(m2, 2, "Type Guards & Narrowing", "30LWjhZzg50", "typeof, instanceof, discriminated unions, and type predicates.");
        createLesson(m2, 3, "TypeScript with React", "30LWjhZzg50", "Typing components, props, hooks, and event handlers in React.");

        return course;
    }

    // ─── REACT COURSE ─────────────────────────────────────────
    private Course createReactCourse(User instructor, Category category) {
        Course course = courseRepository.save(Course.builder()
                .title("React — Modern Frontend Development")
                .description("Build dynamic single-page applications with React. Hooks, state management, routing, context API, and real-world project patterns.")
                .category(category).difficulty(Difficulty.INTERMEDIATE).status(CourseStatus.PUBLISHED)
                .instructor(instructor).build());

        Module m1 = moduleRepository.save(Module.builder().course(course).title("React Fundamentals").orderIndex(1).build());
        Module m2 = moduleRepository.save(Module.builder().course(course).title("Hooks & State").orderIndex(2).build());
        Module m3 = moduleRepository.save(Module.builder().course(course).title("Advanced Patterns").orderIndex(3).build());

        createLesson(m1, 1, "Components & JSX", "bMknfKXIFA8", "Creating components, JSX syntax, and the component lifecycle.");
        createLesson(m1, 2, "Props & Data Flow", "bMknfKXIFA8", "Passing data between components with props and composition.");
        createLesson(m1, 3, "Conditional Rendering & Lists", "bMknfKXIFA8", "Rendering based on conditions and mapping arrays to components.");
        createLesson(m2, 1, "useState Hook", "bMknfKXIFA8", "Managing component state with the useState hook.");
        createLesson(m2, 2, "useEffect & Side Effects", "bMknfKXIFA8", "Data fetching, subscriptions, and cleanup with useEffect.");
        createLesson(m2, 3, "Custom Hooks", "bMknfKXIFA8", "Extracting and reusing logic with custom hooks.");
        createLesson(m3, 1, "Context API", "bMknfKXIFA8", "Global state management without external libraries.");
        createLesson(m3, 2, "Performance Optimization", "bMknfKXIFA8", "React.memo, useMemo, useCallback, and avoiding re-renders.");

        return course;
    }

    // ─── NEXT.JS COURSE ───────────────────────────────────────
    private Course createNextJsCourse(User instructor, Category category) {
        Course course = courseRepository.save(Course.builder()
                .title("Next.js — Full-Stack React Framework")
                .description("Build production-ready full-stack apps with Next.js. App Router, server components, data fetching, API routes, middleware, and deployment.")
                .category(category).difficulty(Difficulty.ADVANCED).status(CourseStatus.PUBLISHED)
                .instructor(instructor).build());

        Module m1 = moduleRepository.save(Module.builder().course(course).title("Next.js Fundamentals").orderIndex(1).build());
        Module m2 = moduleRepository.save(Module.builder().course(course).title("Advanced Next.js").orderIndex(2).build());

        createLesson(m1, 1, "Next.js App Router Overview", "Sklc_fQBmcs", "File-based routing, layouts, and the App Router architecture.");
        createLesson(m1, 2, "Pages, Layouts & Templates", "Sklc_fQBmcs", "Creating pages, shared layouts, and template patterns.");
        createLesson(m1, 3, "Data Fetching Patterns", "Sklc_fQBmcs", "Server components, async data fetching, and caching strategies.");
        createLesson(m1, 4, "Dynamic Routes & Params", "Sklc_fQBmcs", "URL parameters, dynamic segments, and catch-all routes.");
        createLesson(m2, 1, "API Routes & Server Actions", "Sklc_fQBmcs", "Building backend APIs within Next.js and using server actions.");
        createLesson(m2, 2, "Middleware & Authentication", "Sklc_fQBmcs", "Request middleware, protected routes, and auth patterns.");
        createLesson(m2, 3, "Deployment & Optimization", "Sklc_fQBmcs", "Deploying to Vercel, image optimization, and performance tuning.");

        return course;
    }

    // ─── SPRING BOOT COURSE ───────────────────────────────────
    private Course createSpringBootCourse(User instructor, Category category) {
        Course course = courseRepository.save(Course.builder()
                .title("Java Spring Boot — Backend Mastery")
                .description("Build production-ready REST APIs with Spring Boot 3, Spring Security, JPA, and PostgreSQL. The complete backend developer path.")
                .category(category).difficulty(Difficulty.INTERMEDIATE).status(CourseStatus.PUBLISHED)
                .instructor(instructor).build());

        Module m1 = moduleRepository.save(Module.builder().course(course).title("Spring Boot Basics").orderIndex(1).build());
        Module m2 = moduleRepository.save(Module.builder().course(course).title("REST APIs & Security").orderIndex(2).build());

        createLesson(m1, 1, "Spring Boot Project Setup", "9SGDpanrc8U", "Setting up a Spring Boot project with Spring Initializr and Maven.");
        createLesson(m1, 2, "Spring Boot Architecture", "9SGDpanrc8U", "Understanding auto-configuration, starters, and the Spring ecosystem.");
        createLesson(m1, 3, "Spring Data JPA & Repositories", "9SGDpanrc8U", "Database access with JPA entities and Spring Data repositories.");
        createLesson(m2, 1, "Building REST Controllers", "9SGDpanrc8U", "Creating REST endpoints with proper HTTP methods and responses.");
        createLesson(m2, 2, "Spring Security & JWT", "9SGDpanrc8U", "Securing APIs with Spring Security and JSON Web Tokens.");
        createLesson(m2, 3, "Testing & Deployment", "9SGDpanrc8U", "Unit tests, integration tests, and Docker deployment.");

        return course;
    }

    // ─── DOCKER COURSE ────────────────────────────────────────
    private Course createDockerCourse(User instructor, Category category) {
        Course course = courseRepository.save(Course.builder()
                .title("Docker & Containerization")
                .description("Containerize your applications with Docker. Learn images, containers, Docker Compose, multi-stage builds, and deployment strategies.")
                .category(category).difficulty(Difficulty.ADVANCED).status(CourseStatus.PUBLISHED)
                .instructor(instructor).build());

        Module m1 = moduleRepository.save(Module.builder().course(course).title("Docker Fundamentals").orderIndex(1).build());
        Module m2 = moduleRepository.save(Module.builder().course(course).title("Docker Compose & Production").orderIndex(2).build());

        createLesson(m1, 1, "What is Docker? Containers Explained", "pg19Z8LL06w", "Understanding containers vs VMs, Docker architecture, and why containers matter.");
        createLesson(m1, 2, "Docker Images & Dockerfile", "pg19Z8LL06w", "Building custom images with Dockerfile instructions and layers.");
        createLesson(m1, 3, "Running Containers", "pg19Z8LL06w", "docker run, port mapping, volumes, and container lifecycle.");
        createLesson(m2, 1, "Docker Compose", "pg19Z8LL06w", "Orchestrating multi-container apps with docker-compose.yml.");
        createLesson(m2, 2, "Multi-Stage Builds", "pg19Z8LL06w", "Optimizing Docker images with multi-stage build patterns.");
        createLesson(m2, 3, "Docker in Production", "pg19Z8LL06w", "Best practices for production deployments and security.");

        return course;
    }

    // ─── LEARNING PATHS ───────────────────────────────────────
    private void createLearningPaths(Course git, Course htmlCss, Course js, Course ts, Course react, Course nextjs, Course spring, Course docker) {
        // Full-Stack Developer Path
        LearningPath fullStack = learningPathRepository.save(LearningPath.builder()
                .name("Full-Stack Developer Path")
                .description("The complete journey from zero to full-stack developer. Master frontend, backend, and DevOps in one structured path.")
                .build());
        addCoursesToPath(fullStack, List.of(git, htmlCss, js, ts, react, nextjs, spring, docker));

        // Frontend Developer Path
        LearningPath frontend = learningPathRepository.save(LearningPath.builder()
                .name("Frontend Developer Path")
                .description("Become a frontend expert. From HTML basics to advanced React and Next.js applications.")
                .build());
        addCoursesToPath(frontend, List.of(htmlCss, js, ts, react, nextjs));

        // Backend Developer Path
        LearningPath backend = learningPathRepository.save(LearningPath.builder()
                .name("Backend Developer Path")
                .description("Master server-side development with Java, Spring Boot, databases, and containerization.")
                .build());
        addCoursesToPath(backend, List.of(git, js, spring, docker));
    }

    private void addCoursesToPath(LearningPath path, List<Course> courses) {
        List<LearningPathCourse> pathCourses = new ArrayList<>();
        for (int i = 0; i < courses.size(); i++) {
            pathCourses.add(LearningPathCourse.builder()
                    .learningPath(path)
                    .course(courses.get(i))
                    .orderIndex(i + 1)
                    .build());
        }
        path.setCourses(pathCourses);
        learningPathRepository.save(path);
    }

    private void createLesson(Module module, int order, String title, String videoId, String textContent) {
        lessonRepository.save(Lesson.builder()
                .module(module)
                .title(title)
                .videoUrl("https://www.youtube.com/watch?v=" + videoId)
                .videoId(videoId)
                .textContent(textContent)
                .orderIndex(order)
                .isPremium(false)
                .build());
    }
}
