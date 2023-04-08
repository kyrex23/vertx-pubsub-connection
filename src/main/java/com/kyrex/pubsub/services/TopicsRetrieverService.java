package com.kyrex.pubsub.services;

import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Topic;
import com.kyrex.pubsub.adapters.PubSubAdapter;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.StreamSupport;


@Slf4j
@AllArgsConstructor
public class TopicsRetrieverService {

	private final PubSubAdapter pubSubAdapter;
	private final String projectId;


	public Single<List<Topic>> getAll() {
		log.trace("Getting topics from project '{}'...", projectId);

		return Single.defer(() -> {
			try (TopicAdminClient topicAdminClient = pubSubAdapter.createTopicAdminClient()) {
				List<Topic> topics = StreamSupport
					.stream(topicAdminClient.listTopics(ProjectName.of(projectId)).iterateAll().spliterator(), false)
					.toList();

				log.debug("Topics obtained from project '{}': {}", projectId, topics.size());
				return Single.just(topics);
			} catch (Exception e) {
				log.error("Error getting topics for project '{}': {}", projectId, e.getMessage());
				return Single.error(e);
			}
		});
	}

}
