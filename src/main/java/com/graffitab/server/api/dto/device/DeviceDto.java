package com.graffitab.server.api.dto.device;

import lombok.Data;

import com.graffitab.server.persistence.model.Device.OSType;

@Data
public class DeviceDto {

	private String token;
	private OSType osType;
}
