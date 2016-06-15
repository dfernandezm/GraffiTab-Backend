package com.graffitab.server.persistence.model.externalprovider;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("FACEBOOK")
public class ExternalProviderFacebook extends ExternalProvider {

	private static final long serialVersionUID = 1L;

	public ExternalProviderFacebook() {

	}

	public ExternalProviderFacebook(String externalUserId, String accessToken) {
		super(ExternalProviderType.FACEBOOK, externalUserId, accessToken);
	}
}
