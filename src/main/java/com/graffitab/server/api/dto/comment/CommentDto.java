package com.graffitab.server.api.dto.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.graffitab.server.api.dto.streamable.StreamableDto;
import com.graffitab.server.api.dto.user.UserDto;

import lombok.Data;

@Data
@JsonInclude(Include.NON_NULL)
public class CommentDto {

	private Long id;
	private StreamableDto streamable;
	private UserDto user;
	private String text;
	private String date;
	private String editDate;
}
