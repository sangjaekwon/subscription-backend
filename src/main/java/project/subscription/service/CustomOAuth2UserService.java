package project.subscription.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.subscription.dto.CustomUserPrincipal;
import project.subscription.entity.User;
import project.subscription.oauth2.GoogleResponse;
import project.subscription.oauth2.KakaoResponse;
import project.subscription.oauth2.NaverResponse;
import project.subscription.oauth2.OAuth2Response;
import project.subscription.repository.UserRepository;

import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = getResponse(provider, attributes);

        String userKey = oAuth2Response.getProviderId() + ":" + oAuth2Response.getProvider();
        User user = userRepository.findByUserKey(userKey)
                .orElseGet(() -> userRepository.save(
                        User.createOauthUser(userKey, oAuth2Response.getNickname(), oAuth2Response.getEmail(), "ROLE_USER")
                ));
        user.updateProfile(oAuth2Response.getEmail(), oAuth2Response.getNickname());
        return new CustomUserPrincipal(user.getId(), userKey, null, new SimpleGrantedAuthority("ROLE_USER"));
    }

    private OAuth2Response getResponse(String provider, Map<String, Object> data) {
        return switch (provider) {
            case "naver" -> new NaverResponse(data);
            case "google" -> new GoogleResponse(data);
            case "kakao" -> new KakaoResponse(data);
            default -> null;
        };
    }
}
