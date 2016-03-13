package com.graffitab.server.service;

import java.io.InputStream;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.graffitab.server.api.dto.streamable.StreamableGraffitiDto;
import com.graffitab.server.persistence.dao.HibernateDaoImpl;
import com.graffitab.server.persistence.model.Asset;
import com.graffitab.server.persistence.model.Asset.AssetType;
import com.graffitab.server.persistence.model.User;
import com.graffitab.server.persistence.model.streamable.Streamable;
import com.graffitab.server.persistence.model.streamable.StreamableGraffiti;
import com.graffitab.server.service.store.DatastoreService;
import com.graffitab.server.service.user.UserService;

@Service
public class StreamableService {

	@Resource
	private UserService userService;

	@Resource
	private DatastoreService datastoreService;

	@Resource
	private TransactionUtils transactionUtils;

	@Resource
	private HibernateDaoImpl<Streamable, Long> streamableDao;

	public Streamable createStreamableGraffiti(StreamableGraffitiDto streamableGraffitiDto, InputStream assetInputStream, long contentLength) {
		Asset assetToAdd = Asset.asset(AssetType.IMAGE);

		User user = transactionUtils.executeInTransactionWithResult(() -> {
			User currentUser = userService.getCurrentUser();
			return currentUser;
		});

		datastoreService.saveAsset(assetInputStream, contentLength, user.getGuid(), assetToAdd.getGuid());

		Streamable streamable = transactionUtils.executeInTransactionWithResult(() -> {
			User currentUser = userService.getCurrentUser();
			Streamable streamableGraffiti = new StreamableGraffiti(streamableGraffitiDto.getLatitude(),
														   streamableGraffitiDto.getLongitude(),
														   streamableGraffitiDto.getRoll(),
														   streamableGraffitiDto.getYaw(),
														   streamableGraffitiDto.getPitch());
			streamableGraffiti.setAsset(assetToAdd);
			currentUser.getStreamables().add(streamableGraffiti);

			return streamableGraffiti;
		});

		return streamable;
	}
}
