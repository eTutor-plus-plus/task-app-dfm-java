package at.jku.dke.task_app.dfm.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTaskInGroup;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;

/**
 * Represents a dfm task.
 */
@Entity
@Table(name = "task")
public class DfmTask extends BaseTaskInGroup<DfmTaskGroup> {
    @NotNull
    @Column(name = "solution", nullable = false, length = 5000)
    private String solution;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<DfmEvaluationCriterion> evaluationCriteria = new ArrayList<>();

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
    public DfmTask(String solution) {
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
    public DfmTask(BigDecimal maxPoints, TaskStatus status, DfmTaskGroup taskGroup, String solution) {
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
    public DfmTask(Long id, BigDecimal maxPoints, TaskStatus status, DfmTaskGroup taskGroup, String solution) {
        super(id, maxPoints, status, taskGroup);
        this.solution = solution;
    }

    /**
     * Gets the solution.
     *
     * @return The solution.
     */
    public String getSolution() {
        return solution;
    }

    /**
     * Sets the solution.
     *
     * @param solution The solution.
     */
    public void setSolution(String solution) {
        this.solution = solution;
    }

    public List<DfmEvaluationCriterion> getEvaluationCriteria() {
        return evaluationCriteria;
    }

    public void replaceEvaluationCriteria(List<DfmEvaluationCriterion> criteria) {
        evaluationCriteria.clear();
        criteria.forEach(criterion -> {
            criterion.setTask(this);
            evaluationCriteria.add(criterion);
        });
    }
}
