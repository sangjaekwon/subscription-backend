package project.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import project.subscription.dto.CustomUserDetails;
import project.subscription.entity.User;
import project.subscription.repository.UserRepository;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if(user == null) throw new UsernameNotFoundException("유저가 존재하지 않습니다.");

        return new CustomUserDetails(user.getUsername(), user.getPassword());
    }
}
