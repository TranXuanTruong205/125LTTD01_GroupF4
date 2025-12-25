package com.dine.adminweb.service;

import com.dine.adminweb.dto.ChatMessageDto;
import com.dine.adminweb.dto.ConversationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
@Service
@RequiredArgsConstructor
public class ChatApiService {

    private final RestTemplate restTemplate;

    private static final String CHAT_API = "http://localhost:8080/api/chat";

    public List<ConversationDto> getAllConversations(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<ConversationDto>> response =
                restTemplate.exchange(
                        CHAT_API + "/admin/conversations",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<>() {}
                );

        return response.getBody();
    }
}
