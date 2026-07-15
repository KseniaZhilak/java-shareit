package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void serialize_writesCreatedDateInIsoFormat() throws Exception {
        CommentDto dto = CommentDto.builder()
                .id(1L)
                .text("Отличная дрель")
                .authorName("Bob")
                .created(LocalDateTime.of(2026, 7, 14, 12, 30, 45))
                .build();

        JsonContent<CommentDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Отличная дрель");
        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("Bob");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-07-14T12:30:45");
    }

    @Test
    void deserialize_readsCreatedDateIntoLocalDateTime() throws Exception {
        String content = "{\"id\":1,\"text\":\"Отличная дрель\",\"authorName\":\"Bob\","
                + "\"created\":\"2026-07-14T12:30:45\"}";

        CommentDto result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getText()).isEqualTo("Отличная дрель");
        assertThat(result.getAuthorName()).isEqualTo("Bob");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2026, 7, 14, 12, 30, 45));
    }

}
