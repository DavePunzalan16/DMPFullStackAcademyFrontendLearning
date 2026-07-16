-- Badges
CREATE TABLE badges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500) NOT NULL,
    icon_ref VARCHAR(255) NOT NULL,
    criteria_type VARCHAR(50) NOT NULL,
    criteria_value INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE user_badges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id),
    badge_id UUID NOT NULL REFERENCES badges(id),
    awarded_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(student_id, badge_id)
);

CREATE INDEX idx_user_badges_student_id ON user_badges(student_id);

-- Quests
CREATE TABLE quests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500) NOT NULL,
    xp_reward INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE quest_objectives (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quest_id UUID NOT NULL REFERENCES quests(id) ON DELETE CASCADE,
    objective_type VARCHAR(50) NOT NULL,
    target_count INTEGER NOT NULL,
    description VARCHAR(200) NOT NULL,
    order_index INTEGER NOT NULL
);

CREATE INDEX idx_quest_objectives_quest_id ON quest_objectives(quest_id);

CREATE TABLE student_quest_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id),
    quest_id UUID NOT NULL REFERENCES quests(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    completed_at TIMESTAMP,
    UNIQUE(student_id, quest_id)
);

CREATE INDEX idx_student_quest_progress_student_id ON student_quest_progress(student_id);

CREATE TABLE student_objective_progress (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id),
    objective_id UUID NOT NULL REFERENCES quest_objectives(id),
    current_count INTEGER NOT NULL DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(student_id, objective_id)
);
