package com.kyrex.pubsub.services;

import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminClient;
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
		log.trace("Creating a new subscription in topic '{}' -> subscriptionId={}", topicId, subscriptionId);

		return Single.defer(() -> {
			try(var subscriptionAdminClient = pubSubAdapter.createSubscriptionAdminClient()) {
				TopicName topicName = TopicName.of(projectId, topicId);
				SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);
				Subscription subscription = subscriptionAdminClient
					.createSubscription(subscriptionName, topicName, PushConfig.getDefaultInstance(), 10);
				return Single.just(subscription);
			}
		});
	}

	public Single<List<String>> get(String topicId) {
		log.trace("Getting subscriptions from project '{}' and topic '{}'", projectId, topicId);

		return Single.defer(() -> {
			try (TopicAdminClient topicAdminClient = pubSubAdapter.createTopicAdminClient()) {
				TopicName topic = TopicName.of(projectId, topicId);
				List<String> subscriptions = StreamSupport
					.stream(topicAdminClient.listTopicSubscriptions(topic).iterateAll().spliterator(), false)
					.toList();
				return Single.just(subscriptions);
			}
		});
	}

	public Single<List<Subscription>> getAll() {
		log.trace("Getting subscriptions from project '{}'", projectId);

		return Single.defer(() -> {
			try (SubscriptionAdminClient subscriptionAdminClient = pubSubAdapter.createSubscriptionAdminClient()) {
				ProjectName projectName = ProjectName.of(projectId);
				List<Subscription> subscriptions = StreamSupport
					.stream(subscriptionAdminClient.listSubscriptions(projectName).iterateAll().spliterator(), false)
					.toList();
				return Single.just(subscriptions);
			}
		});
	}

	public Completable delete(String subscriptionId) {
		log.trace("Deleting subscription from project '{}' -> subscriptionId={}", projectId, subscriptionId);

		return Completable.defer(() -> {
			try(SubscriptionAdminClient subscriptionAdminClient = pubSubAdapter.createSubscriptionAdminClient()) {
				SubscriptionName subscription = SubscriptionName.of(projectId, subscriptionId);
				subscriptionAdminClient.deleteSubscription(subscription);
				return Completable.complete();
			}
		});
	}

}
