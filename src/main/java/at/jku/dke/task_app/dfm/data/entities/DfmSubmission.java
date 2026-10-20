package at.jku.dke.task_app.dfm.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseSubmission;
import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

/**
 * Represents a dfm input.
 */
@Entity
@Table(name = "submission")
public class DfmSubmission extends BaseSubmission<DfmTask> {
    @NotNull
    @Column(name = "submission", nullable = false, length = 5000)
    private String submission;

    /**
     * Creates a new instance of class {@link DfmSubmission}.
     */
    public DfmSubmission() {
    }

    /**
     * Creates a new instance of class {@link DfmSubmission}.
     *
     * @param submission The input.
     */
    public DfmSubmission(String submission) {
        this.submission = submission;
    }

    /**
     * Creates a new instance of class {@link DfmSubmission}.
     *
     * @param userId        The user id.
     * @param assignmentId  The assignment id.
     * @param task          The task.
     * @param language      The language.
     * @param feedbackLevel The feedback level.
     * @param mode          The mode.
     * @param submission    The input.
     */
    public DfmSubmission(String userId, String assignmentId, DfmTask task, String language, int feedbackLevel, SubmissionMode mode, String submission) {
        super(userId, assignmentId, task, language, feedbackLevel, mode);
        this.submission = submission;
    }

    /**
     * Gets the input.
     *
     * @return The input.
     */
    public String getSubmission() {
        return submission;
    }

    /**
     * Sets the input.
     *
     * @param submission The input.
     */
    public void setSubmission(String submission) {
        this.submission = submission;
    }
}
