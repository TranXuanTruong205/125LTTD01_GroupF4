package com.dine.DINERestaurant_Backend.chat.service;

import com.dine.DINERestaurant_Backend.chat.dto.ChatMessageResponse;
import com.dine.DINERestaurant_Backend.chat.dto.SendMessageRequest;
import com.dine.DINERestaurant_Backend.chat.entity.ChatConversation;
import com.dine.DINERestaurant_Backend.chat.entity.ChatMessage;
import com.dine.DINERestaurant_Backend.chat.repository.ChatConversationRepository;
import com.dine.DINERestaurant_Backend.chat.repository.ChatMessageRepository;
import com.dine.DINERestaurant_Backend.user.entity.User;
import com.dine.DINERestaurant_Backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatConversationRepository conversationRepo;
    private final ChatMessageRepository messageRepo;
    private final UserRepository userRepo;

    // 1️⃣ Lấy hoặc tạo conversation
    public Integer getOrCreateConversation(Integer userId) {
        return conversationRepo.findByUser_UserId(userId)
                .orElseGet(() -> {
                    User user = userRepo.findById(userId)
                            .orElseThrow();

                    ChatConversation c = new ChatConversation();
                    c.setUser(user);
                    c.setUpdatedAt(LocalDateTime.now());
                    return conversationRepo.save(c);
                }).getConversationId();
    }

    // 2️⃣ Lấy tin nhắn
    public List<ChatMessageResponse> getMessages(Integer conversationId) {
        return messageRepo
                .findByConversation_ConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(m -> new ChatMessageResponse(
                        m.getSenderRole(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();
    }

    // 3️⃣ Gửi tin nhắn
    public void sendMessage(SendMessageRequest req) {

        ChatConversation conversation = conversationRepo
                .findById(req.getConversationId())
                .orElseThrow();

        ChatMessage msg = new ChatMessage();
        msg.setConversation(conversation);
        msg.setSenderRole(req.getSenderRole());
        msg.setContent(req.getContent());

        messageRepo.save(msg);

        conversation.setLastMessage(req.getContent());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepo.save(conversation);
    }

    // 4️⃣ Admin xem danh sách chat
    public List<ChatConversation> getAllConversations() {
        return conversationRepo.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
    }
}
