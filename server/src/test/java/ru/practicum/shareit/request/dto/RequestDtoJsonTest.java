package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class RequestDtoJsonTest {

    @Autowired
    private JacksonTester<RequestDto> json;

    @Test
    void serialize_writesCreatedDateInIsoFormatAndAnswers() throws Exception {
        RequestDto dto = RequestDto.builder()
                .id(1L)
                .description("Нужна дрель")
                .created(LocalDateTime.of(2026, 7, 14, 12, 30, 45))
                .items(List.of(ItemRequestDto.builder().item(5L).name("Дрель").requestor(2L).build()))
                .build();

        JsonContent<RequestDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Нужна дрель");
        assertThat(result).extractingJsonPathStringValue("$.created").isEqualTo("2026-07-14T12:30:45");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].item").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathNumberValue("$.items[0].requestor").isEqualTo(2);
    }

    @Test
    void deserialize_readsCreatedDateIntoLocalDateTime() throws Exception {
        String content = "{\"id\":1,\"description\":\"Нужна дрель\",\"created\":\"2026-07-14T12:30:45\","
                + "\"items\":[{\"item\":5,\"name\":\"Дрель\",\"requestor\":2}]}";

        RequestDto result = json.parseObject(content);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Нужна дрель");
        assertThat(result.getCreated()).isEqualTo(LocalDateTime.of(2026, 7, 14, 12, 30, 45));
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getName()).isEqualTo("Дрель");
    }

}
