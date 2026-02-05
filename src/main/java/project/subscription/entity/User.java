package project.subscription.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true) // OAuth2
    private String userKey;
    private String nickname;
    private String email;

    @Column(unique = true)
    private String username;
    private String password;

    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Subscription> subscriptionList = new ArrayList<>();

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public User(String userKey, String nickname, String email, String role) {
        this.userKey = userKey;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }

    public void updateProfile(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
