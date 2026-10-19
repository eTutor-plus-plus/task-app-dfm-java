package at.jku.dke.task_app.dfm.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTaskInGroup;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Represents a dfm task.
 */
@Entity
@Table(name = "task")
public class DfmTask extends BaseTaskInGroup<DfmTaskGroup> {
    @NotNull
    @Column(name = "solution", nullable = false)
    private Integer solution;

    /**
     * Creates a new instance of class {@link DfmTask}.
     */
    public DfmTask() {
    }

    /**
     * Creates a new instance of class {@link DfmTask}.
     *
     * @param solution The solution.
     */
    public DfmTask(Integer solution) {
        this.solution = solution;
    }

    /**
     * Creates a new instance of class {@link DfmTask}.
     *
     * @param maxPoints The maximum points.
     * @param status    The status.
     * @param taskGroup The task group.
     * @param solution  The solution.
     */
    public DfmTask(BigDecimal maxPoints, TaskStatus status, DfmTaskGroup taskGroup, Integer solution) {
        super(maxPoints, status, taskGroup);
        this.solution = solution;
    }

    /**
     * Creates a new instance of class {@link DfmTask}.
     *
     * @param id        The identifier.
     * @param maxPoints The maximum points.
     * @param status    The status.
     * @param taskGroup The task group.
     * @param solution  The solution.
     */
    public DfmTask(Long id, BigDecimal maxPoints, TaskStatus status, DfmTaskGroup taskGroup, Integer solution) {
        super(id, maxPoints, status, taskGroup);
        this.solution = solution;
    }

    /**
     * Gets the solution.
     *
     * @return The solution.
     */
    public Integer getSolution() {
        return solution;
    }

    /**
     * Sets the solution.
     *
     * @param solution The solution.
     */
    public void setSolution(Integer solution) {
        this.solution = solution;
    }
}
