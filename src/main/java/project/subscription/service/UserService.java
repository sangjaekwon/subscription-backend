package project.subscription.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.JoinRequest;
import project.subscription.dto.LoginRequest;
import project.subscription.entity.User;
import project.subscription.exception.DuplicateUsernameException;
import project.subscription.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public void join(JoinRequest joinRequest) {
        User user = userRepository.findByUsername(joinRequest.getUsername());
        if(user != null) throw new DuplicateUsernameException();

        userRepository.save(
                new User(joinRequest.getUsername(), encoder.encode(joinRequest.getPassword()))
        );
    }

    public String login(LoginRequest loginRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), loginRequest.getPasswrod()
        ));


        return null;
    }


}
