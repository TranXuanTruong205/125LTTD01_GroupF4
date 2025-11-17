package com.dinerestaurant.app.activity.other;

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

import androidx.fragment.app.Fragment;

import com.dinerestaurant.app.R;

public class MessageFragment extends Fragment {

    private ImageView btnBack, btnSend;
    private EditText edtMessage;
    private LinearLayout chatContainer;
    private ScrollView scrollView;

    public MessageFragment() { }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message, container, false);

        btnBack = view.findViewById(R.id.btnBack);
        btnSend = view.findViewById(R.id.btnSend);
        edtMessage = view.findViewById(R.id.edtMessage);
        chatContainer = view.findViewById(R.id.chatContainer);
        scrollView = view.findViewById(R.id.scrollView);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());
        btnSend.setOnClickListener(v -> sendMessage());

        return view;
    }


    private void sendMessage() {
        String msg = edtMessage.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) return;

        addUserMessage(msg);
        edtMessage.setText("");

        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    // 🔸 TIN NHẮN NGƯỜI DÙNG (MÀU CAM – bên phải)
    private void addUserMessage(String message) {

        LinearLayout bubble = new LinearLayout(getContext());
        bubble.setOrientation(LinearLayout.VERTICAL);
        bubble.setBackgroundResource(R.drawable.bg_msg_right);
        bubble.setPadding(20, 20, 20, 20);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.END;       // đẩy sang bên phải
        params.topMargin = 20;
        bubble.setLayoutParams(params);

        TextView tv = new TextView(getContext());
        tv.setText(message);
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setTextSize(16);

        TextView time = new TextView(getContext());
        time.setText("Now ✓✓");
        time.setTextSize(12);
        time.setTextColor(0xFFFFE7E7); // màu giống figma

        bubble.addView(tv);
        bubble.addView(time);

        chatContainer.addView(bubble);
    }

    // 🔸 TIN NHẮN ADMIN (MÀU TRẮNG – bên trái)
    private void addAdminMessage(String message) {

        LinearLayout bubble = new LinearLayout(getContext());
        bubble.setOrientation(LinearLayout.VERTICAL);
        bubble.setBackgroundResource(R.drawable.bg_msg_left);
        bubble.setPadding(20, 20, 20, 20);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START;
        params.topMargin = 20;
        bubble.setLayoutParams(params);

        TextView tv = new TextView(getContext());
        tv.setText(message);
        tv.setTextColor(getResources().getColor(R.color.black));
        tv.setTextSize(16);

        TextView time = new TextView(getContext());
        time.setText("Admin");
        time.setTextSize(12);
        time.setTextColor(0xFF999999);

        bubble.addView(tv);
        bubble.addView(time);

        chatContainer.addView(bubble);
    }
}
