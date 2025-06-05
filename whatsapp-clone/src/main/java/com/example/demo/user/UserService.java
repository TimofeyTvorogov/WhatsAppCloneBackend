package com.example.demo.user;

import com.example.demo.chat.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final UserMapper userMapper;
    public List<UserResponse> findAllUsersExceptSelf(Authentication connectedUser) {
        return userRepository.findAllUsersExceptSelf(connectedUser.getName())
                .stream()
                .map(UserMapper::toUserResponse)
                .toList();
    }
}
