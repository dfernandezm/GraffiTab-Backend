package com.graffitab.server.api.dto.comment.result;

import com.graffitab.server.api.dto.comment.CommentDto;

import lombok.Data;

@Data
public class CreateCommentResult {

	private CommentDto comment;
}
