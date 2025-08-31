package com.eshoppingzone.userProfileService.security.oauth2;

import com.eshoppingzone.userProfileService.entity.Role;
import com.eshoppingzone.userProfileService.entity.Users;
import com.eshoppingzone.userProfileService.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public OAuth2UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        GitHubOAuth2UserInfo userInfo = new GitHubOAuth2UserInfo(oAuth2User.getAttributes());
        
        // GitHub might not provide email directly, use login as fallback
        String email = userInfo.getEmail();
        if(email == null) {
            email = userInfo.getLogin() + "@github.com";
        }

        Optional<Users> userOptional = userRepository.findByEmail(email);
        Users user;
        
        if(userOptional.isPresent()) {
            user = userOptional.get();
            user = updateExistingUser(user, userInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, userInfo, email);
        }
        
        return new OAuth2UserPrincipal(user, oAuth2User.getAttributes());
    }

    private Users registerNewUser(OAuth2UserRequest oAuth2UserRequest, GitHubOAuth2UserInfo userInfo, String email) {
        Users user = new Users();

        user.setFullName(userInfo.getName() != null ? userInfo.getName() : userInfo.getLogin());
        user.setEmail(email);
        user.setUsername(userInfo.getLogin());
        // Generate a random password for OAuth2 users
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        user.setGithubUsername(userInfo.getLogin());
        
        Set<Role> roles = new HashSet<>();
        roles.add(Role.ROLE_USER);
        user.setRoles(roles);
        
        return userRepository.save(user);
    }

    private Users updateExistingUser(Users existingUser, GitHubOAuth2UserInfo userInfo) {
        if (userInfo.getName() != null) {
            existingUser.setFullName(userInfo.getName());
        }
        existingUser.setGithubUsername(userInfo.getLogin());
        return userRepository.save(existingUser);
    }
}