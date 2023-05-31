package com.query.builder.validation;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, Enum<?>> {

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        // Initialization, if needed
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            return false; // Null values are considered invalid
        }
        String stringValue = value.toString().trim();
        return stringValue.length() > 0; // Validate non-blank values
    }
}
