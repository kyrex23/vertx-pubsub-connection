package com.kyrex;

import com.kyrex.services.PubSubService;
import io.vertx.rxjava3.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class Main {

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
			.subscribe(id -> log.info("Server up with id: {}", id),
				err -> log.error("Server failed: {}", err.getMessage()));
	}

}
