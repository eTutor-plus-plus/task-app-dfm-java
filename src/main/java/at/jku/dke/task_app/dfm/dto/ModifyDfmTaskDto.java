package at.jku.dke.task_app.dfm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.List;

/**
 * This class represents a data transfer object for modifying a dfm task.
 *
 * @param solution The solution.
 */
public record ModifyDfmTaskDto(@NotBlank @Size(max = 5000) String solution,
                               List<DfmEvaluationCriterionDto> evaluationCriteria) implements Serializable {
    public ModifyDfmTaskDto {
        evaluationCriteria = evaluationCriteria == null ? List.of() : List.copyOf(evaluationCriteria);
    }
}
