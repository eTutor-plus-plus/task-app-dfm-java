package at.jku.dke.task_app.dfm.models.ast;

import at.jku.dke.task_app.dfm.models.enums.ElementType;

/**
 * Base type for elements in a dimensional fact model.
 */
public abstract class AbstractElement {
    private final String name;
    private final ElementType elementType;

    protected AbstractElement(String name, ElementType elementType) {
        this.name = name;
        this.elementType = elementType;
    }

    public String getName() {
        return name;
    }

    public ElementType getElementType() {
        return elementType;
    }

    public abstract boolean structurallyEquals(AbstractElement other);
}
