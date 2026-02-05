package project.subscription.oauth2;

import java.util.Map;

public class NaverResponse implements OAuth2Response{

    private final Map<String, Object> data;

    public NaverResponse(Map<String, Object> data) {
        this.data = (Map<String, Object>) data.get("response");
    }

    @Override
    public String getNickname() {
        return String.valueOf(data.get("name"));
    }

    @Override
    public String getEmail() {
        return String.valueOf(data.get("email"));
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
