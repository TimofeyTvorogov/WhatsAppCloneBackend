package com.example.demo.message;

import com.example.demo.chat.Chat;
import com.example.demo.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
@NamedQueries(
        {
                @NamedQuery(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID,
                            query = "SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createdDate"),
                @NamedQuery(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT_ID,
                            query = "UPDATE Message SET state = :newState WHERE chat.id = :chatId")
        }
)

public class Message extends BaseAuditingEntity  {
    @Id
    @SequenceGenerator(name = "msg_seq", sequenceName = "msg_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "msg_seq")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;
    @Enumerated(EnumType.STRING)
    private MessageState state;
    @Enumerated(EnumType.STRING)
    private MessageType type;
    @Column(name="sender_id", nullable = false)
    private String senderId;
    @Column(name="receiver_id", nullable = false)
    private String receiverId;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    private String mediaFilePath;


}

