package com.dinerestaurant.app.ui.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // Đổi từ AppCompatActivity sang Fragment

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.data.remote.api.ApiClient;
import com.dinerestaurant.app.data.remote.api.ChatApi;
import com.dinerestaurant.app.data.remote.dto.ChatMessageDto;
import com.dinerestaurant.app.data.remote.dto.SendMessageRequest;
import com.dinerestaurant.app.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Lớp này đã được đổi thành kế thừa từ Fragment
public class MessageFragment extends Fragment {

    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private EditText edtMessage;
    private ImageView btnSend, btnBack, btnAdd;

    private List<ChatMessage> messageList;
    private ChatApi chatApi;
    private int conversationId = -1;

    // Sử dụng onCreateView để khởi tạo Fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        chatApi = ApiClient.getChatApi();

        // 1. Inflate layout
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        // 2. Init views (Sử dụng view.findViewById)
        chatContainer = view.findViewById(R.id.chatContainer);
        scrollView = view.findViewById(R.id.scrollView);
        edtMessage = view.findViewById(R.id.edtMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnBack = view.findViewById(R.id.btnBack);
        btnAdd = view.findViewById(R.id.btnAdd);

        // 3. Init message list
        messageList = new ArrayList<>();

        // Xóa tin nhắn mẫu trong XML
        chatContainer.removeAllViews();


        // 5. Back button
        btnBack.setOnClickListener(v -> {
            // Khi ở Fragment, nhấn Back tương đương với pop back stack
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        // 6. Add button
        btnAdd.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Attach file feature", Toast.LENGTH_SHORT).show();
        });

        // 7. Send button
        btnSend.setOnClickListener(v -> sendMessage());

        // 8. Focus vào EditText để mở bàn phím
        edtMessage.requestFocus();
        initConversation();

        return view;
    }


    private void initConversation() {
        chatApi.getConversation().enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    conversationId = response.body();
                    loadMessages();
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(getContext(), "Không tạo được chat", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadMessages() {
        chatApi.getMessages(conversationId).enqueue(new Callback<List<ChatMessageDto>>() {
            @Override
            public void onResponse(Call<List<ChatMessageDto>> call,
                                   Response<List<ChatMessageDto>> response) {

                if (!response.isSuccessful() || response.body() == null) return;

                chatContainer.removeAllViews();

                for (ChatMessageDto dto : response.body()) {
                    boolean isMe = "customer".equalsIgnoreCase(dto.getSenderRole());

                    ChatMessage msg = new ChatMessage(
                            dto.getContent(),
                            formatTime(dto.getCreatedAt()),
                            isMe,
                            true
                    );
                    addMessageToUI(msg);
                }

                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
            }

            @Override
            public void onFailure(Call<List<ChatMessageDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Không tải được tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String formatTime(String createdAt) {
        try {
            SimpleDateFormat inputFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS", Locale.getDefault());

            Date date = inputFormat.parse(createdAt);

            SimpleDateFormat outputFormat =
                    new SimpleDateFormat("HH:mm", Locale.getDefault());

            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void sendMessage() {
        String text = edtMessage.getText().toString().trim();
        if (text.isEmpty() || conversationId == -1) return;

        SendMessageRequest request =
                new SendMessageRequest(conversationId, text);

        chatApi.sendMessage(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                edtMessage.setText("");
                loadMessages(); // reload
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Gửi tin thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ResourceType")
    private void addMessageToUI(ChatMessage message) {
        if (getContext() == null) return;

        // Sử dụng requireContext() hoặc getContext()
        Context context = requireContext();

        // Tạo container cho tin nhắn
        LinearLayout messageContainer = new LinearLayout(context);
        messageContainer.setOrientation(LinearLayout.VERTICAL);
        messageContainer.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.topMargin = dpToPx(10);

        if (message.isSentByMe()) {
            // Tin nhắn của mình (bên phải)
            containerParams.gravity = Gravity.END; // Đã đổi thành Gravity.END
            messageContainer.setBackgroundResource(R.drawable.bg_msg_right);
        } else {
            // Tin nhắn của người khác (bên trái)
            containerParams.gravity = Gravity.START; // Đã đổi thành Gravity.START
            messageContainer.setBackgroundResource(R.drawable.bg_msg_left);
        }

        messageContainer.setLayoutParams(containerParams);

        // TextView cho nội dung tin nhắn
        TextView tvMessage = new TextView(context);
        tvMessage.setText(message.getMessage());
        tvMessage.setTextSize(16);
        // Lấy màu từ Resources (sử dụng getResources() của Fragment)
        tvMessage.setTextColor(getResources().getColor(
                message.isSentByMe() ? R.color.white : R.color.black
        ));

        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        messageParams.bottomMargin = dpToPx(6);
        tvMessage.setLayoutParams(messageParams);

        // TextView cho thời gian
        TextView tvTime = new TextView(context);
        String timeText = message.getTime();
        if (message.isSentByMe()) {
            timeText += message.isRead() ? " ✓✓" : " ✓";
            tvTime.setTextColor(getResources().getColor(R.color.white));
        } else {
            tvTime.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
        tvTime.setText(timeText);
        tvTime.setTextSize(12);

        // Thêm vào container
        messageContainer.addView(tvMessage);
        messageContainer.addView(tvTime);

        // Thêm vào chat container
        chatContainer.addView(messageContainer);
    }

    private int dpToPx(int dp) {
        if (getResources() == null) return 0;
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}