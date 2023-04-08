package com.kyrex.server.handlers;

import com.kyrex.pubsub.services.TopicService;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class TopicCreatorHandler implements Handler<RoutingContext> {

	private final TopicService topicService;

	@Override
	public void handle(RoutingContext routingContext) {
		log.trace("Method={} - Path={}", routingContext.request().method(), routingContext.request().path());

		var topicId = routingContext.pathParam("topicId");
		topicService.create(topicId)
			.subscribe(topic -> routingContext.response().end(topic.getName()),
				routingContext::fail);
	}

}
