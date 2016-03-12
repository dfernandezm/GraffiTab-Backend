package com.graffitab.server.api.dto.notification;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.dto.user.UserDto;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class NotificationDto {

	private Boolean isRead;
	private String date;

	@JsonProperty("type")
	private String notificationType;

	private UserDto follower;
}
