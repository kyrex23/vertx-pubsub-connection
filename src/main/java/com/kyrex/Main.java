package com.kyrex;

import io.vertx.rxjava3.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(Main.class);
	private static final String DEFAULT_PUBSUB_EMULATOR_HOST = "localhost:8085";

	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();

		var host = Objects.requireNonNullElse(System.getenv("PUBSUB_EMULATOR_HOST"), DEFAULT_PUBSUB_EMULATOR_HOST);
		var pubSubService = new PubSubService(host);

		vertx.rxDeployVerticle(new MainVerticle(pubSubService))
			.doOnSuccess(id -> LOG.info("Server up with id: {}", id))
			.doOnError(err -> LOG.error("Server failed: {}", err.getMessage(), err))
			.subscribe();
	}

}
