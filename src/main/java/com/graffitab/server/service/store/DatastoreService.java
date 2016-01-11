package com.graffitab.server.service.store;

import java.io.InputStream;
import java.util.Map;

public interface DatastoreService {

	void saveAsset(InputStream inputStream, long contentLength, String assetName, Map<String, String> metadata);
	void deleteAsset(String assetGuid);
	void updateAsset(String assetGuid, InputStream inputStream);
	String generateDownloadLink(String assetGuid);

}
