package com.graffitab.server.api.mapper.externalprovider;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.externalprovider.ExternalProviderDto;
import com.graffitab.server.persistence.model.externalprovider.ExternalProviderTwitter;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ExternalProviderTwitterMapper extends CustomMapper<ExternalProviderTwitter, ExternalProviderDto> {

	@Override
	public void mapAtoB(ExternalProviderTwitter a, ExternalProviderDto b, MappingContext context) {
		super.mapAtoB(a, b, context);
	}
}
