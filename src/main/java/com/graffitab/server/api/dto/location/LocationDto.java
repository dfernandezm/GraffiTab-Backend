package com.graffitab.server.api.dto.location;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Data
@JsonInclude(Include.NON_NULL)
public class LocationDto {

	private Long id;
	private String address;
	private Double latitude;
	private Double longitude;
	private String createdOn;
	private String updatedOn;
}
