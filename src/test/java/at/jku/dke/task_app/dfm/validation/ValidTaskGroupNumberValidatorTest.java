package at.jku.dke.task_app.dfm.validation;

import at.jku.dke.task_app.dfm.dto.ModifyDfmTaskGroupDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidTaskGroupNumberValidatorTest {

    @Test
    void isValidCorrectOrder() {
        // Arrange
        ValidTaskGroupNumberValidator validTaskGroupNumberValidator = new ValidTaskGroupNumberValidator();
        ModifyDfmTaskGroupDto modifyDfmTaskGroupDto = new ModifyDfmTaskGroupDto(1, 2);

        // Act
        boolean result = validTaskGroupNumberValidator.isValid(modifyDfmTaskGroupDto, null);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidSameValue() {
        // Arrange
        ValidTaskGroupNumberValidator validTaskGroupNumberValidator = new ValidTaskGroupNumberValidator();
        ModifyDfmTaskGroupDto modifyDfmTaskGroupDto = new ModifyDfmTaskGroupDto(2, 2);

        // Act
        boolean result = validTaskGroupNumberValidator.isValid(modifyDfmTaskGroupDto, null);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidIncorrectOrder() {
        // Arrange
        ValidTaskGroupNumberValidator validTaskGroupNumberValidator = new ValidTaskGroupNumberValidator();
        ModifyDfmTaskGroupDto modifyDfmTaskGroupDto = new ModifyDfmTaskGroupDto(2, 1);

        // Act
        boolean result = validTaskGroupNumberValidator.isValid(modifyDfmTaskGroupDto, null);

        // Assert
        assertFalse(result);
    }
}
