package com.kyrex.pubsub.adapters;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
import com.google.cloud.pubsub.v1.SubscriptionAdminSettings;
import com.google.cloud.pubsub.v1.TopicAdminClient;
import com.google.cloud.pubsub.v1.TopicAdminSettings;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;


@Slf4j
public class PubSubAdapter {

	private final TransportChannelProvider transportChannelProvider;
	private final CredentialsProvider credentialsProvider;

	public PubSubAdapter(String host) {
		log.trace("Creating a new PubSub adapter with host={}", host);

		ManagedChannel managedChannel = ManagedChannelBuilder.forTarget(host).usePlaintext().build();
		transportChannelProvider = FixedTransportChannelProvider.create(GrpcTransportChannel.create(managedChannel));
		credentialsProvider = NoCredentialsProvider.create();
	}

	public TopicAdminClient createTopicAdminClient() throws IOException {
		return TopicAdminClient.create(TopicAdminSettings.newBuilder()
			.setTransportChannelProvider(transportChannelProvider)
			.setCredentialsProvider(credentialsProvider)
			.build());
	}

	public SubscriptionAdminClient createSubscriptionAdminClient() throws IOException {
		return SubscriptionAdminClient.create(SubscriptionAdminSettings.newBuilder()
			.setTransportChannelProvider(transportChannelProvider)
			.setCredentialsProvider(credentialsProvider)
			.build());
	}

}
