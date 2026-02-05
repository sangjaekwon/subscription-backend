package project.subscription.oauth2;

import java.util.Map;

public class GoogleResponse implements OAuth2Response{

    private final Map<String, Object> data;

    public GoogleResponse(Map<String, Object> data) {
        this.data = data;
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
        return "google";
    }

    @Override
    public String getProviderId() {
        return String.valueOf(data.get("sub"));
    }
}
