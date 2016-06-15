package com.graffitab.server.api.mapper.externalprovider;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.externalprovider.ExternalProviderDto;
import com.graffitab.server.persistence.model.externalprovider.ExternalProviderFacebook;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ExternalProviderFacebookMapper extends CustomMapper<ExternalProviderFacebook, ExternalProviderDto> {

	@Override
	public void mapAtoB(ExternalProviderFacebook a, ExternalProviderDto b, MappingContext context) {
		super.mapAtoB(a, b, context);
	}
}
