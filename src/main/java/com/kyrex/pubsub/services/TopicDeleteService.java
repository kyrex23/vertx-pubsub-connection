package com.kyrex.pubsub.services;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.TopicName;
import com.kyrex.pubsub.adapters.PubSubAdapter;
import io.reactivex.rxjava3.core.Completable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class TopicDeleteService {

	private final PubSubAdapter pubSubAdapter;
	private final String projectId;

	public Completable deleteTopic(String topicId) {
		log.trace("Deleting topic '{}' from project '{}'", topicId, projectId);

		return Completable.defer(() -> {
			try (TopicAdminClient topicAdminClient = pubSubAdapter.createTopicAdminClient()) {
				TopicName topicName = TopicName.of(projectId, topicId);
				topicAdminClient.deleteTopic(topicName);

				log.debug("Topic deleted ok (topicId={})", topicId);
				return Completable.complete();
			} catch (Exception e) {
				log.error("Error deleting topic '{}' from project '{}': {}", topicId, projectId, e.getMessage());
				return Completable.error(e);
			}
		});
	}

}
