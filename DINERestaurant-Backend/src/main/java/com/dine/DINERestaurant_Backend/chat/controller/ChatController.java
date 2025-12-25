package com.dine.DINERestaurant_Backend.chat.controller;

import com.dine.DINERestaurant_Backend.auth.jwt.JwtUtil;
import com.dine.DINERestaurant_Backend.chat.dto.SendMessageRequest;
import com.dine.DINERestaurant_Backend.chat.service.ChatService;
import com.dine.DINERestaurant_Backend.user.entity.User;
import com.dine.DINERestaurant_Backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    // ================= USER – MỞ CHAT =================
    @GetMapping("/conversation")
    public ResponseEntity<Integer> getConversation(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Integer userId = Integer.parseInt(jwtUtil.extractUserId(token));

        return ResponseEntity.ok(
                chatService.getOrCreateConversation(userId)
        );
    }

    // ================= USER + ADMIN – LOAD MESSAGES =================
    @GetMapping("/messages/{conversationId}")
    public ResponseEntity<?> getMessages(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer conversationId
    ) {
        String token = authHeader.replace("Bearer ", "");
        Integer userId = Integer.parseInt(jwtUtil.extractUserId(token));

        // (Có thể bổ sung check quyền nếu muốn)
        return ResponseEntity.ok(
                chatService.getMessages(conversationId)
        );
    }

    // ================= USER + ADMIN – GỬI TIN =================
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody SendMessageRequest request
    ) {
        String token = authHeader.replace("Bearer ", "");
        Integer userId = Integer.parseInt(jwtUtil.extractUserId(token));

        // Lấy role từ DB
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        request.setSenderRole(user.getRole()); // customer / admin

        chatService.sendMessage(request);
        return ResponseEntity.ok().build();
    }

    // ================= ADMIN – DANH SÁCH CHAT =================
    @GetMapping("/admin/conversations")
    public ResponseEntity<?> getAllConversations(
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        Integer userId = Integer.parseInt(jwtUtil.extractUserId(token));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        if (!"admin".equalsIgnoreCase(user.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Bạn không có quyền truy cập");
        }

        return ResponseEntity.ok(
                chatService.getAllConversations()
        );
    }
}

