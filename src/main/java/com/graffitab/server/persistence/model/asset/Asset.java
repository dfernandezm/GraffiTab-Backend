package com.graffitab.server.persistence.model.asset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import com.graffitab.server.persistence.dao.Identifiable;
import com.graffitab.server.util.GuidGenerator;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "asset")
public class Asset implements Identifiable<Long> {

	private static final long serialVersionUID = 1L;

	public enum AssetType {
		IMAGE;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name = "guid", nullable = false)
	private String guid;

	@Enumerated(EnumType.STRING)
	@Column(name = "asset_type", nullable = false)
	private AssetType assetType;

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
