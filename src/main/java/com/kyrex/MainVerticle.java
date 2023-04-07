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

		router.get("/projects/:projectId/topics").handler(context -> {
			String project = context.pathParam("projectId");

			LOG.info("GET {} - Params: {}", context.request().path(), context.pathParams());

			pubSubService.getTopics(project)
				.subscribe(topics -> context.response()
						.end(String.join("\n", topics.stream().map(Topic::getName).toList())),
					err -> context.response().end("Error: " + err.getMessage()));
		});

		router.post("/projects/:projectId/topics/:topicId").handler(context -> {
			String project = context.pathParam("projectId");
			String topic = context.pathParam("topicId");

			LOG.info("POST {} - Params: {}", context.request().path(), context.pathParams());

			pubSubService.createTopic(project, topic)
				.subscribe(() -> context.response().end("Topic '" + topic + "' created in project " + project),
					err -> context.response().end("Error: " + err.getMessage()));
		});

		return vertx.createHttpServer()
			.requestHandler(router)
			.rxListen(8080)
			.ignoreElement();
	}

}
