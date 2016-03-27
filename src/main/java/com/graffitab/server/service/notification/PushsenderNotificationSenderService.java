package com.graffitab.server.service.notification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.devsu.push.sender.service.async.AsyncAndroidPushService;
import com.graffitab.server.persistence.model.Device;
import com.graffitab.server.persistence.model.Device.OSType;
import com.graffitab.server.persistence.model.notification.Notification;
import com.graffitab.server.persistence.model.user.User;

@Service
public class PushsenderNotificationSenderService implements NotificationSenderService {

	private static Logger log = LogManager.getLogger();

	private AsyncAndroidPushService androidService;
	private GraffitabAsyncApplePushService appleService;

	private String PN_APNS_DEV_PASSWORD_ENVVAR_NAME = "PN_APNS_DEV_PASSWORD";
	private String PN_APNS_PROD_PASSWORD_ENVVAR_NAME = "PN_APNS_PROD_PASSWORD";
	private String PN_GCM_SENDER_KEY_ENVVAR_NAME = "PN_GCM_SENDER_KEY";

	private static boolean PN_IS_PRODUCTION_ENVIRONMENT = false;
	private static String PN_APNS_CERTIFICATE_PATH;
	private String PN_APNS_CERTIFICATE_PASSWORD_ENVVAR_NAME = PN_IS_PRODUCTION_ENVIRONMENT ? PN_APNS_PROD_PASSWORD_ENVVAR_NAME : PN_APNS_DEV_PASSWORD_ENVVAR_NAME;

	@PostConstruct
	public void setup() throws IOException {
		String apnsCertificatePassword = System.getenv(PN_APNS_CERTIFICATE_PASSWORD_ENVVAR_NAME);
		String gcmKey = System.getenv(PN_GCM_SENDER_KEY_ENVVAR_NAME);
		log.debug("Setting up Push Sender with GCM API key: {}", gcmKey);
		log.debug("Loading Push Sender APNS certificate path: {}", PN_APNS_CERTIFICATE_PATH);

		if (StringUtils.hasText(apnsCertificatePassword) && StringUtils.hasText(gcmKey)) {
			androidService = new AsyncAndroidPushService(gcmKey);
			try {
				Resource resource = new ClassPathResource("certificates/APNS_Certificate_" + (PN_IS_PRODUCTION_ENVIRONMENT ? "Prod" : "Dev") + ".p12");
				appleService = new GraffitabAsyncApplePushService(resource.getInputStream(), apnsCertificatePassword, PN_IS_PRODUCTION_ENVIRONMENT);
			} catch (IOException e) {
				log.error("Error reading APNS certificate", e);
			}
		} else {
			log.warn("apnsCertificatePassword and gcmKey are missing -- push notifications won't work");
		}
	}

	@Override
	public void sendNotification(User user, Notification notification) {
		if (log.isDebugEnabled()) {
			log.debug("[PUSHSENDER] About to send push notification to user " +
						user.getUsername() + " through Pushsender");
		}

		try {
			// Build PN content.
			// TODO: Configure content and metadata once apps are working and registering properly.
			String title = "GraffiTab";
			String content = buildContentForNotification(notification);
			Map<String, String> metadata = buildMetadataMapForNotification(notification);

			// Send PN to each of the user's devices.
			for (Device device : user.getDevices()) {
				if (device.getOsType() == OSType.ANDROID) {
					androidService.sendPush(title, content, metadata, device.getToken());
				}
				else if (device.getOsType() == OSType.IOS) {
					appleService.sendPush(title, content, metadata, device.getToken());
				}
			}
		} catch (Exception e) {
			String msg = "Error sending push notification through Pushsender";
			log.error(msg, e);
			throw new NotificationSenderException(msg, e);
		}
	}

	private Map<String, String> buildMetadataMapForNotification(Notification notification) {
		Map<String, String> metadata = new HashMap<String, String>();
		return metadata;
	}

	private String buildContentForNotification(Notification notification) {
		return "Hello! This is a push notification.";
	}
}
