package com.graffitab.server.api.dto.user;

import lombok.Data;

@Data
public class ChangePasswordDto {

	private String currentPassword;
	private String newPassword;
}
