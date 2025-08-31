package com.eshoppingzone.userProfileService.security.oauth2;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class GitHubOAuth2UserInfo {
    private Map<String, Object> attributes;

    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    public String getName() {
        return (String) attributes.get("name");
    }

    public String getEmail() {
        return (String) attributes.get("email");
    }
    
    public String getLogin() {
        return (String) attributes.get("login");
    }
}