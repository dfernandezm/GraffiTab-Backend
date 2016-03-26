package com.graffitab.server.service.store;

import java.io.InputStream;

public interface DatastoreService {

	void saveAsset(InputStream inputStream, long contentLength, String assetGuid);
	void updateAsset(InputStream inputStream, long contentLength, String assetGuid);
	void deleteAsset(String assetGuid);
	String generateDownloadLink(String assetGuid);
	String generateThumbnailLink(String assetGuid);
}
