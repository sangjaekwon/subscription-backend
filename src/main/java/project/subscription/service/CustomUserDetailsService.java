package project.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import project.subscription.dto.CustomUserPrincipal;
import project.subscription.entity.User;
import project.subscription.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if(user == null) throw new RuntimeException(); // UserService에서 커스텀 예외로 처리

        return new CustomUserPrincipal(user.getUsername(), user.getPassword(), new SimpleGrantedAuthority("ROLE_USER"));
    }
}
