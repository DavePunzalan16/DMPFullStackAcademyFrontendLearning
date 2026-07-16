CREATE TABLE coding_challenges (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lesson_id UUID NOT NULL UNIQUE REFERENCES lessons(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    starter_code TEXT NOT NULL,
    language VARCHAR(50) NOT NULL,
    timeout_seconds INTEGER NOT NULL DEFAULT 30,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE challenge_test_cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    challenge_id UUID NOT NULL REFERENCES coding_challenges(id) ON DELETE CASCADE,
    input TEXT NOT NULL,
    expected_output TEXT NOT NULL,
    is_hidden BOOLEAN NOT NULL DEFAULT FALSE,
    order_index INTEGER NOT NULL
);

CREATE INDEX idx_challenge_test_cases_challenge_id ON challenge_test_cases(challenge_id);

CREATE TABLE challenge_submissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id),
    challenge_id UUID NOT NULL REFERENCES coding_challenges(id),
    code TEXT NOT NULL,
    passed BOOLEAN NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_challenge_submissions_student_challenge ON challenge_submissions(student_id, challenge_id);

CREATE TABLE challenge_completions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id),
    challenge_id UUID NOT NULL REFERENCES coding_challenges(id),
    completed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(student_id, challenge_id)
);
