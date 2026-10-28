package at.jku.dke.task_app.dfm.dto;

import java.util.List;

/**
 * DTO for the graph representation of a DFM model.
 *
 * @param nodes The nodes.
 * @param edges The edges.
 */
public record DfmGraphDto(List<Node> nodes, List<Edge> edges) {
    /**
     * The node types.
     */
    public enum NodeType {
        /**
         * A fact.
         */
        FACT,
        /**
         * A dimension level.
         */
        LEVEL,
        /**
         * A descriptive attribute.
         */
        DESCRIPTIVE
    }

    /**
     * A graph node.
     *
     * @param id       The identifier.
     * @param label    The label.
     * @param type     The node type.
     * @param optional Whether the node is optional.
     * @param details  Additional details (e.g., measures of a fact).
     */
    public record Node(String id, String label, NodeType type, boolean optional, List<String> details) {
    }

    /**
     * A graph edge.
     *
     * @param source         The identifier of the source node.
     * @param target         The identifier of the target node.
     * @param connectionType The connection type.
     * @param optional       Whether the connection is optional.
     */
    public record Edge(String source, String target, String connectionType, boolean optional) {
    }
}
