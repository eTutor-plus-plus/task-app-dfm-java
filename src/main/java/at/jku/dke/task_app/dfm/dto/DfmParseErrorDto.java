package at.jku.dke.task_app.dfm.dto;

/**
 * DTO for a DFM parse error.
 *
 * @param message The error message.
 * @param line    The line of the error or {@code null} if unknown.
 * @param column  The column of the error or {@code null} if unknown.
 */
public record DfmParseErrorDto(String message, Integer line, Integer column) {
}
