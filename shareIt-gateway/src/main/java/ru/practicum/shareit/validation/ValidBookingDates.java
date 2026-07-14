package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BookingDatesValidator.class)
@Documented
public @interface ValidBookingDates {

    String message() default "Дата начала аренды не может быть равна дате окончания";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
