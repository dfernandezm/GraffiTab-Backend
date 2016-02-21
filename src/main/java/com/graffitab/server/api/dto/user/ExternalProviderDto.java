package com.graffitab.server.api.dto.user;

import lombok.Data;

@Data
public class ExternalProviderDto {

	public enum ExternalProviderType {
		FACEBOOK,
		TWITTER,
		GOOGLE;
	}

	private String externalId;
	private String accessToken;
	private ExternalProviderType externalProviderType;
}
