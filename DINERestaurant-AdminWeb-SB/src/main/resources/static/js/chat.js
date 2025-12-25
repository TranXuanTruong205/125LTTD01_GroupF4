let conversationId = null;

function openChat(id) {
    conversationId = id;
    loadMessages();
}

function loadMessages() {
    fetch(`/api/chat/messages/${conversationId}`, {
        headers: {
            "Authorization": "Bearer " + localStorage.getItem("token")
        }
    })
        .then(res => res.json())
        .then(data => {
            const box = document.getElementById("chatMessages");
            box.innerHTML = "";

            data.forEach(m => {
                const div = document.createElement("div");
                div.className = m.senderRole === "admin" ? "msg admin" : "msg user";
                div.innerText = m.content;
                box.appendChild(div);
            });

            box.scrollTop = box.scrollHeight;
        });
}

function sendMessage() {
    const text = document.getElementById("messageInput").value;
    if (!text || !conversationId) return;

    fetch("/api/chat/send", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        body: JSON.stringify({
            conversationId: conversationId,
            content: text
        })
    }).then(() => {
        document.getElementById("messageInput").value = "";
        loadMessages();
    });
}

// polling mỗi 3s
setInterval(() => {
    if (conversationId) loadMessages();
}, 3000);
