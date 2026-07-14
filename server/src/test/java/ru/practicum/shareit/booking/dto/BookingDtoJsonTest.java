package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.BookedItemDto;
import ru.practicum.shareit.user.dto.UserBookerDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serialize_writesDatesInIsoFormatAndStatusAsString() throws Exception {
        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2026, 8, 1, 12, 30, 45))
                .end(LocalDateTime.of(2026, 8, 2, 10, 15, 30))
                .itemId(5L)
                .status(Status.WAITING)
                .item(BookedItemDto.builder().id(5L).name("Дрель").build())
                .booker(UserBookerDto.builder().id(2L).build())
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2026-08-01T12:30:45");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2026-08-02T10:15:30");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(2);
    }

    @Test
    void deserialize_readsIsoDatesIntoLocalDateTime() throws Exception {
        String content = "{\"id\":1,\"start\":\"2026-08-01T12:30:45\",\"end\":\"2026-08-02T10:15:30\","
                + "\"itemId\":5,\"status\":\"APPROVED\"}";

        BookingDto result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2026, 8, 1, 12, 30, 45));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2026, 8, 2, 10, 15, 30));
        assertThat(result.getItemId()).isEqualTo(5L);
        assertThat(result.getStatus()).isEqualTo(Status.APPROVED);
    }

}
