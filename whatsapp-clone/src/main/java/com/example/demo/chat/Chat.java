package com.example.demo.chat;

import com.example.demo.common.BaseAuditingEntity;
import com.example.demo.message.Message;
import com.example.demo.message.MessageState;
import com.example.demo.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat")
@NamedQueries(
        {
                @NamedQuery(name=ChatConstants.FIND_CHAT_BY_SENDER_ID,
                            query = "select distinct c from Chat c " +
                                    "where c.sender.id = :senderId or c.recipient.id = :senderId " +
                                    "order by createdDate desc "),
                @NamedQuery(name = ChatConstants.FIND_CHAT_BY_SENDER_ID_AND_RECEIVER_ID,
                query = "select c from Chat c " +
                        "where " +
                        "(c.sender.id = :senderId and c.recipient.id = :receiverId) " +
                        "or " +
                        "(c.sender.id = :receiverId and c.recipient.id = :senderId)" +
                        "order by createdDate desc ")
        }
)
public class Chat extends BaseAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private User recipient;
    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("createdDate DESC")
    private List<Message> messages;

    @Transient
    public String getChatName(String userId) {
        if (recipient.getId().equals(userId)) return sender.getFirstName() + " " + sender.getLastName();
        return recipient.getFirstName() + " " + recipient.getLastName();

    }

    @Transient
    public long getUnreadMessages(String userId) {
        return messages.stream()
                .filter(message -> message.getReceiverId().equals(userId))
                .filter(message -> message.getState() == MessageState.SENT)
                .count();
    }

    @Transient
    public String getLastMessage() {
        if (messages == null || messages.isEmpty()) return null;
        return switch (messages.getFirst().getType()) {
            case TEXT -> messages.getFirst().getContent();
            case AUDIO -> "Audio";
            case IMAGE -> "Attachment";
            case VIDEO -> "Video";
        };
    }

    @Transient
    public LocalDateTime getLastMessageTime() {
        if (messages == null || messages.isEmpty()) return null;
        return messages.getFirst().getCreatedDate();
    }
}
