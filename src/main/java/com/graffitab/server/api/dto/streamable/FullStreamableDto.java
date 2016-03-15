package com.graffitab.server.api.dto.streamable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FullStreamableDto extends StreamableDto {

	private Integer likersCount;
	private Integer commentsCount;
}
