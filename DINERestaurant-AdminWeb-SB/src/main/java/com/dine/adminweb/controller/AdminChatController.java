package com.dine.adminweb.controller;

import com.dine.adminweb.service.ChatApiService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/chat")
@RequiredArgsConstructor
public class AdminChatController {

    private final ChatApiService chatApiService;

    private static final String HARDCODE_ADMIN_TOKEN =
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzY2NjQ5MDA0LCJleHAiOjE3NjY3MzU0MDR9.aEtjNx_FggffL7_Mj2QfVm04ubxUEGrDobGbt-3kA1w";

    @GetMapping
    public String chatPage(Model model) {

        model.addAttribute("pageTitle", "Messages");

        model.addAttribute(
                "conversations",
                chatApiService.getAllConversations(HARDCODE_ADMIN_TOKEN)
        );

        model.addAttribute("adminToken", HARDCODE_ADMIN_TOKEN);

        model.addAttribute("content", "chat/content");

        return "layout/main";
    }

}
