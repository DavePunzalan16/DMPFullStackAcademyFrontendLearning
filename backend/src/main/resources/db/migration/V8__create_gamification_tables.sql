CREATE TABLE xp_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id),
    amount INTEGER NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_id UUID NOT NULL,
    awarded_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(student_id, source_type, source_id)
);

CREATE INDEX idx_xp_transactions_student_id ON xp_transactions(student_id);

CREATE TABLE level_thresholds (
    level INTEGER PRIMARY KEY,
    xp_required INTEGER NOT NULL UNIQUE
);
