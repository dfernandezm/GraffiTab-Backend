package com.graffitab.server.api.dto.asset;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class AssetDto {

	private String guid;

	@JsonProperty("type")
	private String assetType;

	private String link;
}
