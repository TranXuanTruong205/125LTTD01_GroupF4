package com.dinerestaurant.app.ui.other;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.other.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageFragment extends AppCompatActivity {

    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private EditText edtMessage;
    private ImageView btnSend, btnBack, btnAdd;

    private List<ChatMessage> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_message);

        // Init views
        chatContainer = findViewById(R.id.chatContainer);
        scrollView = findViewById(R.id.scrollView);
        edtMessage = findViewById(R.id.edtMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        btnAdd = findViewById(R.id.btnAdd);

        // Init message list
        messageList = new ArrayList<>();

        // Xóa tin nhắn mẫu trong XML
        chatContainer.removeAllViews();

        // Load tin nhắn mẫu
        loadSampleMessages();

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Add button (có thể thêm chức năng đính kèm file, ảnh)
        btnAdd.setOnClickListener(v -> {
            Toast.makeText(this, "Attach file feature", Toast.LENGTH_SHORT).show();
        });

        // Send button
        btnSend.setOnClickListener(v -> sendMessage());

        // Focus vào EditText để mở bàn phím
        edtMessage.requestFocus();
    }

    private void loadSampleMessages() {
        // Tin nhắn từ admin
        addMessageToUI(new ChatMessage("Hello!", "10:10", false, true));

        // Tin nhắn của mình
        addMessageToUI(new ChatMessage("Hi!", "10:11", true, true));
        addMessageToUI(new ChatMessage("Great! 😊", "10:12", true, true));
    }

    private void sendMessage() {
        String messageText = edtMessage.getText().toString().trim();

        if (messageText.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lấy thời gian hiện tại
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Tạo tin nhắn mới
        ChatMessage newMessage = new ChatMessage(messageText, currentTime, true, false);
        messageList.add(newMessage);

        // Thêm tin nhắn vào UI
        addMessageToUI(newMessage);

        // Xóa nội dung EditText
        edtMessage.setText("");

        // Cuộn xuống cuối
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

        // TODO: Gửi tin nhắn lên server ở đây
    }

    private void addMessageToUI(ChatMessage message) {
        // Tạo container cho tin nhắn
        LinearLayout messageContainer = new LinearLayout(this);
        messageContainer.setOrientation(LinearLayout.VERTICAL);
        messageContainer.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));

        LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerParams.topMargin = dpToPx(10);

        if (message.isSentByMe()) {
            // Tin nhắn của mình (bên phải)
            containerParams.gravity = android.view.Gravity.END;
            messageContainer.setBackgroundResource(R.drawable.bg_msg_right);
        } else {
            // Tin nhắn của người khác (bên trái)
            containerParams.gravity = android.view.Gravity.START;
            messageContainer.setBackgroundResource(R.drawable.bg_msg_left);
        }

        messageContainer.setLayoutParams(containerParams);

        // TextView cho nội dung tin nhắn
        TextView tvMessage = new TextView(this);
        tvMessage.setText(message.getMessage());
        tvMessage.setTextSize(16);
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
        TextView tvTime = new TextView(this);
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
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}