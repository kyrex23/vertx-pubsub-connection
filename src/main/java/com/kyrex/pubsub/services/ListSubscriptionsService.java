package com.kyrex.pubsub.services;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Subscription;
import com.google.pubsub.v1.TopicName;
import com.kyrex.pubsub.adapters.PubSubAdapter;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.StreamSupport;


@Slf4j
@AllArgsConstructor
public class ListSubscriptionsService {

	private final PubSubAdapter pubSubAdapter;
	private final String projectId;

	public Single<List<Subscription>> getAllSubscriptions() {
		log.trace("Getting subscriptions from project '{}'", projectId);

		return Single.defer(() -> {
			try (SubscriptionAdminClient subscriptionAdminClient = pubSubAdapter.createSubscriptionAdminClient()) {
				ProjectName projectName = ProjectName.of(projectId);

				List<Subscription> subscriptions = StreamSupport
					.stream(subscriptionAdminClient.listSubscriptions(projectName).iterateAll().spliterator(), false)
					.toList();

				return Single.just(subscriptions);
			} catch (Exception e) {
				log.error("Error getting subscriptions from project '{}': {}", projectId, e.getMessage());
				return Single.error(e);
			}
		});
	}

	public Single<List<String>> getSubscriptions(String topicId) {
		log.trace("Getting subscriptions from project '{}' and topic '{}'", projectId, topicId);

		return Single.defer(() -> {
			try (TopicAdminClient topicAdminClient = pubSubAdapter.createTopicAdminClient()) {
				TopicName topic = TopicName.of(projectId, topicId);

				List<String> subscriptions = StreamSupport
					.stream(topicAdminClient.listTopicSubscriptions(topic).iterateAll().spliterator(), false)
					.toList();

				return Single.just(subscriptions);
			} catch (Exception e) {
				log.error("Error getting subscriptions from project '{}' and topic '{}': {}", projectId, topicId,
					e.getMessage());
				return Single.error(e);
			}
		});
	}

}
