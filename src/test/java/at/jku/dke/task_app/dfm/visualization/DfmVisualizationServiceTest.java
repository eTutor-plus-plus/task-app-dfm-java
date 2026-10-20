package at.jku.dke.task_app.dfm.visualization;

import at.jku.dke.task_app.dfm.parser.DfmParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DfmVisualizationServiceTest {
    @Test
    void rendersEscapedDfmNamesAsSvg() {
        var elements = new DfmParser().parse("fact Sales { profit; };");
        var svg = new DfmVisualizationService().render(elements);

        assertTrue(svg.startsWith("<svg "));
        assertTrue(svg.contains("Sales"));
        assertTrue(svg.endsWith("</svg>"));
    }
}
