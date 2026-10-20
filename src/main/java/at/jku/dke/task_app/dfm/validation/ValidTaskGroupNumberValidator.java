package at.jku.dke.task_app.dfm.validation;

import at.jku.dke.task_app.dfm.dto.ModifyDfmTaskGroupDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Custom validator for numbers in {@link ModifyDfmTaskGroupDto}.
 */
public class ValidTaskGroupNumberValidator implements ConstraintValidator<ValidTaskGroupNumber, ModifyDfmTaskGroupDto> {
    /**
     * Creates a new instance of class Valid task group number validator.
     */
    public ValidTaskGroupNumberValidator() {
    }

    @Override
    public boolean isValid(ModifyDfmTaskGroupDto value, ConstraintValidatorContext context) {
        return value != null
            && value.minNumber() != null
            && value.maxNumber() != null
            && value.minNumber() <= value.maxNumber();
    }
}
