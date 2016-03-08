package com.graffitab.server.persistence.model.streamable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("GRAFFITI")
public class StreamableGraffiti extends Streamable {

	private static final long serialVersionUID = 1L;

	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "longitude")
	private Double longitude;

	public StreamableGraffiti() {
		super(StreamableType.GRAFFITI);
	}

	public StreamableGraffiti(Double latitude, Double longitude) {
		super(StreamableType.GRAFFITI);

		this.latitude = latitude;
		this.longitude = longitude;
	}
}
