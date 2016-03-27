package com.graffitab.server.api.dto.comment.result;

import lombok.Data;

import com.graffitab.server.api.dto.comment.CommentDto;

@Data
public class CreateCommentResult {

	private CommentDto comment;
}
