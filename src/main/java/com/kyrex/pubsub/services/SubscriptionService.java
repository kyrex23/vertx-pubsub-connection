package com.kyrex.pubsub.services;

import com.google.pubsub.v1.*;
import com.kyrex.pubsub.adapters.PubSubAdapter;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.StreamSupport;


@Slf4j
@AllArgsConstructor
public class SubscriptionService {

	private final PubSubAdapter pubSubAdapter;
	private final String projectId;

	public Single<Subscription> create(String topicId, String subscriptionId) {
		TopicName topicName = TopicName.of(projectId, topicId);
		SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);
		log.debug("Creating subscription '{}' for the topic '{}'", subscriptionName, topicName);

		return Single.defer(() -> {
			try (var subscriptionAdminClient = pubSubAdapter.createSubscriptionAdminClient()) {
				Subscription subscription = subscriptionAdminClient
					.createSubscription(subscriptionName, topicName, PushConfig.getDefaultInstance(), 30);
				return Single.just(subscription);
			}
		});
	}

	public Single<List<String>> get(String topicId) {
		TopicName topicName = TopicName.of(projectId, topicId);
		log.debug("Getting subscriptions from topic '{}'", topicName);

		return Single.defer(() -> {
			try (var topicAdminClient = pubSubAdapter.createTopicAdminClient()) {
				List<String> subscriptions = StreamSupport
					.stream(topicAdminClient.listTopicSubscriptions(topicName).iterateAll().spliterator(), false)
					.toList();
				return Single.just(subscriptions);
			}
		});
	}

	public Single<List<Subscription>> getAll() {
		ProjectName projectName = ProjectName.of(projectId);
		log.debug("Getting subscriptions from project '{}'", projectName);

		return Single.defer(() -> {
			try (var subscriptionAdminClient = pubSubAdapter.createSubscriptionAdminClient()) {
				List<Subscription> subscriptions = StreamSupport
					.stream(subscriptionAdminClient.listSubscriptions(projectName).iterateAll().spliterator(), false)
					.toList();
				return Single.just(subscriptions);
			}
		});
	}

	public Completable delete(String subscriptionId) {
		SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);
		log.debug("Deleting subscription '{}'", subscriptionName);

		return Completable.defer(() -> {
			try (var subscriptionAdminClient = pubSubAdapter.createSubscriptionAdminClient()) {
				subscriptionAdminClient.deleteSubscription(subscriptionName);
				return Completable.complete();
			}
		});
	}

}
