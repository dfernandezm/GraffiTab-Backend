package com.graffitab.server.api.authentication;

import com.graffitab.server.service.user.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Log4j2
public class FailedLoginApplicationListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Resource
    private UserService userService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        Object username = event.getAuthentication().getPrincipal();
        Object credentials = event.getAuthentication().getCredentials();

        if (log.isDebugEnabled()) {
            log.debug("Failed login using USERNAME [" + username + "]");
            log.debug("Failed login using PASSWORD [" + credentials + "]");
        }

        // Add failed login attempts
        userService.updateLoginAttempts((String) username);
    }
}
