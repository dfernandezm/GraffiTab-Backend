package com.graffitab.server.persistence.model.externalprovider;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("TWITTER")
public class ExternalProviderTwitter extends ExternalProvider {

	private static final long serialVersionUID = 1L;

	public ExternalProviderTwitter() {

	}

	public ExternalProviderTwitter(String externalUserId, String accessToken) {
		super(ExternalProviderType.TWITTER, externalUserId, accessToken);
	}
}
