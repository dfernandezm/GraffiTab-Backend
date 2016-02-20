package com.graffitab.server.persistence.model;

import com.graffitab.server.persistence.dao.Identifiable;
import com.graffitab.server.util.GuidGenerator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @EqualsAndHashCode
public class Asset implements Identifiable<Long> {

	private static final long serialVersionUID = 1L;

	public enum AssetType {
		AVATAR, COVER, GRAFFITI;
	}

	private Long id;
	private String guid;
	private AssetType assetType;
	private User user;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public static Asset asset(AssetType type) {
		Asset asset = new Asset();
		asset.setGuid(GuidGenerator.generate());
		asset.setAssetType(type);
		return asset;
	}
}
