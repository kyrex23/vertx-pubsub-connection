package com.kyrex.server.handlers;

import com.kyrex.pubsub.services.TopicCreatorService;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class TopicCreatorHandler implements Handler<RoutingContext> {

	private final TopicCreatorService topicCreatorService;

	@Override
	public void handle(RoutingContext routingContext) {
		log.trace("Method={} - Path={}", routingContext.request().method(), routingContext.request().path());

		var topicId = routingContext.pathParam("topicId");
		topicCreatorService.create(topicId)
			.subscribe(topic -> routingContext.response().end(topic.getName()),
				routingContext::fail);
	}

}
