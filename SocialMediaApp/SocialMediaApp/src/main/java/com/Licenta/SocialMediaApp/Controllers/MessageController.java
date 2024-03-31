package com.Licenta.SocialMediaApp.Controllers;

import com.Licenta.SocialMediaApp.Model.Message;
import com.Licenta.SocialMediaApp.Service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody Message message)
    {
        try {
            return ResponseEntity.ok().body(messageService.sendMessage(message));
        } catch (Exception e)
        {
            return ResponseEntity.internalServerError().body(message);
        }
    }
}
