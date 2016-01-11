package com.graffitab.server.service.store;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;

public class AmazonS3DatastoreService implements DatastoreService {
	
	private static Logger LOG = LogManager.getLogger();

	private static String BUCKET_NAME = "graffitab-eu"; // Single bucket for now
	private static String SUFFIX = "/";
	
	private AmazonS3 amazonS3Client;
	
	@PostConstruct
	public void setupClient() {
		amazonS3Client = new AmazonS3Client(new ProfileCredentialsProvider());
	}

	
	@Override
	public void deleteAsset(String assetGuid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAsset(String assetGuid, InputStream inputStream) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String generateDownloadLink(String assetGuid) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void createFolderInBucket(String folderName) {
		
		// create meta-data for your folder and set content-length to 0
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(0);
		
		// create empty content
		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
		
		// create a PutObjectRequest passing the folder name suffixed by /
		PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME,
					folderName + SUFFIX, emptyContent, metadata);
		
		// send request to S3 to create folder
		PutObjectResult putObjectResult = amazonS3Client.putObject(putObjectRequest);
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("Result of creation of folder in Amazon S3: hash " + 
						putObjectResult.getMetadata().getContentMD5());
		}
	}
	
	// This is  folder!
	private String getKeyName(String assetGuid) {
		return assetGuid + "_key";
	}

	@Override
	public void saveAsset(InputStream inputStream, long contentLength,
			String assetName, Map<String, String> metadata) {
		//uploadMultipart(inputStream, contentLength, GuidGenerator.generate());
		// create entity in DB - put RUNNING
		// send file in background thread
		// when background thread finishes upload, update database with DONE
	}
//	private static long MULTIPART_UPLOAD_PART_SIZE = 5 * 1024 * 1024; // Set part size to 5 MB.private static long MULTIPART_UPLOAD_PART_SIZE = 5 * 1024 * 1024; // Set part size to 5 MB.
//	private List<PartETag> uploadMultipart(InputStream inputStream, long contentLength, String assetGuid) {
//		
//		String keyName = getKeyName(assetGuid);
//
//		// Create a list of UploadPartResponse objects. You get one of these for
//		// each part upload.
//		List<PartETag> partETags = new ArrayList<PartETag>();
//
//		// Step 1: Initialize.
//		InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
//		                                                    BUCKET_NAME, keyName);
//		InitiateMultipartUploadResult initResponse = 
//		                              amazonS3Client.initiateMultipartUpload(initRequest);
//
//		long partSize = MULTIPART_UPLOAD_PART_SIZE;
//
//		try {
//		    // Step 2: Upload parts.
//		    long filePosition = 0;
//		    for (int i = 1; filePosition < contentLength; i++) {
//		        // Last part can be less than 5 MB. Adjust part size.
//		    	partSize = Math.min(partSize, (contentLength - filePosition));
//		    	
//		        // Create request to upload a part.
//		        UploadPartRequest uploadRequest = new UploadPartRequest()
//		            .withBucketName(BUCKET_NAME).withKey(keyName)
//		            .withUploadId(initResponse.getUploadId()).withPartNumber(i)
//		            .withFileOffset(filePosition)
//		            .withInputStream(inputStream)
//		            .withPartSize(partSize);
//
//		        // Upload part and add response to our list.
//		        partETags.add(amazonS3Client.uploadPart(uploadRequest).getPartETag());
//
//		        filePosition += partSize;
//		    }
//
//		    // Step 3: Complete.
//		    CompleteMultipartUploadRequest compRequest = new 
//		                CompleteMultipartUploadRequest(BUCKET_NAME, 
//		                                               keyName, 
//		                                               initResponse.getUploadId(), 
//		                                               partETags);
//
//		    amazonS3Client.completeMultipartUpload(compRequest);
//		    return partETags;
//		} catch (Exception e) {
//		    amazonS3Client.abortMultipartUpload(new AbortMultipartUploadRequest(
//		              BUCKET_NAME, keyName, initResponse.getUploadId()));
//		    
//		    throw new RuntimeException("Error uploading multipart", e);
//		}
//		
//		
//	}
}
