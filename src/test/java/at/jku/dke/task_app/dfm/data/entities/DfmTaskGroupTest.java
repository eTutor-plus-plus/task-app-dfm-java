package at.jku.dke.task_app.dfm.data.entities;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DfmTaskGroupTest {

    @Test
    void testConstructor1() {
        // Arrange
        final int expectedMinNumber = 21;
        final int expectedMaxNumber = 42;

        // Act
        DfmTaskGroup dfmTaskGroup = new DfmTaskGroup(expectedMinNumber, expectedMaxNumber);
        int actualMinNumber = dfmTaskGroup.getMinNumber();
        int actualMaxNumber = dfmTaskGroup.getMaxNumber();

        // Assert
        assertEquals(expectedMinNumber, actualMinNumber);
        assertEquals(expectedMaxNumber, actualMaxNumber);
    }

    @Test
    void testConstructor2() {
        // Arrange
        final TaskStatus status = TaskStatus.APPROVED;
        final int expectedMinNumber = 21;
        final int expectedMaxNumber = 42;

        // Act
        DfmTaskGroup dfmTaskGroup = new DfmTaskGroup(status, expectedMinNumber, expectedMaxNumber);
        TaskStatus actualStatus = dfmTaskGroup.getStatus();
        int actualMinNumber = dfmTaskGroup.getMinNumber();
        int actualMaxNumber = dfmTaskGroup.getMaxNumber();

        // Assert
        assertEquals(status, actualStatus);
        assertEquals(expectedMinNumber, actualMinNumber);
        assertEquals(expectedMaxNumber, actualMaxNumber);
    }

    @Test
    void testConstructor3() {
        // Arrange
        final long expectedId = 21;
        final TaskStatus status = TaskStatus.APPROVED;
        final int expectedMinNumber = 21;
        final int expectedMaxNumber = 42;

        // Act
        DfmTaskGroup dfmTaskGroup = new DfmTaskGroup(expectedId, status, expectedMinNumber, expectedMaxNumber);
        long actualId = dfmTaskGroup.getId();
        TaskStatus actualStatus = dfmTaskGroup.getStatus();
        int actualMinNumber = dfmTaskGroup.getMinNumber();
        int actualMaxNumber = dfmTaskGroup.getMaxNumber();

        // Assert
        assertEquals(expectedId, actualId);
        assertEquals(status, actualStatus);
        assertEquals(expectedMinNumber, actualMinNumber);
        assertEquals(expectedMaxNumber, actualMaxNumber);
    }

    @Test
    void testGetSetMinNumber() {
        // Arrange
        DfmTaskGroup dfmTaskGroup = new DfmTaskGroup();
        final int expected = 21;

        // Act
        dfmTaskGroup.setMinNumber(expected);
        int actual = dfmTaskGroup.getMinNumber();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void testGetSetMaxNumber() {
        // Arrange
        DfmTaskGroup dfmTaskGroup = new DfmTaskGroup();
        final int expected = 21;

        // Act
        dfmTaskGroup.setMaxNumber(expected);
        int actual = dfmTaskGroup.getMaxNumber();

        // Assert
        assertEquals(expected, actual);
    }

}
