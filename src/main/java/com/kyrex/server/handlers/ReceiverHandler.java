package com.kyrex.server.handlers;

import com.kyrex.pubsub.services.MessageService;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class ReceiverHandler implements Handler<RoutingContext> {

	private final MessageService messageService;

	@Override
	public void handle(RoutingContext routingContext) {
		var subscriptionId = routingContext.pathParam("subscriptionId");

		messageService.retrieve(subscriptionId)
			.subscribe(pubsubMessage -> routingContext.response()
					.end("Message received: " + pubsubMessage.getData().toStringUtf8()),
				routingContext::fail);
	}

}
