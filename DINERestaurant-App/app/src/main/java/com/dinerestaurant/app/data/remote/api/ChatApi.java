package com.dinerestaurant.app.data.remote.api;


import com.dinerestaurant.app.data.remote.dto.ChatMessageDto;
import com.dinerestaurant.app.data.remote.dto.SendMessageRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface ChatApi {

    // GET /api/chat/conversation
    @GET("api/chat/conversation")
    Call<Integer> getConversation();

    // GET /api/chat/messages/{conversationId}
    @GET("api/chat/messages/{conversationId}")
    Call<List<ChatMessageDto>> getMessages(
            @Path("conversationId") int conversationId
    );

    // POST /api/chat/send
    @POST("api/chat/send")
    Call<Void> sendMessage(
            @Body SendMessageRequest request
    );
}
