package com.vulkano.desarolloApp.security;

import com.vulkano.desarolloApp.models.user.UserEntity;
import com.vulkano.desarolloApp.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    @Autowired
    private UserRepository userRepository;

    public boolean isCurrentUser(Long userId) {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       String currentUsername = authentication.getName();

       UserEntity user = userRepository.findById(String.valueOf(userId)).orElse(null);
       return user != null && user.getUsername().equals(currentUsername);
    }

}
