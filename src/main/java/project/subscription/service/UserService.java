package project.subscription.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.request.JoinRequest;
import project.subscription.entity.User;
import project.subscription.exception.ex.DuplicateUsernameException;
import project.subscription.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Transactional
    public void join(JoinRequest joinRequest) {
        User user = userRepository.findByUsername(joinRequest.getUsername()).orElse(null);
        if (user != null) throw new DuplicateUsernameException();


        userRepository.save(User.createLocalUser(joinRequest.getUsername(), joinRequest.getEmail(), encoder.encode(joinRequest.getPassword()), "ROLE_USER"));
    }


}
