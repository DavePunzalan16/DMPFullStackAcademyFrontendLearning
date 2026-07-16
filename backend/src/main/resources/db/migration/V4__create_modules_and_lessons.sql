CREATE TABLE modules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(150) NOT NULL,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(course_id, order_index)
);

CREATE INDEX idx_modules_course_id ON modules(course_id);

CREATE TABLE lessons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    module_id UUID NOT NULL REFERENCES modules(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    text_content TEXT,
    video_url VARCHAR(500),
    video_id VARCHAR(20),
    is_premium BOOLEAN NOT NULL DEFAULT FALSE,
    order_index INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(module_id, order_index)
);

CREATE INDEX idx_lessons_module_id ON lessons(module_id);
