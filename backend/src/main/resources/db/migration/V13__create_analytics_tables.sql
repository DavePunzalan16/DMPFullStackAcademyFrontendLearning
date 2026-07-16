CREATE TABLE course_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL UNIQUE REFERENCES courses(id),
    enrollment_count INTEGER NOT NULL DEFAULT 0,
    avg_completion_pct DECIMAL(5,2) NOT NULL DEFAULT 0,
    avg_quiz_pass_rate DECIMAL(5,2) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE platform_analytics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    total_users INTEGER NOT NULL DEFAULT 0,
    total_courses INTEGER NOT NULL DEFAULT 0,
    total_certificates INTEGER NOT NULL DEFAULT 0,
    avg_completion_pct DECIMAL(5,2) NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Insert singleton row for platform analytics
INSERT INTO platform_analytics (id, total_users, total_courses, total_certificates, avg_completion_pct)
VALUES (gen_random_uuid(), 0, 0, 0, 0);
