package com.dinerestaurant.app.ui.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color; // Thêm import Color
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // Đổi từ AppCompatActivity sang Fragment

import com.dinerestaurant.app.R;
import com.dinerestaurant.app.model.other.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Lớp này đã được đổi thành kế thừa từ Fragment
public class MessageFragment extends Fragment {

    private LinearLayout chatContainer;
    private ScrollView scrollView;
    private EditText edtMessage;
    private ImageView btnSend, btnBack, btnAdd;

    private List<ChatMessage> messageList;

    // Sử dụng onCreateView để khởi tạo Fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

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

        // 4. Load tin nhắn mẫu
        loadSampleMessages();

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

        return view;
    }

    // Tất cả các phương thức hỗ trợ phải được cập nhật để sử dụng Context/Resources đúng cách

    private void loadSampleMessages() {
        // Tin nhắn từ admin
        addMessageToUI(new ChatMessage("Hello!", "10:10", false, true));

        // Tin nhắn của mình
        addMessageToUI(new ChatMessage("Hi!", "10:11", true, true));
        addMessageToUI(new ChatMessage("Great! 😊", "10:12", true, true));
    }

    private void sendMessage() {
        if (getContext() == null) return;

        String messageText = edtMessage.getText().toString().trim();

        if (messageText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
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