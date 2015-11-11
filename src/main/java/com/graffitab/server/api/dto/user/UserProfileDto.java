package com.graffitab.server.api.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"password"})
public class UserProfileDto extends UserDto {
	
	// Exclude password
	private Integer followersCount;
	private Integer followingCount;
	private Integer streamablesCount;
	
	public UserProfileDto() {
		this.followersCount = 0;
		this.followingCount = 0;
		this.streamablesCount = 0;
	}
	
	public Integer getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(Integer followersCount) {
		this.followersCount = followersCount;
	}
	public Integer getFollowingCount() {
		return followingCount;
	}
	public void setFollowingCount(Integer followingCount) {
		this.followingCount = followingCount;
	}
	public Integer getStreamablesCount() {
		return streamablesCount;
	}
	public void setStreamablesCount(Integer streamablesCount) {
		this.streamablesCount = streamablesCount;
	}
}
