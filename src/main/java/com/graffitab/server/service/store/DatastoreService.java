package com.graffitab.server.service.store;

import java.io.InputStream;
import java.util.Map;

import com.graffitab.server.persistence.model.AssetType;

public interface DatastoreService {

	void saveAsset(InputStream inputStream, long contentLength, String userGuid, String assetGuid, AssetType assetType, Map<String, String> metadata);
	void deleteAsset(String assetGuid);
	void updateAsset(String assetGuid, InputStream inputStream);
	String generateDownloadLink(String assetGuid);

}
