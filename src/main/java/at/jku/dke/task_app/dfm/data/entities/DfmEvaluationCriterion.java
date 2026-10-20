package at.jku.dke.task_app.dfm.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

/**
 * A criterion used to grade a DFM task.
 */
@Entity
@Table(name = "evaluation_criterion")
public class DfmEvaluationCriterion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "points", nullable = false, precision = 7, scale = 2)
    private BigDecimal points;

    @NotBlank
    @Column(name = "subtree", nullable = false, length = 5000)
    private String subtree;

    @Column(name = "is_partial_solution", nullable = false)
    private boolean partialSolution;

    @Column(name = "allow_incorrect_fact_class", nullable = false)
    private boolean allowIncorrectFactClass;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private DfmTask task;

    protected DfmEvaluationCriterion() {
    }

    public DfmEvaluationCriterion(String name, BigDecimal points, String subtree,
                                  boolean partialSolution, boolean allowIncorrectFactClass) {
        this.name = name;
        this.points = points;
        this.subtree = subtree;
        this.partialSolution = partialSolution;
        this.allowIncorrectFactClass = allowIncorrectFactClass;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPoints() {
        return points;
    }

    public String getSubtree() {
        return subtree;
    }

    public boolean isPartialSolution() {
        return partialSolution;
    }

    public boolean isAllowIncorrectFactClass() {
        return allowIncorrectFactClass;
    }

    public DfmTask getTask() {
        return task;
    }

    public void setTask(DfmTask task) {
        this.task = task;
    }
}
