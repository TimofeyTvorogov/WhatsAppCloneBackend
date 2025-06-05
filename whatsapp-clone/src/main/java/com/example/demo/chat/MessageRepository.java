package com.example.demo.chat;

import com.example.demo.message.Message;
import com.example.demo.message.MessageConstants;
import com.example.demo.message.MessageState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query(name = MessageConstants.FIND_MESSAGES_BY_CHAT_ID)
    List<Message> findMessagesByChatId(@Param("chatId") String chatId);

    @Query(name = MessageConstants.SET_MESSAGES_TO_SEEN_BY_CHAT_ID)
    @Modifying
    void setMessagesToSeenByChat(@Param("newState") MessageState State, @Param("chatId") String chatId);
}
