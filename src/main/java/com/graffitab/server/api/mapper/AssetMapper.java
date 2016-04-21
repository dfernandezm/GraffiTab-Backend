package com.graffitab.server.api.mapper;

import javax.annotation.Resource;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;

import org.springframework.stereotype.Component;

import com.graffitab.server.api.dto.asset.AssetDto;
import com.graffitab.server.persistence.model.asset.Asset;
import com.graffitab.server.service.store.DatastoreService;

@Component
public class AssetMapper extends CustomMapper<Asset, AssetDto> {

	@Resource
	private DatastoreService datastoreService;

	@Override
	public void mapAtoB(Asset asset, AssetDto assetDto, MappingContext context) {
		assetDto.setLink(datastoreService.generateDownloadLink(asset.getGuid()));
		assetDto.setThumbnail(datastoreService.generateThumbnailLink(asset.getGuid()));
	}
}
