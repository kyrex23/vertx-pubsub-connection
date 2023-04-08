package com.kyrex.server.handlers;

import com.kyrex.pubsub.services.SubscriptionService;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class DeleteSubscriptionHandler implements Handler<RoutingContext> {

	private final SubscriptionService subscriptionService;

	@Override
	public void handle(RoutingContext routingContext) {
		String subscriptionId = routingContext.pathParam("subscriptionId");
		log.debug("Deleting subscriptionId={}", subscriptionId);

		subscriptionService.delete(subscriptionId)
			.subscribe(() -> routingContext.response().end("Done! subscriptionId='" + subscriptionId + "' deleted"),
				routingContext::fail);
	}

}
