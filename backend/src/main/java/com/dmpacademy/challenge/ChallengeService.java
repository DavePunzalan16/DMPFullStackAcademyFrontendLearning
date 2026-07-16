package com.dmpacademy.challenge;

import com.dmpacademy.challenge.dto.*;
import com.dmpacademy.common.exception.AccessDeniedException;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.common.exception.ValidationException;
import com.dmpacademy.event.ChallengeCompletedEvent;
import com.dmpacademy.gamification.StreakService;
import com.dmpacademy.gamification.XpAwardService;
import com.dmpacademy.gamification.XpConfig;
import com.dmpacademy.lesson.Lesson;
import com.dmpacademy.lesson.LessonRepository;
import com.dmpacademy.progress.EnrollmentRepository;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeCompletionRepository completionRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CodeExecutionService codeExecutionService;
    private final XpAwardService xpAwardService;
    private final StreakService streakService;
    private final XpConfig xpConfig;
    private final ApplicationEventPublisher eventPublisher;

    @PreAuthorize("hasRole('INSTRUCTOR')")
    @Transactional
    public ChallengeResponse createChallenge(UUID lessonId, ChallengeCreateRequest request, UUID instructorId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId));

        if (!lesson.getModule().getCourse().getInstructor().getId().equals(instructorId)) {
            throw new AccessDeniedException("You do not own this course");
        }

        if (challengeRepository.existsByLessonId(lessonId)) {
            throw new ValidationException("A coding challenge already exists for this lesson");
        }

        CodingChallenge challenge = CodingChallenge.builder()
                .lesson(lesson)
                .title(request.title().trim())
                .description(request.description().trim())
                .starterCode(request.starterCode())
                .language(request.language().toLowerCase().trim())
                .timeoutSeconds(request.timeoutSeconds() != null ? request.timeoutSeconds() : 30)
                .build();

        List<ChallengeTestCase> testCases = new ArrayList<>();
        for (int i = 0; i < request.testCases().size(); i++) {
            ChallengeCreateRequest.TestCaseRequest tc = request.testCases().get(i);
            testCases.add(ChallengeTestCase.builder()
                    .challenge(challenge)
                    .input(tc.input())
                    .expectedOutput(tc.expectedOutput())
                    .isHidden(tc.isHidden())
                    .orderIndex(i + 1)
                    .build());
        }
        challenge.setTestCases(testCases);

        CodingChallenge saved = challengeRepository.save(challenge);
        log.info("Challenge created for lesson {}: {}", lessonId, saved.getId());
        return mapToResponse(saved);
    }

    public ChallengeResponse getChallenge(UUID lessonId) {
        CodingChallenge challenge = challengeRepository.findByLessonIdWithTestCases(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge", "lessonId", lessonId));
        return mapToResponse(challenge);
    }

    @PreAuthorize("hasRole('STUDENT')")
    @Transactional
    public ExecutionResultResponse submitCode(UUID challengeId, CodeSubmissionRequest request, UUID studentId) {
        CodingChallenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge", "id", challengeId));

        UUID courseId = challenge.getLesson().getModule().getCourse().getId();
        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AccessDeniedException("You are not enrolled in this course");
        }

        // Execute code against test cases
        ExecutionResultResponse result = codeExecutionService.executeCode(
                request.code(),
                challenge.getLanguage(),
                challenge.getTestCases(),
                challenge.getTimeoutSeconds()
        );

        // If all passed and not already completed, mark complete + award XP
        if (result.allPassed() && !completionRepository.existsByStudentIdAndChallengeId(studentId, challengeId)) {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

            ChallengeCompletion completion = ChallengeCompletion.builder()
                    .student(student)
                    .challenge(challenge)
                    .build();
            completionRepository.save(completion);

            xpAwardService.awardXp(studentId, xpConfig.getChallengeCompletion(), "CHALLENGE", challengeId);
            streakService.updateStreak(studentId);
            eventPublisher.publishEvent(new ChallengeCompletedEvent(studentId, challengeId));
            log.info("Student {} completed challenge {}", studentId, challengeId);
        }

        return result;
    }

    private ChallengeResponse mapToResponse(CodingChallenge challenge) {
        List<ChallengeResponse.TestCaseResponse> testCases = challenge.getTestCases().stream()
                .filter(tc -> !tc.isHidden())
                .map(tc -> new ChallengeResponse.TestCaseResponse(tc.getId(), tc.getInput(), tc.getExpectedOutput(), tc.getOrderIndex()))
                .toList();

        return new ChallengeResponse(
                challenge.getId(), challenge.getLesson().getId(), challenge.getTitle(),
                challenge.getDescription(), challenge.getStarterCode(), challenge.getLanguage(),
                challenge.getTimeoutSeconds(), testCases
        );
    }
}
