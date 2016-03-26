package com.graffitab.server.service.image;

import java.io.File;

import lombok.Data;

@Data
public class ScaledImage {

	private File scaledImage;
	private Integer scaledWidth;
	private Integer scaledHeight;
}
