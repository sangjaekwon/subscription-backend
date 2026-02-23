package project.subscription.oauth2;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> data;

    public KakaoResponse(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String getNickname() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) data.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        return String.valueOf(profile.get("nickname"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) data.get("kakao_account");
        return String.valueOf(kakaoAccount.get("email"));
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return String.valueOf(data.get("id"));
    }
}
