package com.kyrex.server.handlers;

import com.google.pubsub.v1.Subscription;
import com.kyrex.pubsub.services.SubscriptionService;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class SubscriptionListHandler implements Handler<RoutingContext> {

	private final SubscriptionService subscriptionService;

	@Override
	public void handle(RoutingContext routingContext) {
		log.trace("Method={} - Path={}", routingContext.request().method(), routingContext.request().path());

		subscriptionService.getAll()
			.subscribe(subscriptions -> routingContext.response()
					.end(String.join("\n", subscriptions.stream().map(Subscription::getName).toList())),
				routingContext::fail);
	}

}
