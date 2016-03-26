package com.graffitab.server.service.image;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;
import com.graffitab.server.service.store.DatastoreService;
import com.graffitab.server.util.GuidGenerator;
import com.mortennobel.imagescaling.MultiStepRescaleOp;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ImageUtilsService {

	@Resource
	private DatastoreService datastoreService;

	@Value("${filesystem.tempDir:/tmp}")
	private String FILE_SYSTEM_TEMP_ROOT;

	private static Integer STANDARD_IMG_WIDTH = 1024;
	private static Integer STANDARD_IMG_HEIGHT = 768;

	private static Integer THUMBNAIL_IMG_WIDTH = 200;
	private static Integer THUMBNAIL_IMG_HEIGHT = 200;

	private static String OUTPUT_IMG_FORMAT = "png";

	public static final String ASSET_THUMBNAIL_SUFFIX = "_thumb";

	@PostConstruct
	public void init() {
		File file = new File(FILE_SYSTEM_TEMP_ROOT);
		if (!file.exists()) {
			file.mkdirs();
		}

		if (log.isDebugEnabled()) {
			log.debug("Temporary filesystem root is " + FILE_SYSTEM_TEMP_ROOT);
		}
	}

	public ImageSizes generateAndUploadImagesForAsset(InputStream imageInputStream, String assetGuid) {

		File imageTempFile;
		ScaledImage standardImage;
		long standardImageSize;
		ScaledImage thumbnailImage;
		long thumbnailImageSize;

		try {

			imageTempFile = transferImageToTemporaryArea(imageInputStream);

			standardImage = generateScaledImage(imageTempFile, STANDARD_IMG_WIDTH, STANDARD_IMG_HEIGHT, "");
			standardImageSize = standardImage.getScaledImage().length();

			if (log.isDebugEnabled()) {
				log.debug("Standard image generated for asset GUID with size {} bytes", standardImageSize);
			}

			thumbnailImage = generateScaledImage(imageTempFile, THUMBNAIL_IMG_WIDTH, THUMBNAIL_IMG_HEIGHT, "thumb");
			thumbnailImageSize = thumbnailImage.getScaledImage().length();

			if (log.isDebugEnabled()) {
				log.debug("Thumbnail image generated for asset GUID with size {} bytes", thumbnailImageSize);
			}
		} catch (Exception e1) {
			log.error("Error generating images", e1);
			throw new RestApiException("Error generating images");
		}

		try {

			// Upload standard image
			FileInputStream standardImageInputStream = new FileInputStream(standardImage.getScaledImage());
			datastoreService.saveAsset(standardImageInputStream, standardImageSize, assetGuid);

			// Upload thumbnail
			FileInputStream thumbnailImageInputStream = new FileInputStream(thumbnailImage.getScaledImage());
			datastoreService.saveAsset(thumbnailImageInputStream, thumbnailImageSize, assetGuid + "_thumb");

			ImageSizes imageSizes = new ImageSizes();
			imageSizes.setWidth(standardImage.getScaledWidth());
			imageSizes.setHeight(standardImage.getScaledHeight());
			imageSizes.setThumbnailWidth(thumbnailImage.getScaledWidth());
			imageSizes.setThumbnailHeight(thumbnailImage.getScaledHeight());
			return imageSizes;
		} catch (FileNotFoundException e) {
			log.error("Error generating images", e);
			throw new RestApiException("Error generating images");
		} finally {
			imageTempFile.delete();
			standardImage.getScaledImage().delete();
			thumbnailImage.getScaledImage().delete();
		}
	}

	private File getTemporaryFile() {
		return new File(FILE_SYSTEM_TEMP_ROOT + File.separatorChar + GuidGenerator.generate());
	}

	private File transferImageToTemporaryArea(InputStream inputStream) {
		File tempFile = getTemporaryFile();

		if (log.isDebugEnabled()) {
			log.debug("Transferring file to temporary store {}", tempFile.getAbsolutePath());
		}

		try {

			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(tempFile));
			FileCopyUtils.copy(inputStream, stream);
			stream.close();
			if (log.isDebugEnabled()) {
				log.debug("Transferring file to temporary completed successfully", tempFile.getAbsolutePath());
			}
			return tempFile;
		} catch (FileNotFoundException e) {
			log.error("File not found: " + tempFile.getAbsolutePath(), e);
			throw new RestApiException(ResultCode.GENERAL_ERROR, "Cannot transfer to temporary file");
		} catch (IOException e) {
			log.error("General error transferring file",e);
			throw new RestApiException(ResultCode.GENERAL_ERROR, "Cannot transfer to temporary file");
		}
	}

	private ScaledImage generateScaledImage(File originalImageTempFile, Integer width, Integer height, String suffix) throws IOException {
		FileInputStream fis = new FileInputStream(originalImageTempFile);
		String imageFileName = 	FILE_SYSTEM_TEMP_ROOT + File.separatorChar +
										GuidGenerator.generate() +
										( (suffix != null && StringUtils.hasText(suffix)) ? "_" + suffix : "");
		ScaledImage scaledImage = scaleImage(originalImageTempFile, fis, width, height, imageFileName);
		return scaledImage;
	}

	private ScaledImage scaleImage(File originalImageTempFile, InputStream sourceImageInputStream, Integer requestedWidth, Integer requestedHeight, String scaledImageTempFileName) throws IOException {

		File tempFile = new File(scaledImageTempFileName);

		// Source Image
		BufferedImage sourceImage = ImageIO.read(sourceImageInputStream);

		// Calculate scaled height and width preserving the aspect ratio.
		int sourceHeight = sourceImage.getHeight();
		int sourceWidth = sourceImage.getWidth();
		int outputHeight= sourceHeight;
		int outputWidth = sourceWidth;

		double aspectRatio = (double) sourceHeight / (double) sourceWidth;

		requestedWidth = requestedWidth != null ? requestedWidth : sourceWidth;
		requestedHeight = requestedHeight != null ? requestedHeight : sourceHeight;

		// Scale only if the original dimensions are greater than the requested ones
		if (sourceWidth > requestedWidth || sourceHeight > requestedHeight) {

			if (log.isDebugEnabled()) {
				log.debug("Original image dimensions are w=: " + sourceWidth + ", h=" + sourceHeight);
				log.debug("Requested image dimensions are w=: " + requestedWidth + ", h=" + requestedHeight);
			}

			// Fix the width, calculate height
			outputWidth = (sourceWidth > requestedWidth) ? requestedWidth : sourceWidth;
			outputHeight = (int) (aspectRatio * outputWidth + 0.5); // + 0.5 to round.

			// If the calculated height is over the requested height value, fix it and calculate width
			if (outputHeight > requestedHeight) {
				outputHeight = (sourceHeight > requestedHeight) ? requestedHeight : sourceHeight;
				outputWidth = (int) (outputHeight / aspectRatio + 0.5); // + 0.5 to round.
			}

			// TODO: is this needed here?
			if (outputWidth > requestedWidth) {
				outputWidth = requestedWidth;
			}

			if (log.isDebugEnabled()) {
				log.debug("Returning image with dimensions w=: " + outputWidth + ", h=" + outputHeight);
			}

			if (outputWidth > requestedWidth || outputHeight > requestedHeight ) {
				String msg = "Invalid image scaling -- width or height are over the requested values: outputWidth: " + outputWidth + ", "
						+ "outputHeight: " + outputHeight + ", sourceWidth: " + sourceWidth + ", sourceHeight: " + sourceHeight +
						", requestedWidth: " + requestedWidth + ", requestedHeight: " + requestedHeight;
				throw new IllegalStateException(msg);
			}

			long startTime = System.currentTimeMillis();

			MultiStepRescaleOp resampleOp = new MultiStepRescaleOp(outputWidth, outputHeight);
			BufferedImage scaledImage = resampleOp.filter(sourceImage, null);

			long endTime = System.currentTimeMillis();

			if (log.isDebugEnabled()) {
				log.debug("Image scaling took: " + (endTime - startTime) + " ms");
			}

			ImageIO.write(scaledImage, OUTPUT_IMG_FORMAT, tempFile);
		}
		else {
			// Image does not require scaling so use the original file instead.
			tempFile = originalImageTempFile;
		}

		ScaledImage scaledImage = new ScaledImage();
		scaledImage.setScaledImage(tempFile);
		scaledImage.setScaledWidth(outputWidth);
		scaledImage.setScaledHeight(outputHeight);

		return scaledImage;
	}
}
