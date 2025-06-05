package com.example.demo.user;

import com.example.demo.chat.Chat;
import com.example.demo.common.BaseAuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@NamedQueries(
        {
        @NamedQuery(name=UserConstants.FIND_USER_BY_EMAIL,
                query = "select u from User u where u.email = :email"),
        @NamedQuery(name=UserConstants.FIND_ALL_USERS_EXCEPT_SELF,
                query = "SELECT u from User u where u.id != :publicId"),
        @NamedQuery(name=UserConstants.FIND_USER_BY_PUBLIC_ID,
                query = "SELECT u from User u where u.id = :publicId")
    }
)




public class User extends BaseAuditingEntity {
    private static final long LAST_ACTIVE_INTERVAL = 5;
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime lastSeen;

    @OneToMany(mappedBy = "sender")
    private List<Chat> chatsAsSender;
    @OneToMany(mappedBy = "recipient")
    private List<Chat> chatsAsRecipient;

    @Transient
    public boolean isUserOnline(){
        return lastSeen != null && lastSeen.isAfter(LocalDateTime.now().plusMinutes(LAST_ACTIVE_INTERVAL));
    }
}
