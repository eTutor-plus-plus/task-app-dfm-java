package at.jku.dke.task_app.dfm.controllers;

import at.jku.dke.dfm.core.graph.DfmGraphBuilder;
import at.jku.dke.dfm.core.graph.NodeKind;
import at.jku.dke.dfm.core.parser.DfmParseException;
import at.jku.dke.dfm.core.parser.DfmParser;
import at.jku.dke.etutor.task_app.auth.AuthConstants;
import at.jku.dke.task_app.dfm.dto.DfmGraphDto;
import at.jku.dke.task_app.dfm.dto.DfmParseErrorDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that provides a graph preview of DFM models.
 */
@RestController
@RequestMapping("/api/dfm")
public class DfmPreviewController {
    private static final int MAX_SOURCE_LENGTH = 5000;

    private final DfmParser parser = new DfmParser();
    private final DfmGraphBuilder graphBuilder = new DfmGraphBuilder();

    /**
     * Parses the DFM model and returns its graph representation.
     *
     * @param source The DFM model.
     * @return The graph or a parse error.
     */
    @PostMapping(value = "/parse", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthConstants.CRUD_AUTHORITY)
    @SecurityRequirement(name = AuthConstants.API_KEY_REQUIREMENT)
    public ResponseEntity<?> parse(@RequestBody String source) {
        if (source.length() > MAX_SOURCE_LENGTH)
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new DfmParseErrorDto("The DFM model must not exceed " + MAX_SOURCE_LENGTH + " characters.", null, null));

        try {
            return ResponseEntity.ok(toDto(source));
        } catch (DfmParseException ex) {
            return ResponseEntity.badRequest().body(new DfmParseErrorDto(
                ex.getMessage(),
                ex.getLine() > 0 ? ex.getLine() : null,
                ex.getColumn() > 0 ? ex.getColumn() : null));
        }
    }

    private DfmGraphDto toDto(String source) {
        var graph = graphBuilder.build(parser.parse(source));
        var nodes = graph.nodes().stream()
            .map(node -> new DfmGraphDto.Node(node.id(), node.label(), toNodeType(node.kind()), node.optional(), node.details()))
            .toList();
        var edges = graph.edges().stream()
            .map(edge -> new DfmGraphDto.Edge(edge.source(), edge.target(), edge.connectionType().name(), edge.optional()))
            .toList();
        return new DfmGraphDto(nodes, edges);
    }

    private static DfmGraphDto.NodeType toNodeType(NodeKind kind) {
        return switch (kind) {
            case FACT -> DfmGraphDto.NodeType.FACT;
            case LEVEL -> DfmGraphDto.NodeType.LEVEL;
            case DESCRIPTIVE -> DfmGraphDto.NodeType.DESCRIPTIVE;
        };
    }
}
