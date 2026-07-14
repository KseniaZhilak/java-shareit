package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.CreateRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.RequestMapper.*;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public CreateRequestDto createRequest(Long userId, CreateRequestDto createDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Request request = toRequest(createDto);
        request.setRequestor(user);

        Request saved = requestRepository.save(request);
        return toCreatedRequestDto(saved);
    }

    @Override
    public List<RequestDto> getOwnRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Request> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        List<Long> requestIds = requests.stream()
                .map(Request::getId)
                .toList();
        Map<Long, List<Item>> itemsByRequestId = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> toRequestDto(request, itemsByRequestId.getOrDefault(request.getId(), List.of())))
                .toList();
    }

    @Override
    public List<RequestDto> getAllRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<Request> requests = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);
        List<Long> requestIds = requests.stream()
                .map(Request::getId)
                .toList();
        Map<Long, List<Item>> itemsByRequestId = itemRepository.findAllByRequestIdIn(requestIds).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        return requests.stream()
                .map(request -> toRequestDto(request, itemsByRequestId.getOrDefault(request.getId(), List.of())))
                .toList();
    }

    @Override
    public RequestDto getRequestById(Long id, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Request not found"));
        Item item = itemRepository.findByRequestId(id)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        return toRequestDto(request, item);
    }
}
