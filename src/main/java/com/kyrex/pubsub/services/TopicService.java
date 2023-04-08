package com.kyrex.pubsub.services;

import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import com.kyrex.pubsub.adapters.PubSubAdapter;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.StreamSupport;


@Slf4j
@AllArgsConstructor
public class TopicService {

	private final PubSubAdapter pubSubAdapter;
	private final String projectId;

	public Single<Topic> create(String topicId) {
		TopicName topicName = TopicName.of(projectId, topicId);
		log.debug("Creating topic '{}'", topicName);

		return Single.defer(() -> {
			try (var topicAdminClient = pubSubAdapter.createTopicAdminClient()) {
				Topic topic = topicAdminClient.createTopic(topicName);
				return Single.just(topic);
			}
		});
	}

	public Single<List<Topic>> getAll() {
		ProjectName projectName = ProjectName.of(projectId);
		log.debug("Getting topics from project '{}'", projectName);

		return Single.defer(() -> {
			try (var topicAdminClient = pubSubAdapter.createTopicAdminClient()) {
				List<Topic> topics = StreamSupport
					.stream(topicAdminClient.listTopics(projectName).iterateAll().spliterator(), false)
					.toList();
				return Single.just(topics);
			}
		});
	}

	public Completable delete(String topicId) {
		TopicName topicName = TopicName.of(projectId, topicId);
		log.debug("Deleting topic '{}'", topicName);

		return Completable.defer(() -> {
			try (var topicAdminClient = pubSubAdapter.createTopicAdminClient()) {
				topicAdminClient.deleteTopic(topicName);
				return Completable.complete();
			}
		});
	}

}
