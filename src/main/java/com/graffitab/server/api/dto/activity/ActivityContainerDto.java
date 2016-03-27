package com.graffitab.server.api.dto.activity;

import java.util.List;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.dto.user.UserDto;

@Data
@JsonInclude(Include.NON_NULL)
public class ActivityContainerDto {

	private UserDto user;

	@JsonProperty("date")
	private String createdOn;

	@JsonProperty("type")
	private String activityType;

	private List<ActivityDto> activities;
}
