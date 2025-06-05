package com.example.demo.chat;

import com.example.demo.file.FileService;
import com.example.demo.file.FileUtils;
import com.example.demo.message.*;
import com.example.demo.notification.Notification;
import com.example.demo.notification.NotificationService;
import com.example.demo.notification.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper mapper;
    private final ChatRepository chatRepository;
    private final FileService fileService;
    private final NotificationService notificationService;

    public void saveMessage(MessageRequest messageRequest) {
        Chat chat = chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(() -> new EntityNotFoundException("no chat with such id " + messageRequest.getChatId()));
        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setChat(chat);
        message.setReceiverId(messageRequest.getReceiverId());
        message.setSenderId(messageRequest.getSenderId());
        message.setType(messageRequest.getType());
        message.setState(MessageState.SENT);
        messageRepository.save(message);
        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .messageType(message.getType())
                .content(message.getContent())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .notificationType(NotificationType.MESSAGE)
                .chatName(chat.getChatName(message.getSenderId()))
                .build();
        notificationService.sendNotification(message.getReceiverId(), notification);
    }

    public List<MessageResponse> findChatMessages(String chatId) {
        return messageRepository.findMessagesByChatId(chatId)
                .stream()
                .map(mapper::toMessageResponse)
                .toList();
    }
    @Transactional
    public void setMessagesToSeen(String chatId, Authentication authentication) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("no chat with such id " + chatId));
        String recipientId = getRecipientId(chat, authentication);
        messageRepository.setMessagesToSeenByChat(MessageState.SEEN, chatId);
        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .senderId(getSenderId(chat, authentication))
                .receiverId(recipientId)
                .notificationType(NotificationType.SEEN)
                .build();
        notificationService.sendNotification(recipientId, notification);
    }

    public void uploadMediaMessage(String chatId, MultipartFile file, Authentication authentication) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("no chat with such id " + chatId));
        String senderId = getSenderId(chat, authentication);
        String recipientId = getRecipientId(chat, authentication);
        String filePath = fileService.saveFile(file, senderId);
        Message message = new Message();
        message.setChat(chat);
        message.setReceiverId(recipientId);
        message.setSenderId(senderId);
        message.setType(MessageType.IMAGE);
        message.setState(MessageState.SENT);
        message.setMediaFilePath(filePath);
        messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .messageType(message.getType())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .notificationType(NotificationType.IMAGE)
                .media(FileUtils.readFileFromLocation(filePath))
                .build();
        notificationService.sendNotification(recipientId, notification);
    }
    private String getRecipientId(Chat chat, Authentication authentication) {
        String senderId = chat.getSender().getId();
        if (senderId.equals(authentication.getName())) {
            return chat.getRecipient().getId();
        }
        return senderId;
    }
    private String getSenderId(Chat chat, Authentication authentication) {
        String senderId = chat.getSender().getId();
        if (senderId.equals(authentication.getName())) {
            return senderId;
        }
        return chat.getRecipient().getId();
    }
}
