package com.graffitab.server.api.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.api.dto.asset.AssetDto;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class UserDto {

	private Long id;
	private String guid;
	private String username;
	private String firstName;
	private String lastName;
	private String password;
	private String email;
	private String website;
	private String about;
	private Boolean followedByCurrentUser = Boolean.FALSE;
	private String createdOn;
	private String updatedOn;

	@JsonProperty("avatar")
	private AssetDto avatarAsset;

	@JsonProperty("cover")
	private AssetDto coverAsset;

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}
}
