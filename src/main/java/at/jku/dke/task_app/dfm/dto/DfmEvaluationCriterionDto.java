package at.jku.dke.task_app.dfm.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO for a DFM evaluation criterion.
 */
public record DfmEvaluationCriterionDto(
    @NotBlank @Size(max = 255) String name,
    @NotNull @DecimalMin("0.00") BigDecimal points,
    @NotBlank @Size(max = 5000) String subtree,
    boolean partialSolution,
    boolean allowIncorrectFactClass
) {
}
