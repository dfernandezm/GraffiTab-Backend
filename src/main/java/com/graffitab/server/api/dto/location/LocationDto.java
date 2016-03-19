package com.graffitab.server.api.dto.location;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class LocationDto {

	private Long id;
	private String address;
	private Double latitude;
	private Double longitude;
}