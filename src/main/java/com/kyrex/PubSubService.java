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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.StreamSupport;

public class PubSubService {

	private static final Logger LOG = LoggerFactory.getLogger(PubSubService.class);

	private final TransportChannelProvider transportChannelProvider;
	private final CredentialsProvider credentialsProvider;

	public PubSubService(String host) {
		LOG.debug("Creating PubSubService with host={}", host);

		ManagedChannel managedChannel = ManagedChannelBuilder.forTarget(host).usePlaintext().build();
		this.transportChannelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(managedChannel));
		this.credentialsProvider = NoCredentialsProvider.create();
	}

	public Single<List<Topic>> getTopics(String projectId) {
		LOG.debug("Getting topics with: {projectId={}}", projectId);

		return Single.defer(() -> {
			try (TopicAdminClient topicAdminClient = TopicAdminClient.create(TopicAdminSettings.newBuilder()
				.setTransportChannelProvider(transportChannelProvider)
				.setCredentialsProvider(credentialsProvider)
				.build())
			) {
				List<Topic> topics = StreamSupport
					.stream(topicAdminClient.listTopics(ProjectName.of(projectId)).iterateAll().spliterator(), false)
					.toList();

				LOG.info("Topics obtained for project {}: {}", projectId, topics.size());
				return Single.just(topics);
			} catch (Exception e) {
				LOG.error("Error getting topics for project {}: {}", projectId, e.getMessage());
				return Single.error(e);
			}
		});
	}

	public Completable createTopic(String projectId, String topicId) {
		LOG.debug("Creating topic with: {projectId={}, topicId={}}...", projectId, topicId);

		return Completable.defer(() -> {
			try (TopicAdminClient topicAdminClient = TopicAdminClient.create(TopicAdminSettings.newBuilder()
				.setTransportChannelProvider(transportChannelProvider)
				.setCredentialsProvider(credentialsProvider)
				.build())
			) {
				TopicName topicName = TopicName.of(projectId, topicId);
				Topic topic = topicAdminClient.createTopic(topicName);

				LOG.info("Topic created successfully: {}", topic.getName());
				return Completable.complete();
			} catch (Exception e) {
				LOG.error("Error creating topic {}/{}: {}", projectId, topicId, e.getMessage());
				return Completable.error(e);
			}
		});
	}

}
