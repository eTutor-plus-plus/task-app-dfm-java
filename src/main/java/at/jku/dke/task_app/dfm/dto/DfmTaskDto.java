package at.jku.dke.task_app.dfm.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link at.jku.dke.task_app.dfm.data.entities.DfmTask}
 *
 * @param solution The solution.
 */
public record DfmTaskDto(@NotNull Integer solution) implements Serializable {
}
