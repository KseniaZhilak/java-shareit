package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class RequestServiceImplIntegrationTest {

    @Autowired
    private RequestService requestService;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User requestor;
    private User owner;

    @BeforeEach
    void setUp() {
        requestor = userRepository.save(User.builder().name("Alice").email("alice@example.com").build());
        owner = userRepository.save(User.builder().name("Bob").email("bob@example.com").build());
    }

    private Request saveRequest(User user, String description, LocalDateTime created) {
        return requestRepository.save(Request.builder()
                .description(description)
                .requestor(user)
                .created(created)
                .build());
    }

    private Item saveItem(User itemOwner, String name, Request request) {
        return itemRepository.save(Item.builder()
                .owner(itemOwner)
                .name(name)
                .description(name + " description")
                .available(true)
                .request(request)
                .build());
    }

    @Test
    void createRequest_savesRequestToDatabase() {
        CreateRequestDto dto = CreateRequestDto.builder().description("Нужна дрель").build();

        CreateRequestDto created = requestService.createRequest(requestor.getId(), dto);

        assertNotNull(created.getId());
        assertNotNull(created.getCreated());
        Request saved = requestRepository.findById(created.getId()).orElseThrow();
        assertEquals("Нужна дрель", saved.getDescription());
        assertEquals(requestor.getId(), saved.getRequestor().getId());
    }

    @Test
    void createRequest_unknownUser_throwsNotFoundException() {
        CreateRequestDto dto = CreateRequestDto.builder().description("Нужна дрель").build();

        assertThrows(NotFoundException.class, () -> requestService.createRequest(9999L, dto));
    }

    @Test
    void getOwnRequests_returnsOnlyOwnRequestsWithAnswersSortedByCreatedDesc() {
        Request oldRequest = saveRequest(requestor, "Нужна дрель", LocalDateTime.now().minusDays(2));
        Request recentRequest = saveRequest(requestor, "Нужна отвёртка", LocalDateTime.now().minusDays(1));
        saveRequest(owner, "Нужен молоток", LocalDateTime.now());
        Item answer = saveItem(owner, "Дрель", oldRequest);

        List<RequestDto> result = requestService.getOwnRequests(requestor.getId());

        assertEquals(2, result.size());
        assertEquals(recentRequest.getId(), result.get(0).getId());
        assertEquals(oldRequest.getId(), result.get(1).getId());
        assertTrue(result.get(0).getItems().isEmpty());
        assertEquals(1, result.get(1).getItems().size());
        assertEquals(answer.getId(), result.get(1).getItems().get(0).getItem());
        assertEquals("Дрель", result.get(1).getItems().get(0).getName());
    }

    @Test
    void getAllRequests_returnsOnlyOtherUsersRequests() {
        saveRequest(requestor, "Нужна дрель", LocalDateTime.now().minusDays(1));
        Request otherRequest = saveRequest(owner, "Нужен молоток", LocalDateTime.now());

        List<RequestDto> result = requestService.getAllRequests(requestor.getId());

        assertEquals(1, result.size());
        assertEquals(otherRequest.getId(), result.get(0).getId());
        assertEquals("Нужен молоток", result.get(0).getDescription());
    }

    @Test
    void getRequestById_returnsRequestWithAnswer() {
        Request request = saveRequest(requestor, "Нужна дрель", LocalDateTime.now());
        Item answer = saveItem(owner, "Дрель", request);

        RequestDto result = requestService.getRequestById(request.getId(), requestor.getId());

        assertEquals(request.getId(), result.getId());
        assertEquals("Нужна дрель", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals(answer.getId(), result.getItems().get(0).getItem());
        assertEquals(owner.getId(), result.getItems().get(0).getRequestor());
    }

    @Test
    void getOwnRequests_unknownUser_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> requestService.getOwnRequests(9999L));
    }

    @Test
    void getAllRequests_unknownUser_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> requestService.getAllRequests(9999L));
    }

    @Test
    void getRequestById_unknownRequest_throwsNotFoundException() {
        assertThrows(NotFoundException.class, () -> requestService.getRequestById(9999L, requestor.getId()));
    }

    @Test
    void getRequestById_requestWithoutAnswer_throwsNotFoundException() {
        Request request = saveRequest(requestor, "Нужна дрель", LocalDateTime.now());

        assertThrows(NotFoundException.class,
                () -> requestService.getRequestById(request.getId(), requestor.getId()));
    }

}
