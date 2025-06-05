package com.example.demo.message;

import com.example.demo.chat.MessageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Message")
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void postMessage(@RequestBody MessageRequest messageRequest) {
        messageService.saveMessage(messageRequest);

    }
    @PostMapping(value = "/upload_media", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public void postMedia(
            @RequestParam("chat-id") String chatId,
            @Parameter()
            @RequestParam("file")MultipartFile file,
            Authentication authentication
            ) {
        messageService.uploadMediaMessage(chatId,file, authentication);
    }
    @PatchMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void patchMessagesToSeen(@RequestParam("chat-id") String chatId,
                                    Authentication authentication) {
        messageService.setMessagesToSeen(chatId, authentication);
    }

    @GetMapping
    public ResponseEntity<List<MessageResponse>> getChatMessages(@RequestParam("chat-id") String chatId){
        return ResponseEntity.ok(messageService.findChatMessages(chatId));
    }

}
