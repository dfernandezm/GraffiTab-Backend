package com.graffitab.server.api.mapper.externalprovider;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.externalprovider.ExternalProviderDto;
import com.graffitab.server.persistence.model.externalprovider.ExternalProviderGoogle;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class ExternalProviderGoogleMapper extends CustomMapper<ExternalProviderGoogle, ExternalProviderDto> {

	@Override
	public void mapAtoB(ExternalProviderGoogle a, ExternalProviderDto b, MappingContext context) {
		super.mapAtoB(a, b, context);
	}
}
