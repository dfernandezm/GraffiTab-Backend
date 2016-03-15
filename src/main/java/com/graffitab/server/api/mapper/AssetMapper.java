package com.graffitab.server.api.mapper;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.asset.AssetDto;
import com.graffitab.server.persistence.model.asset.Asset;
import com.graffitab.server.service.store.DatastoreService;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

@Component
public class AssetMapper extends CustomMapper<Asset, AssetDto> {

	@Resource
	private DatastoreService datastoreService;

	@Override
	public void mapAtoB(Asset a, AssetDto b, MappingContext context) {
		super.mapAtoB(a, b, context);

		b.setLink(datastoreService.generateDownloadLink(a.getGuid()));
	}
}
