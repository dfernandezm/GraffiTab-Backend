package com.graffitab.server.service.streamable;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graffitab.server.api.dto.ListItemsResult;
import com.graffitab.server.api.dto.comment.CommentDto;
import com.graffitab.server.api.errors.RestApiException;
import com.graffitab.server.api.errors.ResultCode;
import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.Comment;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.persistence.model.streamable.Streamable;
import com.graffitab.server.service.PagingService;
import com.graffitab.server.service.TransactionUtils;
import com.graffitab.server.service.notification.NotificationService;
import com.graffitab.server.service.user.UserService;

@Service
public class CommentService {

	@Resource
	private UserService userService;

	@Resource
	private NotificationService notificationService;

	@Resource
	private TransactionUtils transactionUtils;

	@Resource
	private PagingService pagingService;

	@Resource
	private HibernateDaoImpl<Streamable, Long> streamableDao;

	@Resource
	private HibernateDaoImpl<Comment, Long> commentDao;

	@Transactional
	public Comment postComment(Long streamableId, String text) {
		Streamable streamable = findStreamableById(streamableId);

		if (streamable != null) {
			User currentUser = userService.getCurrentUser();

			Comment comment = Comment.comment();
			comment.setUser(currentUser);
			comment.setText(text);
			streamable.getComments().add(comment);

			Comment persisted = commentDao.persist(comment);
			if (!streamable.getUser().equals(currentUser)) {
				// Send notification.
				notificationService.addCommentNotification(streamable.getUser(), currentUser, streamable, persisted);
			}

			return persisted;
		} else {
			throw new RestApiException(ResultCode.STREAMABLE_NOT_FOUND, "Streamable with id " + streamableId + " not found");
		}
	}

	@Transactional
	public void deleteComment(Long streamableId, Long commentId) {
		Streamable streamable = findStreamableById(streamableId);

		if (streamable != null) {
			Comment toDelete = findCommentById(commentId);

			if (toDelete != null) {
				// TODO: This will fail, as there is a link between the notifications and activities.
				streamable.getComments().remove(toDelete);
			}
			else {
				throw new RestApiException(ResultCode.COMMENT_NOT_FOUND, "Comment with id " + commentId + " not found");
			}
		} else {
			throw new RestApiException(ResultCode.STREAMABLE_NOT_FOUND, "Streamable with id " + streamableId + " not found");
		}
	}

	@Transactional
	public Comment editComment(Long streamableId, Long commentId, String newText) {
		Streamable streamable = findStreamableById(streamableId);

		if (streamable != null) {
			Comment toEdit = findCommentById(commentId);

			if (toEdit != null) {
				User currentUser = userService.getCurrentUser();

				if (currentUser.equals(toEdit.getUser())) {
					toEdit.setText(newText);
					toEdit.setEditDate(new DateTime());
					return toEdit;
				}
				else {
					throw new RestApiException(ResultCode.USER_NOT_OWNER, "The comment with id " + commentId + " cannot be edited by user with id " + currentUser.getId());
				}
			}
			else {
				throw new RestApiException(ResultCode.COMMENT_NOT_FOUND, "Comment with id " + commentId + " not found");
			}
		} else {
			throw new RestApiException(ResultCode.STREAMABLE_NOT_FOUND, "Streamable with id " + streamableId + " not found");
		}
	}

	@Transactional
	public ListItemsResult<CommentDto> getCommentsResult(Long streamableId, Integer offset, Integer count) {
		Streamable streamable = findStreamableById(streamableId);

		if (streamable != null) {
			Query query = streamableDao.createQuery(
					"select c "
				  + "from Streamable s "
				  + "join s.comments c "
				  + "where s = :currentStreamable");
			query.setParameter("currentStreamable", streamable);

			return pagingService.getPagedItems(Comment.class, CommentDto.class, offset, count, query);
		} else {
			throw new RestApiException(ResultCode.STREAMABLE_NOT_FOUND, "Streamable with id " + streamableId + " not found");
		}
	}

	@Transactional(readOnly = true)
	public Streamable findStreamableById(Long id) {
		return streamableDao.find(id);
	}

	@Transactional(readOnly = true)
	public Comment findCommentById(Long id) {
		return commentDao.find(id);
	}
}
