package com.kyrex.handlers;

import com.kyrex.services.PubSubService;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.rxjava3.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.HttpStatus;

@Slf4j
@AllArgsConstructor
public class CreateTopicHandler implements Handler<RoutingContext> {

	private final PubSubService pubSubService;

	@Override
	public void handle(RoutingContext routingContext) {
		log.trace("Method={} - Path={}", routingContext.request().method(), routingContext.request().path());

		var topicId = routingContext.pathParam("topicId");
		pubSubService.createTopic(topicId)
			.subscribe(() -> routingContext.response()
					.putHeader("content-type", "application/json")
					.end(Json.encode(topicId)),
				err -> routingContext.response()
					.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
					.end("Error: " + err.getMessage()));
	}

}
