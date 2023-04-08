package com.kyrex.server.handlers;

import com.kyrex.pubsub.services.SubscriptionService;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class CreateSubscriptionHandler implements Handler<RoutingContext> {

	private final SubscriptionService subscriptionService;

	@Override
	public void handle(RoutingContext routingContext) {
		String topicId = routingContext.pathParam("topicId");
		String subscriptionId = routingContext.pathParam("subscriptionId");

		subscriptionService.create(topicId, subscriptionId)
			.subscribe(subscription -> routingContext.response()
					.end("Subscription '" + subscription.getName() + "' created OK"),
				routingContext::fail);
	}

}
