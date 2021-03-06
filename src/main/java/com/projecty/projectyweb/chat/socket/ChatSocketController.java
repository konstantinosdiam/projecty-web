package com.projecty.projectyweb.chat.socket;

import com.projecty.projectyweb.chat.ChatMessage;
import com.projecty.projectyweb.chat.ChatService;
import com.projecty.projectyweb.user.UserNotFoundException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;

@Controller
@CrossOrigin()
public class ChatSocketController {
    private final ChatService chatService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatSocketController(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate) {
        this.chatService = chatService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/secured/room")
    public void sendSpecific(
            @Payload SocketChatMessage msg,
            Principal user,
            @Header("simpSessionId") String sessionId) throws Exception {
        try {
            ChatMessage chatMessage = chatService.saveInDatabase(msg, user);
            simpMessagingTemplate.convertAndSendToUser(
                    msg.getRecipient(), "/secured/user/queue/specific-user", chatMessage);
        } catch (UserNotFoundException ignored) {
        }
    }
}
