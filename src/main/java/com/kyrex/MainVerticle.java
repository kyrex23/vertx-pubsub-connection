package com.kyrex;

import com.google.pubsub.v1.Topic;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

	private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

	private final PubSubService pubSubService;

	public MainVerticle(PubSubService pubSubService) {
		this.pubSubService = pubSubService;
	}

	@Override
	public Completable rxStart() {
		Router router = Router.router(vertx);

		router.get("/topics").handler(context -> {
			LOG.trace("GET {}", context.request().path());

			pubSubService.getTopics()
				.subscribe(topics -> context.response()
						.end(String.join("\n", topics.stream().map(Topic::getName).toList())),
					err -> context.response().end("Error: " + err.getMessage()));
		});

		router.post("/topics/:topicId").handler(context -> {
			LOG.trace("POST {} - params: {}", context.request().path(), context.pathParams());
			String topicId = context.pathParam("topicId");

			pubSubService.createTopic(topicId)
				.subscribe(() -> context.response().end("Topic '" + topicId + "' created: OK"),
					err -> context.response().end("Error: " + err.getMessage()));
		});

		return vertx.createHttpServer()
			.requestHandler(router)
			.rxListen(8080)
			.ignoreElement();
	}

}
