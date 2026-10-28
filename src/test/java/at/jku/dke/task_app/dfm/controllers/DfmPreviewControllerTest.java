package at.jku.dke.task_app.dfm.controllers;

import at.jku.dke.task_app.dfm.dto.DfmGraphDto;
import at.jku.dke.task_app.dfm.dto.DfmParseErrorDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DfmPreviewControllerTest {
    private final DfmPreviewController controller = new DfmPreviewController();

    @Test
    void parseReturnsGraphForValidModel() {
        var response = controller.parse("""
            fact Sales { Revenue; {descriptive} Salesperson; };
            dimension Time { Day - Month - Year; };
            Sales - Time;
            """);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        var graph = assertInstanceOf(DfmGraphDto.class, response.getBody());
        assertTrue(graph.nodes().stream().anyMatch(n -> n.type() == DfmGraphDto.NodeType.FACT
            && n.details().contains("Revenue")));
        assertTrue(graph.nodes().stream().anyMatch(n -> n.label().equals("Salesperson")
            && n.type() == DfmGraphDto.NodeType.DESCRIPTIVE));
        assertTrue(graph.nodes().stream().anyMatch(n -> n.label().equals("Day")));
        assertTrue(graph.edges().stream().anyMatch(e -> e.connectionType().equals("SIMPLE")));
    }

    @Test
    void parseReturnsBadRequestForInvalidModel() {
        var response = controller.parse("not a valid DFM model");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var error = assertInstanceOf(DfmParseErrorDto.class, response.getBody());
        assertTrue(error.message() != null && !error.message().isBlank());
    }

    @Test
    void parseRejectsOversizedModel() {
        var response = controller.parse("x".repeat(5001));

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertInstanceOf(DfmParseErrorDto.class, response.getBody());
    }
}
