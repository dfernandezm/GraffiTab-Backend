package com.graffitab.server.service.store;

import java.io.InputStream;

public interface DatastoreService {

	void saveAsset(InputStream inputStream, long contentLength, String userGuid, String assetGuid);
	void updateAsset(InputStream inputStream, long contentLength, String userGuid, String assetGuid);
	void deleteAsset(String userGuid, String assetGuid);
	String generateDownloadLink(String userGuid, String assetGuid);
}
