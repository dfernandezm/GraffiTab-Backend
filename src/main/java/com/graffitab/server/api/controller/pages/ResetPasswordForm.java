package com.graffitab.server.api.controller.pages;

import lombok.Data;

/**
 * Created by david on 27/07/2016.
 */
@Data
public class ResetPasswordForm {
    private String newPassword;
    private String repeatedPassword;
    private String token;
}
