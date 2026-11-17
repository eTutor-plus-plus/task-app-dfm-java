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
    void parseAcceptsLineComments() {
        var response = controller.parse("""
            // model
            fact Sales { Revenue; }; // measure
            dimension Time { Day - Month; };
            Sales - Time; // connection
            """);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertInstanceOf(DfmGraphDto.class, response.getBody());
    }

    @Test
    void parseReturnsConnectionLabelsInGraph() {
        var response = controller.parse("""
            fact Sales { Revenue; };
            dimension Customer { customer -[customercity]- city; };
            Sales - Customer;
            """);

        var graph = assertInstanceOf(DfmGraphDto.class, response.getBody());
        assertTrue(graph.edges().stream().anyMatch(e -> "customercity".equals(e.label())));
    }

    @Test
    void parseReturnsParallelFactConnections() {
        var response = controller.parse("""
            fact Invoice { quantity; };
            dimension Customer { customer; };
            Invoice (-[shipping]-) Customer;
            Invoice =[billing]= Customer;
            """);
        var graph = assertInstanceOf(DfmGraphDto.class, response.getBody());
        var shipping = graph.edges().stream().filter(e -> "shipping".equals(e.label())).findFirst().orElseThrow();
        var billing = graph.edges().stream().filter(e -> "billing".equals(e.label())).findFirst().orElseThrow();
        assertEquals(shipping.source(), billing.source());
        assertEquals(shipping.target(), billing.target());
        assertTrue(shipping.optional());
        assertEquals("MULTIPLE", billing.connectionType());
    }

    @Test
    void parseReturnsRecursiveHierarchyAsSelfLoop() {
        var response = controller.parse("""
            fact Sales { profit; };
            dimension SalesRepresentative { salesRepresentative -[supervisor]-> salesRepresentative; };
            Sales - SalesRepresentative;
            """);

        var graph = assertInstanceOf(DfmGraphDto.class, response.getBody());
        var loop = graph.edges().stream().filter(e -> "supervisor".equals(e.label())).findFirst().orElseThrow();
        assertEquals(loop.source(), loop.target());
        assertEquals("CONVERGENCE", loop.connectionType());
    }

    @Test
    void parseReturnsSharedAndCrossNodes() {
        var response = controller.parse("""
            fact Sales { profit; };
            hierarchy VAT { VAT - vatClass; };
            hierarchy city { city - country - {cross} VAT; };
            dimension city { {shared} city; };
            dimension customer { customer -[customerCity]- {shared} city; };
            dimension product { product - category - {cross} VAT; };
            Sales -[storeCity]- city;
            Sales - customer;
            Sales - product;
            """);

        var graph = assertInstanceOf(DfmGraphDto.class, response.getBody());
        assertTrue(graph.nodes().stream().anyMatch(n -> n.label().equals("city")
            && n.type() == DfmGraphDto.NodeType.SHARED));
        var vat = graph.nodes().stream().filter(n -> n.label().equals("VAT")).findFirst().orElseThrow();
        assertEquals(DfmGraphDto.NodeType.CROSS, vat.type());
        assertEquals(2, graph.edges().stream().filter(e -> e.target().equals(vat.id())).count());
        assertTrue(graph.edges().stream().anyMatch(e -> "storeCity".equals(e.label())));
    }

    @Test
    void parseRejectsHierarchyWithASingleIncomingConnection() {
        var response = controller.parse("hierarchy h { a; };\ndimension d {\n  x - {shared} h;\n};");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var error = assertInstanceOf(DfmParseErrorDto.class, response.getBody());
        assertTrue(error.message().contains("only one connection"));
        assertEquals(3, error.line());
        assertEquals(16, error.column());
    }

    @Test
    void parseAcceptsOneReferenceWithSeveralRolesFromTheFact() {
        var response = controller.parse("""
            fact Invoice { quantity; };
            hierarchy Customer { Customer - City; };
            dimension Customer { {shared} Customer; };
            Invoice -[shipping]- Customer;
            Invoice -[billing]- Customer;
            """);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        var graph = assertInstanceOf(DfmGraphDto.class, response.getBody());
        assertEquals(2, graph.edges().stream().filter(e -> e.label() != null).count());
    }

    @Test
    void parseReturnsPositionForUndeclaredHierarchy() {
        var response = controller.parse("dimension d { x - {shared} missing; };");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var error = assertInstanceOf(DfmParseErrorDto.class, response.getBody());
        assertTrue(error.message().contains("Hierarchy missing is not declared"));
        assertEquals(1, error.line());
        assertEquals(28, error.column());
    }

    @Test
    void parseRejectsMultipleRecursiveConnection() {
        var response = controller.parse("dimension D {\n  a =[role]= a;\n};");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var error = assertInstanceOf(DfmParseErrorDto.class, response.getBody());
        assertTrue(error.message().contains("recursive connection cannot be multiple"));
        assertEquals(2, error.line());
        assertEquals(14, error.column());
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
