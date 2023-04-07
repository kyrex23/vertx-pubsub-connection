package com.kyrex;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import com.google.pubsub.v1.ProjectName;
import com.google.pubsub.v1.Topic;
import com.google.pubsub.v1.TopicName;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.StreamSupport;

@Slf4j
public class PubSubService {

	private final String projectId;
	private final TransportChannelProvider transportChannelProvider;
	private final CredentialsProvider credentialsProvider;

	public PubSubService(String host, String projectId) {
		log.debug("Creating PubSubService with: {host={}, projectId={}}", host, projectId);

		ManagedChannel managedChannel = ManagedChannelBuilder.forTarget(host).usePlaintext().build();
		this.transportChannelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(managedChannel));
		this.credentialsProvider = NoCredentialsProvider.create();
		this.projectId = projectId;
	}

	public Single<List<Topic>> getTopics() {
		log.debug("Getting topics from project '{}'...", projectId);

		return Single.defer(() -> {
			try (TopicAdminClient topicAdminClient = TopicAdminClient.create(TopicAdminSettings.newBuilder()
				.setTransportChannelProvider(transportChannelProvider)
				.setCredentialsProvider(credentialsProvider)
				.build())
			) {
				List<Topic> topics = StreamSupport
					.stream(topicAdminClient.listTopics(ProjectName.of(projectId)).iterateAll().spliterator(), false)
					.toList();

				log.debug("Topics obtained from project '{}': {}", projectId, topics.size());
				return Single.just(topics);
			} catch (Exception e) {
				log.error("Error getting topics for project {}: {}", projectId, e.getMessage());
				return Single.error(e);
			}
		});
	}

	public Completable createTopic(String topicId) {
		log.debug("Creating topic in project '{}' -> topicId={}", projectId, topicId);

		return Completable.defer(() -> {
			try (TopicAdminClient topicAdminClient = TopicAdminClient.create(TopicAdminSettings.newBuilder()
				.setTransportChannelProvider(transportChannelProvider)
				.setCredentialsProvider(credentialsProvider)
				.build())
			) {
				TopicName topicName = TopicName.of(projectId, topicId);
				Topic topic = topicAdminClient.createTopic(topicName);

				log.info("Topic created successfully: {}", topic.getName());
				return Completable.complete();
			} catch (Exception e) {
				log.error("Error creating topic {}/{}: {}", projectId, topicId, e.getMessage());
				return Completable.error(e);
			}
		});
	}

}
