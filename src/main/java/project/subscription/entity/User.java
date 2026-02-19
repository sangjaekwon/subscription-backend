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


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptionList = new ArrayList<>();

    public static User createLocalUser(String username, String email, String password, String role) {
        User user = new User();
        user.username = username;
        user.password = password;
        user.email = email;
        user.role = role;
        return user;
    }

    public static User createOauthUser(String userKey, String nickname, String email, String role) {
        User user = new User();
        user.userKey = userKey;
        user.nickname = nickname;
        user.email = email;
        user.role = role;
        return user;
    }

    public void updateProfile(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

    public void removeSubscription(Subscription subscription) {
        subscriptionList.remove(subscription);
        subscription.changeUser(null);
    }

    public void addSubscription(Subscription subscription) {
        subscriptionList.add(subscription);
        subscription.changeUser(this);
    }
}
