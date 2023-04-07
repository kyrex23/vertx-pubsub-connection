package com.kyrex.handlers;

import com.google.pubsub.v1.Topic;
import com.kyrex.services.PubSubService;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;

@Slf4j
@AllArgsConstructor
public class GetTopicsHandler implements Handler<RoutingContext> {

	private final PubSubService pubSubService;

	@Override
	public void handle(RoutingContext routingContext) {
		log.trace("GET {}", routingContext.request().path());

		pubSubService.getTopics()
			.doOnSuccess(topics -> log.debug("Retrieved topics: {}", topics.stream().map(Topic::getName).toList()))
			.doOnError(err -> log.warn("Error retrieving topics: {}", err.getMessage()))
			.subscribe(topics -> routingContext.response()
					.putHeader("content-type", "application/json")
					.end(Json.encode(topics.stream().map(Topic::getName).toList())),
				err -> routingContext.response()
					.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
					.end("Error: " + err));
	}

}
