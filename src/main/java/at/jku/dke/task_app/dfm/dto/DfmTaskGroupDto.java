package at.jku.dke.task_app.dfm.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link at.jku.dke.task_app.dfm.data.entities.DfmTaskGroup}
 *
 * @param minNumber The minimum number.
 * @param maxNumber The maximum number.
 */
public record DfmTaskGroupDto(@NotNull Integer minNumber, @NotNull Integer maxNumber) implements Serializable {
}
