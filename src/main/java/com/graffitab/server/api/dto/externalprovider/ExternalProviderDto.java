package com.graffitab.server.api.dto.externalprovider;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.graffitab.server.persistence.model.externalprovider.ExternalProviderType;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class ExternalProviderDto {

	@JsonProperty(value = "userId")
	private String externalUserId;

	@JsonProperty(value = "type")
	private ExternalProviderType externalProviderType;

	@JsonProperty(value = "token")
	private String accessToken;

	@JsonIgnore
	public String getAccessToken() {
		return accessToken;
	}

	@JsonProperty
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
