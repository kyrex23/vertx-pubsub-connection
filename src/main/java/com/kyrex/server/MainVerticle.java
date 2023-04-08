package com.kyrex.server;

import com.kyrex.pubsub.adapters.PubSubAdapter;
import com.kyrex.pubsub.services.*;
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

		// Topic Management
		// ----------------
		var topicService = new TopicService(pubSubAdapter, projectId);

		var topicsRetrieverHandler = new TopicsRetrieverHandler(topicService);
		router.get("/topics").handler(topicsRetrieverHandler);

		var topicCreatorHandler = new TopicCreatorHandler(topicService);
		router.post("/topics/:topicId").handler(topicCreatorHandler);

		var topicDeleteHandler = new TopicDeleteHandler(topicService);
		router.delete("/topics/:topicId").handler(topicDeleteHandler);

		// Subscription Management
		// -----------------------
		var subscriptionService = new SubscriptionService(pubSubAdapter, projectId);

		var subscriptionListHandler = new SubscriptionListHandler(subscriptionService);
		router.get("/subscriptions").handler(subscriptionListHandler);

		var topicSubscriptionListHandler = new TopicSubscriptionListHandler(subscriptionService);
		router.get("/topics/:topicId/subscriptions").handler(topicSubscriptionListHandler);

		var createSubscriptionHandler = new CreateSubscriptionHandler(subscriptionService);
		router.post("/topics/:topicId/subscriptions/:subscriptionId").handler(createSubscriptionHandler);

		var deleteSubscriptionHandler = new DeleteSubscriptionHandler(subscriptionService);
		router.delete("/subscriptions/:subscriptionId").handler(deleteSubscriptionHandler);

		return vertx.createHttpServer()
			.requestHandler(router)
			.rxListen(8080)
			.ignoreElement();
	}

}
