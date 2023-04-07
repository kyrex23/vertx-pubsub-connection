package com.kyrex;

import com.kyrex.handlers.CreateTopicHandler;
import com.kyrex.handlers.GetTopicsHandler;
import com.kyrex.services.PubSubService;
import io.reactivex.rxjava3.core.Completable;
import io.vertx.rxjava3.core.AbstractVerticle;
import io.vertx.rxjava3.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainVerticle extends AbstractVerticle {

	private final PubSubService pubSubService;

	public MainVerticle(PubSubService pubSubService) {
		this.pubSubService = pubSubService;
	}

	@Override
	public Completable rxStart() {
		Router router = Router.router(vertx);

		GetTopicsHandler getTopicsHandler = new GetTopicsHandler(pubSubService);
		router.get("/topics").handler(getTopicsHandler);

		CreateTopicHandler createTopicHandler = new CreateTopicHandler(pubSubService);
		router.post("/topics/:topicId").handler(createTopicHandler);

		return vertx.createHttpServer()
			.requestHandler(router)
			.rxListen(8080)
			.ignoreElement();
	}

}
