package com.kyrex.pubsub.services;

import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import com.google.pubsub.v1.TopicName;
import com.kyrex.pubsub.adapters.PubSubAdapter;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@Slf4j
@AllArgsConstructor
public class MessageService {

	private final PubSubAdapter pubSubAdapter;
	private final String projectId;

	public Single<PubsubMessage> publish(String topicId, String message) {
		TopicName topicName = TopicName.of(projectId, topicId);

		return Single.defer(() -> {
			try (var topicAdminClient = pubSubAdapter.createTopicAdminClient()) {
				PubsubMessage pubsubMessage = PubsubMessage.newBuilder()
					.setData(ByteString.copyFromUtf8(message))
					.build();

				topicAdminClient.publish(topicName, List.of(pubsubMessage));
				return Single.just(pubsubMessage);
			}
		});
	}

	public Single<PubsubMessage> retrieve(String subscriptionId) {
		SubscriptionName subscriptionName = SubscriptionName.of(projectId, subscriptionId);

		return Single.defer(() -> {
			try (var subscriptionAdminClient = pubSubAdapter.createSubscriptionAdminClient()) {
				PubsubMessage pubsubMessage = subscriptionAdminClient.pull(subscriptionName, 1)
					.getReceivedMessages(0)
					.getMessage();
				return Single.just(pubsubMessage);
			}
		});
	}

}
