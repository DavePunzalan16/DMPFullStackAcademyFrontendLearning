package com.dmpacademy.quest;

import com.dmpacademy.quest.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quests")
@RequiredArgsConstructor
@Tag(name = "Quests", description = "Quest management and progress")
public class QuestController {

    private final QuestService questService;

    @PostMapping
    @Operation(summary = "Create a quest (Admin only)")
    public ResponseEntity<QuestResponse> createQuest(@Valid @RequestBody QuestCreateRequest request) {
        QuestResponse response = questService.createQuest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List active quests")
    public ResponseEntity<List<QuestResponse>> listActiveQuests() {
        List<QuestResponse> quests = questService.listActiveQuests();
        return ResponseEntity.ok(quests);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get quest with progress (Student)")
    public ResponseEntity<QuestProgressResponse> getQuestProgress(
            @PathVariable UUID id, Authentication authentication
    ) {
        UUID studentId = (UUID) authentication.getPrincipal();
        QuestProgressResponse response = questService.getQuestProgress(id, studentId);
        return ResponseEntity.ok(response);
    }
}
