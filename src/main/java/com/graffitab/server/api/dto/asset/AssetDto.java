package com.graffitab.server.api.dto.asset;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AssetDto {

	private String guid;

	@JsonProperty("type")
	private String assetType;

	private String link;
}
