INSERT INTO badges (id, name, description, icon_ref, criteria_type, criteria_value) VALUES
-- Streak milestones
(gen_random_uuid(), 'Week Warrior', 'Maintained a 7-day learning streak', 'badges/streak-7.svg', 'STREAK_MILESTONE', 7),
(gen_random_uuid(), 'Monthly Master', 'Maintained a 30-day learning streak', 'badges/streak-30.svg', 'STREAK_MILESTONE', 30),
(gen_random_uuid(), 'Century Scholar', 'Maintained a 100-day learning streak', 'badges/streak-100.svg', 'STREAK_MILESTONE', 100),

-- Course completion milestones
(gen_random_uuid(), 'First Steps', 'Completed your first course', 'badges/course-1.svg', 'COURSES_COMPLETED', 1),
(gen_random_uuid(), 'Knowledge Seeker', 'Completed 5 courses', 'badges/course-5.svg', 'COURSES_COMPLETED', 5),
(gen_random_uuid(), 'Course Champion', 'Completed 10 courses', 'badges/course-10.svg', 'COURSES_COMPLETED', 10),

-- XP thresholds
(gen_random_uuid(), 'Rising Star', 'Earned 1,000 XP', 'badges/xp-1000.svg', 'XP_THRESHOLD', 1000),
(gen_random_uuid(), 'XP Hunter', 'Earned 5,000 XP', 'badges/xp-5000.svg', 'XP_THRESHOLD', 5000),
(gen_random_uuid(), 'XP Legend', 'Earned 10,000 XP', 'badges/xp-10000.svg', 'XP_THRESHOLD', 10000);
