package com.graffitab.server.service.store;

import java.io.InputStream;

import com.graffitab.server.persistence.model.Asset.AssetType;

public interface DatastoreService {

	void saveAsset(InputStream inputStream, long contentLength, String userGuid, String assetGuid, AssetType assetType);
	void deleteAsset(String assetGuid);
	void updateAsset(String assetGuid, InputStream inputStream);
	String generateDownloadLink(String assetGuid);

}
