package com.graffitab.server.api.dto.activity;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.dto.comment.CommentDto;
import com.graffitab.server.api.dto.streamable.FullStreamableDto;
import com.graffitab.server.api.dto.user.UserDto;

@Data
@JsonInclude(Include.NON_NULL)
public class ActivityDto {

	@JsonProperty("date")
	private String createdOn;

	@JsonProperty("type")
	private String activityType;

	private UserDto followed;
	private UserDto follower;
	private UserDto liker;
	private FullStreamableDto likedStreamable;
	private UserDto commenter;
	private FullStreamableDto commentedStreamable;
	private CommentDto comment;
	private UserDto creator;
	private FullStreamableDto createdStreamable;
}
