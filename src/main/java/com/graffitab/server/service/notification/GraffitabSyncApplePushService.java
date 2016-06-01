package com.graffitab.server.service.notification;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devsu.push.sender.service.sync.SyncPushServiceBase;
import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.PayloadBuilder;
import com.notnoop.exceptions.InvalidSSLConfig;
import com.notnoop.exceptions.RuntimeIOException;

public class GraffitabSyncApplePushService extends SyncPushServiceBase {

	/**
	 * Logger.
	 */
	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * The APNS service.
	 */
	private ApnsService apnsService;

	/**
	 * 3 param constructor.
	 * @param certificatePath The path of the p12 certificate file.
	 * @param certificatePassword The password for the p12 certificate.
	 * @param useProductionServer Indicates if the services uses a Production environment or a Sandbox environment.
	 * @throws RuntimeIOException
	 * @throws InvalidSSLConfig
	 */
	public GraffitabSyncApplePushService(String certificatePath, String certificatePassword, boolean useProductionServer) throws RuntimeIOException, InvalidSSLConfig {
		setDefaultValues();
		if (useProductionServer) {
			setupProductionServer(certificatePath, certificatePassword);
		}
		else {
			setupDevelopmentServer(certificatePath, certificatePassword);
		}
	}

	/**
	 * The same as the previous one, but using an InputStream
	 *
	 * @param certificate
	 * @param certificatePassword
	 * @param useProductionServer
	 * @throws RuntimeIOException
	 * @throws InvalidSSLConfig
	 */
	public GraffitabSyncApplePushService(InputStream certificate, String certificatePassword, boolean useProductionServer) throws RuntimeIOException, InvalidSSLConfig {
		setDefaultValues();
		if (useProductionServer) {
			setupProductionServer(certificate, certificatePassword);
		}
		else {
			setupDevelopmentServer(certificate, certificatePassword);
		}
	}


	/**
	 * Sets the default values.
	 */
	private void setDefaultValues() {
		pushEnabled = Defaults.PUSH_ENABLED;
	}

	/*
	 * @see com.rion18.pusher.service.sync.PushService#sendPush(java.lang.String, java.lang.String, java.util.Map, java.lang.String)
	 */
	@Override
	public boolean sendPush(String title, String message,
			Map<String, String> additionalFields, String token) throws Exception {
		if (!validateSingleData(log, message, token)) {
			return false;
		}
		apnsService.start();
		PayloadBuilder msgBuilder = generateBuilder(title, message, additionalFields);
		apnsService.push(token, msgBuilder.build());
		apnsService.stop();
		return true;
	}

	/*
	 * @see com.rion18.pusher.service.sync.PushService#sendPushInBulk(java.lang.String, java.lang.String, java.util.Map, java.lang.String[])
	 */
	@Override
	public boolean sendPushInBulk(String title, String message,
			Map<String, String> additionalFields, String... tokens) {
		if (!validateBulkData(log, message)) {
			return false;
		}
		apnsService.start();
		PayloadBuilder msgBuilder = generateBuilder(title, message, additionalFields);
		apnsService.push(Arrays.asList(tokens), msgBuilder.build());
		apnsService.stop();
		return true;
	}

	/**
	 * Default payload builder generator.
	 * @param title The push message title.
	 * @param message The push message content.
	 * @param additionalFields The additional fields sent on the push message.
	 * @return The payload builder.
	 */
	private PayloadBuilder generateBuilder(String title, String message, Map<String, String> additionalFields) {
		PayloadBuilder msgBuilder = APNS.newPayload().alertBody(message).sound(Defaults.SOUND);
		msgBuilder.badge(1);
		if (title != null) {
			msgBuilder.alertTitle(title);
		}
		if (additionalFields == null) {
			return msgBuilder;
		}
		msgBuilder.customFields(additionalFields);
		return msgBuilder;
	}

	/**
	 * Sets up the APNS Sandbox environment.
	 * @param certificatePath The path of the p12 certificate file.
	 * @param certificatePassword The password for the p12 certificate.
	 * @throws RuntimeIOException
	 * @throws InvalidSSLConfig
	 */
	public void setupDevelopmentServer(String certificatePath, String certificatePassword) throws RuntimeIOException, InvalidSSLConfig {
		apnsService = APNS.newService().withCert(certificatePath, certificatePassword).withSandboxDestination().build();
		apnsService.stop();
	}

	public void setupDevelopmentServer(InputStream certificate, String certificatePassword) throws RuntimeIOException, InvalidSSLConfig {
		apnsService = APNS.newService().withCert(certificate, certificatePassword).withSandboxDestination().build();
		apnsService.stop();
	}

	/**
	 * Sets up the APNS Production environment.
	 * @param certificatePath The path of the p12 certificate file.
	 * @param certificatePassword The password for the p12 certificate.
	 * @throws RuntimeIOException
	 * @throws InvalidSSLConfig
	 */
	public void setupProductionServer(String certificatePath, String certificatePassword) throws RuntimeIOException, InvalidSSLConfig {
		apnsService = APNS.newService().withCert(certificatePath, certificatePassword).withProductionDestination().build();
		apnsService.stop();
	}

	public void setupProductionServer(InputStream certificate, String certificatePassword) throws RuntimeIOException, InvalidSSLConfig {
		apnsService = APNS.newService().withCert(certificate, certificatePassword).withProductionDestination().build();
		apnsService.stop();
	}

	/**
	 * Class default values.
	 */
	private static class Defaults {
		private static final boolean PUSH_ENABLED = true;
		private static final String SOUND = "default";
	}

	public interface PayloadBuilderInterface {

		public PayloadBuilder payloadDidBuild(PayloadBuilder payloadBuilder);
	}
}
