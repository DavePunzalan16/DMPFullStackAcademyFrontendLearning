package com.dmpacademy.quest;

import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.common.exception.ValidationException;
import com.dmpacademy.gamification.XpAwardService;
import com.dmpacademy.quest.dto.*;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestService {

    private final QuestRepository questRepository;
    private final StudentQuestProgressRepository questProgressRepository;
    private final StudentObjectiveProgressRepository objectiveProgressRepository;
    private final UserRepository userRepository;
    private final XpAwardService xpAwardService;

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public QuestResponse createQuest(QuestCreateRequest request) {
        if (!request.endDate().isAfter(request.startDate())) {
            throw new ValidationException("End date must be after start date");
        }

        Quest quest = Quest.builder()
                .title(request.title().trim())
                .description(request.description().trim())
                .xpReward(request.xpReward())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        List<QuestObjective> objectives = new ArrayList<>();
        for (int i = 0; i < request.objectives().size(); i++) {
            QuestCreateRequest.ObjectiveRequest obj = request.objectives().get(i);
            objectives.add(QuestObjective.builder()
                    .quest(quest)
                    .objectiveType(obj.objectiveType())
                    .targetCount(obj.targetCount())
                    .description(obj.description().trim())
                    .orderIndex(i + 1)
                    .build());
        }
        quest.setObjectives(objectives);

        Quest saved = questRepository.save(quest);
        log.info("Quest created: {}", saved.getId());
        return mapToResponse(saved);
    }

    public List<QuestResponse> listActiveQuests() {
        return questRepository.findActiveQuests(LocalDate.now()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public QuestProgressResponse getQuestProgress(UUID questId, UUID studentId) {
        Quest quest = questRepository.findByIdWithObjectives(questId)
                .orElseThrow(() -> new ResourceNotFoundException("Quest", "id", questId));

        StudentQuestProgress progress = questProgressRepository
                .findByStudentIdAndQuestId(studentId, questId)
                .orElse(null);

        String status = progress != null ? progress.getStatus() : "ACTIVE";

        List<StudentObjectiveProgress> objProgress =
                objectiveProgressRepository.findByStudentIdAndObjectiveQuestId(studentId, questId);

        List<QuestProgressResponse.ObjectiveProgressItem> items = quest.getObjectives().stream()
                .map(obj -> {
                    StudentObjectiveProgress op = objProgress.stream()
                            .filter(p -> p.getObjective().getId().equals(obj.getId()))
                            .findFirst()
                            .orElse(null);
                    return new QuestProgressResponse.ObjectiveProgressItem(
                            obj.getId(), obj.getDescription(), obj.getObjectiveType(),
                            obj.getTargetCount(), op != null ? op.getCurrentCount() : 0,
                            op != null && op.isCompleted()
                    );
                }).toList();

        return new QuestProgressResponse(questId, quest.getTitle(), status, quest.getXpReward(), items);
    }

    @Transactional
    public void incrementObjectiveProgress(UUID studentId, String objectiveType) {
        List<Quest> activeQuests = questRepository.findActiveQuests(LocalDate.now());

        for (Quest quest : activeQuests) {
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective.getObjectiveType().equals(objectiveType)) {
                    updateObjectiveAndCheckCompletion(studentId, quest, objective);
                }
            }
        }
    }

    private void updateObjectiveAndCheckCompletion(UUID studentId, Quest quest, QuestObjective objective) {
        User student = userRepository.findById(studentId).orElse(null);
        if (student == null) return;

        // Get or create objective progress
        List<StudentObjectiveProgress> allProgress =
                objectiveProgressRepository.findByStudentIdAndObjectiveQuestId(studentId, quest.getId());

        StudentObjectiveProgress objProgress = allProgress.stream()
                .filter(p -> p.getObjective().getId().equals(objective.getId()))
                .findFirst()
                .orElse(null);

        if (objProgress == null) {
            objProgress = StudentObjectiveProgress.builder()
                    .student(student)
                    .objective(objective)
                    .currentCount(0)
                    .completed(false)
                    .build();
        }

        if (objProgress.isCompleted()) return;

        objProgress.setCurrentCount(objProgress.getCurrentCount() + 1);
        if (objProgress.getCurrentCount() >= objective.getTargetCount()) {
            objProgress.setCompleted(true);
        }
        objectiveProgressRepository.save(objProgress);

        // Check if all objectives for this quest are completed
        checkQuestCompletion(studentId, quest, student);
    }

    private void checkQuestCompletion(UUID studentId, Quest quest, User student) {
        List<StudentObjectiveProgress> allProgress =
                objectiveProgressRepository.findByStudentIdAndObjectiveQuestId(studentId, quest.getId());

        boolean allCompleted = quest.getObjectives().stream().allMatch(obj ->
                allProgress.stream()
                        .filter(p -> p.getObjective().getId().equals(obj.getId()))
                        .anyMatch(StudentObjectiveProgress::isCompleted)
        );

        if (allCompleted) {
            StudentQuestProgress questProgress = questProgressRepository
                    .findByStudentIdAndQuestId(studentId, quest.getId())
                    .orElse(StudentQuestProgress.builder()
                            .student(student).quest(quest).status("ACTIVE").build());

            if (!"COMPLETED".equals(questProgress.getStatus())) {
                questProgress.setStatus("COMPLETED");
                questProgress.setCompletedAt(Instant.now());
                questProgressRepository.save(questProgress);
                xpAwardService.awardXp(studentId, quest.getXpReward(), "QUEST", quest.getId());
                log.info("Student {} completed quest {}", studentId, quest.getId());
            }
        }
    }

    private QuestResponse mapToResponse(Quest quest) {
        return new QuestResponse(quest.getId(), quest.getTitle(), quest.getDescription(),
                quest.getXpReward(), quest.getStartDate(), quest.getEndDate(), quest.isActive(),
                quest.getObjectives().stream()
                        .map(o -> new QuestResponse.ObjectiveResponse(o.getId(), o.getObjectiveType(), o.getTargetCount(), o.getDescription(), o.getOrderIndex()))
                        .toList());
    }
}
