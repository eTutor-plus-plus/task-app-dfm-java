package at.jku.dke.task_app.dfm.data.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DfmTaskGroupValidationTest {
    @Test
    void acceptsAscendingNumberRange() {
        var group = new DfmTaskGroup(1, 10);

        assertTrue(group.isNumberRangeValid());
    }

    @Test
    void rejectsDescendingNumberRange() {
        var group = new DfmTaskGroup(10, 1);

        assertFalse(group.isNumberRangeValid());
    }

    @Test
    void acceptsEqualNumberRange() {
        var group = new DfmTaskGroup(5, 5);

        assertTrue(group.isNumberRangeValid());
    }
}
