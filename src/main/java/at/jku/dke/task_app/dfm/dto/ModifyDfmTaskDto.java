package at.jku.dke.task_app.dfm.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * This class represents a data transfer object for modifying a dfm task.
 *
 * @param solution The solution.
 */
public record ModifyDfmTaskDto(@NotNull Integer solution) implements Serializable {
}
