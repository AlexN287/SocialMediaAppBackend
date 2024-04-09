package com.Licenta.SocialMediaApp.Service;

import com.Licenta.SocialMediaApp.Model.Message;

import java.util.List;

public interface MessageService {
    Message sendMessage(Message message);
    List<Message> getMessagesByConversationId(int conversationId);

}
