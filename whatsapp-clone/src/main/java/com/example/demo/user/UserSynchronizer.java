package com.example.demo.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSynchronizer {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    public void synchronizeWithIdp(Jwt token) {
        log.info("Synchronizing user with idp");
        getUserEmail(token).ifPresent(email -> {
            log.info("Synchronizing user having email {}", email);
            Optional<User> optionalUser = userRepository.findByEmail(email);
            User requestedUser = userMapper.fromTokenAttributes(token.getClaims());
            optionalUser.ifPresent(user -> requestedUser.setId(user.getId()));
            userRepository.save(requestedUser);
        });
    }

    private Optional<String> getUserEmail(Jwt token) {
        Map<String, Object> attributes = token.getClaims();
        if (!attributes.containsKey("email")) return Optional.empty();
        return Optional.of(attributes.get("email").toString());

    }
}
