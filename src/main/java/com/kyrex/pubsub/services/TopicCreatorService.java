package com.kyrex.pubsub.services;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import com.kyrex.pubsub.adapters.PubSubAdapter;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
public class TopicCreatorService {

	private final PubSubAdapter pubSubAdapter;
	private final String projectId;

	public Single<Topic> create(String topicId) {
		log.trace("Creating topic in project '{}' -> topicId={}", projectId, topicId);

		return Single.defer(() -> {
			try (TopicAdminClient topicAdminClient = pubSubAdapter.createTopicAdminClient()) {
				TopicName topicName = TopicName.of(projectId, topicId);
				Topic topic = topicAdminClient.createTopic(topicName);

				log.debug("Topic created ok: {}", topic.getName());
				return Single.just(topic);
			} catch (Exception e) {
				log.error("Error creating topic {projectId={}, topicId={}}: {}", projectId, topicId, e.getMessage());
				return Single.error(e);
			}
		});
	}

}
