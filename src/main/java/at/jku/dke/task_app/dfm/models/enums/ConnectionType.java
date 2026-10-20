package at.jku.dke.task_app.dfm.models.enums;

/**
 * The relationship between adjacent levels or between a fact and a dimension.
 */
public enum ConnectionType {
    SIMPLE("-"),
    MULTIPLE("="),
    CONVERGENCE("->");

    private final String symbol;

    ConnectionType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
