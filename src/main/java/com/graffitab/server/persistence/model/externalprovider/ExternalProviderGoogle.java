package com.graffitab.server.persistence.model.externalprovider;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("GOOGLE")
public class ExternalProviderGoogle extends ExternalProvider {

	private static final long serialVersionUID = 1L;

	public ExternalProviderGoogle() {

	}

	public ExternalProviderGoogle(String externalUserId, String accessToken) {
		super(ExternalProviderType.GOOGLE, externalUserId, accessToken);
	}
}
