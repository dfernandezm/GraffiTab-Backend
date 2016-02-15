package com.graffitab.server.api.dto.asset;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssetDto {

	private String guid;

	@JsonProperty("type")
	private String assetType;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getAssetType() {
		return assetType;
	}

	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
}
