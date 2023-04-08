package com.kyrex.server;

import com.kyrex.pubsub.adapters.PubSubAdapter;
import com.kyrex.pubsub.services.TopicDeleteService;
import com.kyrex.server.handlers.TopicCreatorHandler;
import com.kyrex.server.handlers.TopicDeleteHandler;
import com.kyrex.server.handlers.TopicsRetrieverHandler;
import com.kyrex.pubsub.services.TopicCreatorService;
import com.kyrex.pubsub.services.TopicsRetrieverService;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.Router;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class MainVerticle extends AbstractVerticle {

	private final String host;
	private final String projectId;

	@Override
	public Completable rxStart() {
		Router router = Router.router(vertx);

		PubSubAdapter pubSubAdapter = new PubSubAdapter(host);
		TopicsRetrieverService topicsRetrieverService = new TopicsRetrieverService(pubSubAdapter, projectId);
		TopicsRetrieverHandler topicsRetrieverHandler = new TopicsRetrieverHandler(topicsRetrieverService);
		router.get("/topics").handler(topicsRetrieverHandler);

		TopicCreatorService topicCreatorService = new TopicCreatorService(pubSubAdapter, projectId);
		TopicCreatorHandler topicCreatorHandler = new TopicCreatorHandler(topicCreatorService);
		router.post("/topics/:topicId").handler(topicCreatorHandler);

		TopicDeleteService topicDeleteService = new TopicDeleteService(pubSubAdapter, projectId);
		TopicDeleteHandler topicDeleteHandler = new TopicDeleteHandler(topicDeleteService);
		router.delete("/topics/:topicId").handler(topicDeleteHandler);

		return vertx.createHttpServer()
			.requestHandler(router)
			.rxListen(8080)
			.ignoreElement();
	}

}
