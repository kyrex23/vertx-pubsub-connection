package com.kyrex.server.handlers;

import com.google.pubsub.v1.Subscription;
import com.kyrex.pubsub.services.SubscriptionService;
import io.reactivex.rxjava3.core.Single;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RequestBody;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


@Slf4j
@AllArgsConstructor
public class CreateSubscriptionHandler implements Handler<RoutingContext> {

	private final SubscriptionService subscriptionService;

	@Override
	public void handle(RoutingContext routingContext) {
		String topicId = routingContext.pathParam("topicId");
		String subscriptionId = routingContext.pathParam("subscriptionId");

		var pushEndpoint = Optional.ofNullable(routingContext.body())
			.map(RequestBody::asJsonObject)
			.map(jsonObject -> jsonObject.getString("push_endpoint"));

		Single<Subscription> subscriptionSingle = pushEndpoint
			.map(endpoint -> subscriptionService.create(topicId, subscriptionId, endpoint))
			.orElse(subscriptionService.create(topicId, subscriptionId));

		log.debug("Path={} - Body={}", routingContext.request().path(), pushEndpoint.orElse(null));

		subscriptionSingle.subscribe(subscription -> routingContext.response()
				.end("Subscription '" + subscription.getName() + "' created OK"),
			routingContext::fail);
	}

}
