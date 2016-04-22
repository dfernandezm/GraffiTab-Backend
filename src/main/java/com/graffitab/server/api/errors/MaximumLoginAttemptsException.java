package com.graffitab.server.api.errors;

import org.springframework.security.authentication.InternalAuthenticationServiceException;

/**
 * Created by david on 27/04/2016.
 */
public class MaximumLoginAttemptsException  extends InternalAuthenticationServiceException {

        private static final long serialVersionUID = 1L;
        public MaximumLoginAttemptsException(String message, Throwable cause) {
            super(message, cause);
        }
}
