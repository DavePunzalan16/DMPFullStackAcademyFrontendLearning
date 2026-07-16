package com.dmpacademy.quiz;

import com.dmpacademy.common.exception.AccessDeniedException;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.common.exception.ValidationException;
import com.dmpacademy.event.QuizPassedEvent;
import com.dmpacademy.gamification.StreakService;
import com.dmpacademy.gamification.XpAwardService;
import com.dmpacademy.gamification.XpConfig;
import com.dmpacademy.lesson.Lesson;
import com.dmpacademy.lesson.LessonRepository;
import com.dmpacademy.progress.EnrollmentRepository;
import com.dmpacademy.quiz.dto.*;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizBestScoreRepository bestScoreRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final XpAwardService xpAwardService;
    private final StreakService streakService;
    private final XpConfig xpConfig;
    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public QuizResponse createQuiz(UUID lessonId, QuizCreateRequest request, UUID instructorId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        // Verify ownership
        if (!lesson.getModule().getCourse().getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }

        // Check no quiz already exists for this lesson
        if (quizRepository.existsByLessonId(lessonId)) {
            throw new ValidationException("A quiz already exists for this lesson");
        }

        // Validate each question has exactly one correct answer
        for (QuizCreateRequest.QuestionRequest q : request.questions()) {
            long correctCount = q.options().stream().filter(QuizCreateRequest.OptionRequest::isCorrect).count();
            if (correctCount != 1) {
                throw new ValidationException("Each question must have exactly one correct answer");
            }
        }

        // Build quiz entity
        Quiz quiz = Quiz.builder()
                .lesson(lesson)
                .title(request.title().trim())
                .passingScore(request.passingScore())
                .build();

        // Build questions and options
        List<QuizQuestion> questions = new ArrayList<>();
        for (int i = 0; i < request.questions().size(); i++) {
            QuizCreateRequest.QuestionRequest qReq = request.questions().get(i);
            QuizQuestion question = QuizQuestion.builder()
                    .quiz(quiz)
                    .questionText(qReq.questionText().trim())
                    .orderIndex(i + 1)
                    .build();

            List<QuizAnswerOption> options = new ArrayList<>();
            for (int j = 0; j < qReq.options().size(); j++) {
                QuizCreateRequest.OptionRequest oReq = qReq.options().get(j);
                QuizAnswerOption option = QuizAnswerOption.builder()
                        .question(question)
                        .optionText(oReq.optionText().trim())
                        .isCorrect(oReq.isCorrect())
                        .orderIndex(j + 1)
                        .build();
                options.add(option);
            }
            question.setOptions(options);
            questions.add(question);
        }
        quiz.setQuestions(questions);

        Quiz saved = quizRepository.save(quiz);
        log.info("Quiz created for lesson {}: {}", lessonId, saved.getId());
        return mapToResponse(saved);
    }

    public QuizResponse getQuiz(UUID lessonId) {
        Quiz quiz = quizRepository.findByLessonIdWithQuestions(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "lessonId", lessonId));
        return mapToResponse(quiz);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public QuizResultResponse submitQuiz(UUID quizId, QuizSubmissionRequest request, UUID studentId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", "id", quizId));

        // Verify enrollment
        UUID courseId = quiz.getLesson().getModule().getCourse().getId();
        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AccessDeniedException("You are not enrolled in this course");
        }

        // Calculate score
        List<QuizQuestion> questions = quiz.getQuestions();
        int totalQuestions = questions.size();
        int correctAnswers = 0;
        List<QuizResultResponse.QuestionResult> results = new ArrayList<>();

        for (QuizQuestion question : questions) {
            UUID selectedOptionId = request.answers().get(question.getId());
            UUID correctOptionId = question.getOptions().stream()
                    .filter(QuizAnswerOption::isCorrect)
                    .findFirst()
                    .map(QuizAnswerOption::getId)
                    .orElse(null);

            boolean isCorrect = correctOptionId != null && correctOptionId.equals(selectedOptionId);
            if (isCorrect) correctAnswers++;

            results.add(new QuizResultResponse.QuestionResult(question.getId(), isCorrect, correctOptionId));
        }

        int score = totalQuestions == 0 ? 0 : (correctAnswers * 100) / totalQuestions;
        boolean passed = score >= quiz.getPassingScore();

        // Update best score
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

        QuizBestScore bestScore = bestScoreRepository.findByStudentIdAndQuizId(studentId, quizId)
                .orElse(null);

        boolean firstPass = false;

        if (bestScore == null) {
            bestScore = QuizBestScore.builder()
                    .student(student)
                    .quiz(quiz)
                    .bestScore(score)
                    .passed(passed)
                    .firstPassedAt(passed ? Instant.now() : null)
                    .build();
            firstPass = passed;
        } else {
            if (score > bestScore.getBestScore()) {
                bestScore.setBestScore(score);
            }
            if (passed && !bestScore.isPassed()) {
                bestScore.setPassed(true);
                bestScore.setFirstPassedAt(Instant.now());
                firstPass = true;
            }
        }
        bestScoreRepository.save(bestScore);

        // Award XP on first pass only
        if (firstPass) {
            xpAwardService.awardXp(studentId, xpConfig.getQuizPass(), "QUIZ", quizId);
            streakService.updateStreak(studentId);
            eventPublisher.publishEvent(new QuizPassedEvent(studentId, quizId, score));
            log.info("Student {} passed quiz {} with score {}", studentId, quizId, score);
        }

        return new QuizResultResponse(score, passed, totalQuestions, correctAnswers, results);
    }

    private QuizResponse mapToResponse(Quiz quiz) {
        List<QuizResponse.QuestionResponse> questions = quiz.getQuestions().stream()
                .map(q -> new QuizResponse.QuestionResponse(
                        q.getId(),
                        q.getQuestionText(),
                        q.getOrderIndex(),
                        q.getOptions().stream()
                                .map(o -> new QuizResponse.OptionResponse(o.getId(), o.getOptionText(), o.getOrderIndex()))
                                .toList()
                ))
                .toList();

        return new QuizResponse(
                quiz.getId(),
                quiz.getLesson().getId(),
                quiz.getTitle(),
                quiz.getPassingScore(),
                quiz.getQuestions().size(),
                questions
        );
    }
}
