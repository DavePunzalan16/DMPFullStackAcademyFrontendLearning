CREATE TABLE quizzes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lesson_id UUID NOT NULL UNIQUE REFERENCES lessons(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    passing_score INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE quiz_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quiz_id UUID NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    question_text VARCHAR(2000) NOT NULL,
    order_index INTEGER NOT NULL
);

CREATE INDEX idx_quiz_questions_quiz_id ON quiz_questions(quiz_id);

CREATE TABLE quiz_answer_options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL REFERENCES quiz_questions(id) ON DELETE CASCADE,
    option_text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    order_index INTEGER NOT NULL
);

CREATE INDEX idx_quiz_answer_options_question_id ON quiz_answer_options(question_id);

CREATE TABLE quiz_attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id),
    quiz_id UUID NOT NULL REFERENCES quizzes(id),
    score INTEGER NOT NULL,
    passed BOOLEAN NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_quiz_attempts_student_quiz ON quiz_attempts(student_id, quiz_id);

CREATE TABLE quiz_best_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id),
    quiz_id UUID NOT NULL REFERENCES quizzes(id),
    best_score INTEGER NOT NULL,
    passed BOOLEAN NOT NULL,
    first_passed_at TIMESTAMP,
    UNIQUE(student_id, quiz_id)
);
