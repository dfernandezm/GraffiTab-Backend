package com.graffitab.server.api.controller.streamable;

import javax.annotation.Resource;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.graffitab.server.api.controller.user.UserStatusRequired;
import com.graffitab.server.api.dto.streamable.FullStreamableDto;
import com.graffitab.server.api.dto.streamable.result.GetFullStreamableResult;
import com.graffitab.server.api.mapper.OrikaMapper;
import com.graffitab.server.persistence.model.User.AccountStatus;
import com.graffitab.server.persistence.model.streamable.Streamable;
import com.graffitab.server.service.StreamableService;

@RestController
@RequestMapping("/api/streamables")
public class StreamableApiController {

	@Resource
	private StreamableService streamableService;

	@Resource
	private OrikaMapper mapper;

	@RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public GetFullStreamableResult getStreamable(@PathVariable("id") Long streamableId) {
		GetFullStreamableResult getFullStreamableResult = new GetFullStreamableResult();
		Streamable streamable = streamableService.findStreamableById(streamableId);
		getFullStreamableResult.setStreamable(mapper.map(streamable, FullStreamableDto.class));
		return getFullStreamableResult;
	}

	@RequestMapping(value = {"/{id}/likes"}, method = RequestMethod.POST)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public GetFullStreamableResult like(@PathVariable("id") Long streamableId) {
		GetFullStreamableResult getFullStreamableResult = new GetFullStreamableResult();
		Streamable toLike = streamableService.like(streamableId);
		getFullStreamableResult.setStreamable(mapper.map(toLike, FullStreamableDto.class));
		return getFullStreamableResult;
	}

	@RequestMapping(value = {"/{id}/likes"}, method = RequestMethod.DELETE)
	@Transactional
	@UserStatusRequired(value = AccountStatus.ACTIVE)
	public GetFullStreamableResult unlike(@PathVariable("id") Long streamableId) {
		GetFullStreamableResult getFullStreamableResult = new GetFullStreamableResult();
		Streamable toUnlike = streamableService.unlike(streamableId);
		getFullStreamableResult.setStreamable(mapper.map(toUnlike, FullStreamableDto.class));
		return getFullStreamableResult;
	}
}
