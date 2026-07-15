package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingDto;

public class BookingDatesValidator
        implements ConstraintValidator<ValidBookingDates, BookingDto> {

    @Override
    public boolean isValid(BookingDto bookingDto,
                           ConstraintValidatorContext context) {

        if (bookingDto == null
                || bookingDto.getStart() == null
                || bookingDto.getEnd() == null) {
            return true;
        }

        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Дата начала аренды не может быть равна дате окончания")
                    .addPropertyNode("end")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
