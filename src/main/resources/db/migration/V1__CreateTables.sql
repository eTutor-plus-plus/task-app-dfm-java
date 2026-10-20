CREATE TYPE task_status AS ENUM ('draft', 'ready_for_approval', 'approved');
CREATE TYPE submission_mode AS ENUM ('run', 'diagnose', 'submit');

CREATE CAST (CHARACTER VARYING as task_status) WITH INOUT AS IMPLICIT;
CREATE CAST (CHARACTER VARYING as submission_mode) WITH INOUT AS IMPLICIT;

CREATE TABLE task_group
(
    id         BIGINT      NOT NULL,
    status     TASK_STATUS NOT NULL,
    min_number INTEGER     NOT NULL, -- custom column
    max_number INTEGER     NOT NULL, -- custom column
    CONSTRAINT task_group_pk PRIMARY KEY (id),
    CONSTRAINT task_group_number_range_ck CHECK (min_number <= max_number)
);

CREATE TABLE task
(
    id            BIGINT        NOT NULL,
    max_points    NUMERIC(7, 2) NOT NULL,
    status        TASK_STATUS   NOT NULL,
    task_group_id BIGINT        NOT NULL,
    solution      VARCHAR(5000) NOT NULL, -- custom column
    CONSTRAINT task_pk PRIMARY KEY (id),
    CONSTRAINT task_task_group_fk FOREIGN KEY (task_group_id) REFERENCES task_group (id)
        ON DELETE CASCADE,
    CONSTRAINT task_max_points_ck CHECK (max_points >= 0)
);

CREATE TABLE evaluation_criterion
(
    id                        BIGSERIAL      NOT NULL,
    task_id                   BIGINT        NOT NULL,
    name                      VARCHAR(255)  NOT NULL,
    points                    NUMERIC(7, 2) NOT NULL,
    subtree                   VARCHAR(5000) NOT NULL,
    is_partial_solution       BOOLEAN       NOT NULL DEFAULT FALSE,
    allow_incorrect_fact_class BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT evaluation_criterion_pk PRIMARY KEY (id),
    CONSTRAINT evaluation_criterion_task_fk FOREIGN KEY (task_id) REFERENCES task (id)
        ON DELETE CASCADE,
    CONSTRAINT evaluation_criterion_points_ck CHECK (points >= 0)
);

CREATE TABLE submission
(
    id                UUID                     DEFAULT gen_random_uuid(),
    user_id           VARCHAR(255),
    assignment_id     VARCHAR(255),
    task_id           BIGINT,
    submission_time   TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    language          VARCHAR(2)      NOT NULL DEFAULT 'en',
    mode              submission_mode NOT NULL,
    feedback_level    INT             NOT NULL,
    evaluation_result JSONB,
    submission        VARCHAR(5000) NOT NULL, -- custom column
    CONSTRAINT submission_pk PRIMARY KEY (id),
    CONSTRAINT submission_task_fk FOREIGN KEY (task_id) REFERENCES task (id)
        ON DELETE CASCADE
);
