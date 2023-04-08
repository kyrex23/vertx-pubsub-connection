package com.kyrex.server;

import com.kyrex.pubsub.adapters.PubSubAdapter;
import com.kyrex.pubsub.services.ListSubscriptionsService;
import com.kyrex.pubsub.services.TopicCreatorService;
import com.kyrex.pubsub.services.TopicDeleteService;
import com.kyrex.pubsub.services.TopicsRetrieverService;
import com.kyrex.server.handlers.*;
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

		var pubSubAdapter = new PubSubAdapter(host);
		var topicsRetrieverService = new TopicsRetrieverService(pubSubAdapter, projectId);
		var topicsRetrieverHandler = new TopicsRetrieverHandler(topicsRetrieverService);
		router.get("/topics").handler(topicsRetrieverHandler);

		var topicCreatorService = new TopicCreatorService(pubSubAdapter, projectId);
		var topicCreatorHandler = new TopicCreatorHandler(topicCreatorService);
		router.post("/topics/:topicId").handler(topicCreatorHandler);

		var topicDeleteService = new TopicDeleteService(pubSubAdapter, projectId);
		var topicDeleteHandler = new TopicDeleteHandler(topicDeleteService);
		router.delete("/topics/:topicId").handler(topicDeleteHandler);

		var listSubscriptionsService = new ListSubscriptionsService(pubSubAdapter, projectId);
		var subscriptionListHandler = new SubscriptionListHandler(listSubscriptionsService);
		router.get("/subscriptions").handler(subscriptionListHandler);

		var topicSubscriptionListHandler = new TopicSubscriptionListHandler(listSubscriptionsService);
		router.get("/topics/:topicId/subscriptions").handler(topicSubscriptionListHandler);

		return vertx.createHttpServer()
			.requestHandler(router)
			.rxListen(8080)
			.ignoreElement();
	}

}
