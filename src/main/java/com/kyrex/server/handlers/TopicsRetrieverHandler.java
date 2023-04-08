package com.kyrex.server.handlers;

import com.google.pubsub.v1.Topic;
import com.kyrex.pubsub.services.TopicsRetrieverService;
import io.vertx.core.Handler;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class TopicsRetrieverHandler implements Handler<RoutingContext> {

	private final TopicsRetrieverService topicsRetrieverService;

	@Override
	public void handle(RoutingContext routingContext) {
		log.trace("Method={} - Path={}", routingContext.request().method(), routingContext.request().path());

		topicsRetrieverService.getAll()
			.subscribe(topics -> routingContext.response()
					.end(String.join("\n", topics.stream().map(Topic::getName).toList())),
				routingContext::fail);
	}

}
