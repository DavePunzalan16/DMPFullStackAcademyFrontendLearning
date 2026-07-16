CREATE TABLE learning_paths (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(150) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE learning_path_courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    learning_path_id UUID NOT NULL REFERENCES learning_paths(id) ON DELETE CASCADE,
    course_id UUID NOT NULL REFERENCES courses(id),
    order_index INTEGER NOT NULL,
    UNIQUE(learning_path_id, course_id),
    UNIQUE(learning_path_id, order_index)
);

CREATE INDEX idx_learning_path_courses_path_id ON learning_path_courses(learning_path_id);
