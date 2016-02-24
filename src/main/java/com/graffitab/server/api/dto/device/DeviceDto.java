package com.graffitab.server.api.dto.device;

import com.graffitab.server.persistence.model.Device.OSType;

import lombok.Data;

@Data
public class DeviceDto {

	private String token;
	private OSType osType;
}
