package com.kyrex;

import io.vertx.rxjava3.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private static final String DEFAULT_PUBSUB_EMULATOR_HOST = "localhost:8085";
	private static final String DEFAULT_PUBSUB_PROJECT_ID = "local-project";

	public static void main(String[] args) throws IOException {
		Vertx vertx = Vertx.vertx();

		Properties properties = new Properties();
		properties.load(Main.class.getResourceAsStream("/config.properties"));

		String host = properties.getProperty("PUBSUB_EMULATOR_HOST", DEFAULT_PUBSUB_EMULATOR_HOST);
		String project = properties.getProperty("PUBSUB_PROJECT_ID", DEFAULT_PUBSUB_PROJECT_ID);
		PubSubService pubSubService = new PubSubService(host, project);

		vertx.rxDeployVerticle(new MainVerticle(pubSubService))
			.doOnSuccess(id -> LOG.info("Server up with id: {}", id))
			.doOnError(err -> LOG.error("Server failed: {}", err.getMessage(), err))
			.subscribe();
	}

}
