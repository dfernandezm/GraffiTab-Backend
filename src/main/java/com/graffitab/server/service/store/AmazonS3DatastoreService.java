package com.graffitab.server.service.store;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.graffitab.server.util.GuidGenerator;

public class AmazonS3DatastoreService implements DatastoreService {
	
	private static Logger LOG = LogManager.getLogger();

	private static String BUCKET_NAME = "graffitab-eu1"; // Single bucket for now
	private static String SUFFIX = "/";
	
	private AmazonS3 amazonS3Client;
	
	@PostConstruct
	public void setupClient() {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("",
				"");
		amazonS3Client = new AmazonS3Client(awsCreds);
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

	@Override
	public void saveAsset(InputStream inputStream, long contentLength,
			String assetName, Map<String, String> metadata) {
		
		createFolderInBucket("assets");
		
		// create entity in DB - put RUNNING
		// send file in background thread
		// when background thread finishes upload, update database with DONE
	}
	
	
	private static void createFolderInBucket(String folderName, AmazonS3Client amazonS3Client) {
		
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
	
	public static void testAmazon(String[] args) {
		BasicAWSCredentials awsCreds = new BasicAWSCredentials("AccessKey", "SecreID");
		
		String usersRoot = "users";
		String assetGuid = GuidGenerator.generate();
		String userGuid = GuidGenerator.generate();
		String resourceName = "graffiti"; // avatar, cover
		
		// Service to generate this
		String resourcePath = usersRoot + "/" + userGuid + "/"+ resourceName + "/" + assetGuid; 
		
		AmazonS3Client amazonS3Client = new AmazonS3Client(awsCreds);
		//TODO: Investigate folder deletion
		// createFolderInBucket("assets", amazonS3Client);
		// InputStream from the API endpoint
		File f = new File("/Users/david/graffiti-2.jpg");
		long startTime = System.currentTimeMillis();
		
		//TODO: create row in DB for upload_job -> PENDING
		// Thumbnails - generate it in the background job
		// Submit to threadpool
		PutObjectResult result = amazonS3Client.putObject(new PutObjectRequest(
                BUCKET_NAME, resourcePath, f));
		
		ObjectMetadata metadata = result.getMetadata();
		
		// return with state
		// Once the thread is done, create/update DB with that assetGuid, it can be downloaded
		
		System.out.println("It took " + (System.currentTimeMillis() - startTime) + " to upload");
	
		metadata.getUserMetadata().forEach((key, value) -> System.out.println(key + ", " + value));
		
		startTime = System.currentTimeMillis();
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, resourcePath);
		request.setGeneralProgressListener((event) -> { 
			    System.out.println("Event: " + event.getEventType()  + ", bytes " + event.getBytesTransferred());
			    });
		System.out.println("Link: " + amazonS3Client.generatePresignedUrl(request));
		System.out.println("It took " + (System.currentTimeMillis() - startTime) + " to generate the link");

	}
}
