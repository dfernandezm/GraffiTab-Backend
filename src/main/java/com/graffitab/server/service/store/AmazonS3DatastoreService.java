package com.graffitab.server.service.store;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.graffitab.server.persistence.model.AssetType;
import com.graffitab.server.service.TransactionUtils;
import com.graffitab.server.service.UploadJobService;
import com.graffitab.server.util.GuidGenerator;

@Service
public class AmazonS3DatastoreService implements DatastoreService {

	private static Logger LOG = LogManager.getLogger();

	private static String BUCKET_NAME = "graffitab-eu1"; // Single bucket for now
	private static String SUFFIX = "/";
	private static String USERS_ROOT_KEY = "users";
    private static String ASSETS_ROOT_KEY = "assets";

	private AmazonS3 amazonS3Client;

	//private Executor executor = Executors.newFixedThreadPool(2);

	private String AWS_SECRET_ENVVAR_NAME = "AWS_SECRET_KEY";
	private String AWS_KEY_ENVVAR_NAME = "AWS_ACCESS_KEY";

	@Resource
	private TransactionUtils transactionUtils;

	@Resource
	private UploadJobService uploadJobService;

	@PostConstruct
	public void setupClient() {
		String awsSecret = System.getenv(AWS_SECRET_ENVVAR_NAME);
		String awsKey = System.getenv(AWS_KEY_ENVVAR_NAME);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Logging into Amazon S3 - AWS Key is {}", awsKey);
		}

		BasicAWSCredentials awsCreds = new BasicAWSCredentials("",
				"");
		amazonS3Client = new AmazonS3Client(awsCreds);
		LOG.info("Successfully logged into Amazon S3");
	}

	@Override
	public void saveAsset(InputStream inputStream, long contentLength, String userGuid,
			String assetGuid, AssetType assetType) {

		// create upload_job in DB - put RUNNING
		// send file in background thread
		// when background thread finishes upload, update database with DONE

		//TODO:
		// transactionUtilsService.executeInNewTransaction(() -> uploadJobService.setProcessing());

		String key = generateKey(userGuid, assetGuid, assetType);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Starting upload to Amazon S3, key is {}", key);
		}

		ObjectMetadata requestMetadata = new ObjectMetadata();
		requestMetadata.setContentLength(contentLength);

		PutObjectRequest putRequest = new PutObjectRequest(BUCKET_NAME, key, inputStream, requestMetadata);
		PutObjectResult result = amazonS3Client.putObject(putRequest);

		//TODO:
		// transactionUtilsService.executeInNewTransaction(() -> uploadJobService.setCompleted());

		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, key);
		String downloadLink = amazonS3Client.generatePresignedUrl(request).toString();

		if (LOG.isDebugEnabled()) {
			LOG.debug("Upload finished, ETag is {}", result.getETag());
			LOG.debug("Download link is {}", downloadLink);
		}
	}

	public String generateKey(String userGuid, String assetGuid, AssetType assetType) {
		return USERS_ROOT_KEY + "/" + userGuid + "/" + ASSETS_ROOT_KEY + "/" +
			    assetType.name().toLowerCase() + "/" + assetGuid;
	}



	////// ---------------------------------------------------------------------------------------
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

		if (LOG.isDebugEnabled()) {
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
		LOG.debug("");
		startTime = System.currentTimeMillis();
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, resourcePath);
		request.setGeneralProgressListener((event) -> {
			    System.out.println("Event: " + event.getEventType()  + ", bytes " + event.getBytesTransferred());
			    });
		System.out.println("Link: " + amazonS3Client.generatePresignedUrl(request));
		System.out.println("It took " + (System.currentTimeMillis() - startTime) + " to generate the link");

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

}
