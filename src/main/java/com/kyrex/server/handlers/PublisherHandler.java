package com.kyrex.server.handlers;

import com.kyrex.pubsub.services.MessageService;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class PublisherHandler implements Handler<RoutingContext> {

	private final MessageService messageService;

	@Override
	public void handle(RoutingContext routingContext) {
		String topicId = routingContext.pathParam("topicId");
		String message = routingContext.body().asJsonObject().getString("message");

		messageService.publish(topicId, message)
			.subscribe(pubsubMessage -> routingContext.response()
					.end("Message '" + pubsubMessage.getData().toStringUtf8() + "' published: OK"),
				routingContext::fail);
	}

}
