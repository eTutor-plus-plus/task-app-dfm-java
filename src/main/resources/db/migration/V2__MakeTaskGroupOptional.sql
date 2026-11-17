-- DFM tasks do not belong to a task group anymore. The column is kept so that existing data is not lost.
ALTER TABLE task
    ALTER COLUMN task_group_id DROP NOT NULL;
