package com.dmpacademy.badge;

import com.dmpacademy.badge.dto.BadgeResponse;
import com.dmpacademy.common.dto.PageResponse;
import com.dmpacademy.common.exception.ResourceNotFoundException;
import com.dmpacademy.event.BadgeAwardedEvent;
import com.dmpacademy.user.User;
import com.dmpacademy.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void evaluateAndAwardBadge(UUID studentId, String criteriaType, int value) {
        List<Badge> matchingBadges = badgeRepository.findByCriteriaTypeAndCriteriaValue(criteriaType, value);

        for (Badge badge : matchingBadges) {
            if (!userBadgeRepository.existsByStudentIdAndBadgeId(studentId, badge.getId())) {
                User student = userRepository.findById(studentId)
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", studentId));

                UserBadge userBadge = UserBadge.builder()
                        .student(student)
                        .badge(badge)
                        .build();
                userBadgeRepository.save(userBadge);

                eventPublisher.publishEvent(new BadgeAwardedEvent(studentId, badge.getId(), badge.getName()));
                log.info("Badge '{}' awarded to student {}", badge.getName(), studentId);
            }
        }
    }

    public PageResponse<BadgeResponse> listEarnedBadges(UUID studentId, Pageable pageable) {
        Page<BadgeResponse> page = userBadgeRepository.findByStudentIdOrderByAwardedAtDesc(studentId, pageable)
                .map(ub -> new BadgeResponse(
                        ub.getBadge().getId(),
                        ub.getBadge().getName(),
                        ub.getBadge().getDescription(),
                        ub.getBadge().getIconRef(),
                        ub.getBadge().getCriteriaType() + ": " + ub.getBadge().getCriteriaValue(),
                        ub.getAwardedAt()
                ));
        return PageResponse.from(page);
    }
}
