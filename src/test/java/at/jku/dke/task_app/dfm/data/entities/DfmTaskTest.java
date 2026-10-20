package at.jku.dke.task_app.dfm.data.entities;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DfmTaskTest {

    @Test
    void testConstructor1() {
        // Arrange
        final String expected = "fact Sales { profit; };";

        // Act
        var task = new DfmTask(expected);
        String actual = task.getSolution();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void testConstructor2() {
        // Arrange
        final String expected = "fact Sales { profit; };";
        final BigDecimal maxPoints = BigDecimal.TEN;
        final TaskStatus status = TaskStatus.APPROVED;
        final DfmTaskGroup taskGroup = new DfmTaskGroup();
        taskGroup.setId(55L);

        // Act
        var task = new DfmTask(maxPoints, status, taskGroup, expected);
        String actualSolution = task.getSolution();
        BigDecimal actualMaxPoints = task.getMaxPoints();
        TaskStatus actualStatus = task.getStatus();
        DfmTaskGroup actualTaskGroup = task.getTaskGroup();

        // Assert
        assertEquals(expected, actualSolution);
        assertEquals(maxPoints, actualMaxPoints);
        assertEquals(status, actualStatus);
        assertEquals(taskGroup, actualTaskGroup);
    }

    @Test
    void testConstructor3() {
        // Arrange
        final String expected = "fact Sales { profit; };";
        final BigDecimal maxPoints = BigDecimal.TEN;
        final TaskStatus status = TaskStatus.APPROVED;
        final DfmTaskGroup taskGroup = new DfmTaskGroup();
        taskGroup.setId(55L);
        final long id = 1L;

        // Act
        var task = new DfmTask(id, maxPoints, status, taskGroup, expected);
        long actualId = task.getId();
        String actualSolution = task.getSolution();
        BigDecimal actualMaxPoints = task.getMaxPoints();
        TaskStatus actualStatus = task.getStatus();
        DfmTaskGroup actualTaskGroup = task.getTaskGroup();

        // Assert
        assertEquals(id, actualId);
        assertEquals(expected, actualSolution);
        assertEquals(maxPoints, actualMaxPoints);
        assertEquals(status, actualStatus);
        assertEquals(taskGroup, actualTaskGroup);
    }

    @Test
    void testGetSetSolution() {
        // Arrange
        var task = new DfmTask();
        final String expected = "fact Sales { profit; };";

        // Act
        task.setSolution(expected);
        final String actual = task.getSolution();

        // Assert
        assertEquals(expected, actual);
    }

}
