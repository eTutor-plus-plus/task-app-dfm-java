package at.jku.dke.task_app.dfm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link at.jku.dke.task_app.dfm.data.entities.DfmTask}
 *
 * @param solution The solution.
 */
public record DfmTaskDto(@NotBlank @Size(max = 5000) String solution,
                         List<DfmEvaluationCriterionDto> evaluationCriteria) implements Serializable {
}
