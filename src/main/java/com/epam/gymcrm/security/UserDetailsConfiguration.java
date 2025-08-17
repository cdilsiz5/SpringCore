package com.epam.gymcrm.security;



import com.epam.gymcrm.exception.UserNotFoundException;
import com.epam.gymcrm.model.User;
import com.epam.gymcrm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
 import org.springframework.transaction.annotation.Transactional;



@RequiredArgsConstructor
@Slf4j
@Configuration
public class UserDetailsConfiguration implements UserDetailsService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("User Not Found"));
        log.info("Retrieved  user {}", username);
        if (user!=null) {
            return UserDetailsImpl.build(user);
        }
        log.error("Retrieved  user not found {}", username);
        throw new UserNotFoundException("User Not Found");
    }
}