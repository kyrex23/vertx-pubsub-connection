package com.kyrex.server.handlers;

import com.kyrex.pubsub.services.TopicDeleteService;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class TopicDeleteHandler implements Handler<RoutingContext> {

	private final TopicDeleteService topicDeleteService;

	@Override
	public void handle(RoutingContext routingContext) {
		log.trace("Method={} - Path={}", routingContext.request().method(), routingContext.request().path());

		var topicId = routingContext.pathParam("topicId");
		topicDeleteService.deleteTopic(topicId)
			.subscribe(() -> routingContext.response().end("Topic '" + topicId + "' deleted OK"),
				routingContext::fail);
	}

}
