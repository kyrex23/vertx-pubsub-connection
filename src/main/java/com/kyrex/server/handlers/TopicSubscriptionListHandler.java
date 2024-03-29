package com.kyrex.server.handlers;

import com.kyrex.pubsub.services.SubscriptionService;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class TopicSubscriptionListHandler implements Handler<RoutingContext> {

	private final SubscriptionService subscriptionService;

	@Override
	public void handle(RoutingContext routingContext) {
		log.trace("Method={} - Path={}", routingContext.request().method(), routingContext.request().path());

		String topicId = routingContext.pathParam("topicId");
		subscriptionService.get(topicId)
			.subscribe(subscriptions -> routingContext.response().end(String.join("\n", subscriptions)),
				routingContext::fail);
	}

}
