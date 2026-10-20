package at.jku.dke.task_app.dfm.visualization;

import at.jku.dke.task_app.dfm.models.ast.AbstractElement;
import at.jku.dke.task_app.dfm.models.ast.DimensionElement;
import at.jku.dke.task_app.dfm.models.ast.FactElement;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Creates a compact SVG overview of a parsed DFM.
 */
public class DfmVisualizationService {
    private static final int NODE_WIDTH = 180;
    private static final int NODE_HEIGHT = 36;
    private static final int GAP = 24;

    public String render(List<AbstractElement> elements) {
        var visibleElements = flattenElements(elements);
        int width = Math.max(320, visibleElements.size() * (NODE_WIDTH + GAP) + GAP);
        int height = 180;
        var svg = new StringBuilder()
            .append("<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"")
            .append(width).append("\" height=\"").append(height)
            .append("\" viewBox=\"0 0 ").append(width).append(" ").append(height).append("\">")
            .append("<style>.fact{fill:#dbeafe;stroke:#2563eb}.dimension{fill:#dcfce7;stroke:#16a34a}")
            .append("text{font:14px sans-serif}.detail{font-size:11px}</style>");

        for (int i = 0; i < visibleElements.size(); i++) {
            var element = visibleElements.get(i);
            int x = GAP + i * (NODE_WIDTH + GAP);
            String cssClass = element instanceof FactElement ? "fact" : "dimension";
            svg.append("<rect class=\"").append(cssClass).append("\" x=\"").append(x)
                .append("\" y=\"40\" width=\"").append(NODE_WIDTH).append("\" height=\"")
                .append(NODE_HEIGHT).append("\" rx=\"6\"/>");
            svg.append("<text x=\"").append(x + 10).append("\" y=\"63\">")
                .append(escape(element.getName())).append("</text>");
            if (element instanceof FactElement fact) {
                renderFactDetails(svg, fact, x);
                for (int dimensionIndex = 0; dimensionIndex < fact.getDimensions().size(); dimensionIndex++) {
                    int target = visibleElements.indexOf(fact.getDimensions().get(dimensionIndex));
                    if (target >= 0) {
                        int targetX = GAP + target * (NODE_WIDTH + GAP);
                        svg.append("<line x1=\"").append(x + NODE_WIDTH).append("\" y1=\"58\" x2=\"")
                            .append(targetX).append("\" y2=\"58\" stroke=\"#64748b\"/>");
                    }
                }
            } else if (element instanceof DimensionElement dimension) {
                var levels = dimension.getHierarchies().stream()
                    .flatMap(h -> java.util.stream.Stream.iterate(h.getHead(), level -> level != null,
                        level -> level.getNextLevel()))
                    .map(level -> level.getName())
                    .limit(3)
                    .toList();
                svg.append("<text class=\"detail\" x=\"").append(x + 10).append("\" y=\"95\">")
                    .append(escape(String.join(" - ", levels))).append("</text>");
            }
        }
        return svg.append("</svg>").toString();
    }

    private List<AbstractElement> flattenElements(List<AbstractElement> elements) {
        var result = new ArrayList<AbstractElement>();
        var seen = new HashSet<String>();
        for (var element : elements) {
            if (seen.add(element.getName())) {
                result.add(element);
            }
            if (element instanceof FactElement fact) {
                for (var dimension : fact.getDimensions()) {
                    if (seen.add(dimension.getName())) {
                        result.add(dimension);
                    }
                }
            }
        }
        return result;
    }

    private void renderFactDetails(StringBuilder svg, FactElement fact, int x) {
        String details = String.join(", ", fact.getMeasures());
        if (!fact.getDescriptives().isEmpty()) {
            details = details + (details.isBlank() ? "" : " | ") + String.join(", ", fact.getDescriptives());
        }
        svg.append("<text class=\"detail\" x=\"").append(x + 10).append("\" y=\"95\">")
            .append(escape(details)).append("</text>");
    }

    private String escape(String value) {
        return value.replace("&", "&amp;").replace("<", "&lt;")
            .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }
}
