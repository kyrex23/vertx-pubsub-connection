<h1 align="center">:fire: Google Pub/Sub Emulation :fire:</h1>

Demo API created using Vert.x to interact with a locally-emulated Google Pub/Sub service
(totally free :money_mouth_face:)


## Getting started

To get started, you will need to deploy the locally-emulated Google Pub/Sub service.

You can do this using this [docker-compose](./docker/docker-compose.yml) by running the following command from the
project root:

```
docker-compose -f docker/docker-compose.yml up -d
```

This will start the service and create any necessary containers.

Once the service is deployed, you can compile and run the API from the CLI by running the following commands:

```
mvn compile
mvn exec:java -Dexec.mainClass="com.kyrex.Main"
```

(_Alternatively, you can use an IDE to import and run the Maven project_)

After that, your server will be listening on `localhost:8080`


## API Endpoints

<details>
	<summary>Topic Management</summary>
	<ul>
		<li><code>GET /topics</code> -- Retrieves the current topics</li>
		<li><code>POST /topics/{topicId}</code> -- Creates a new topic with the given `topicId` name</li>
		<li><code>DELETE /topics/{topicId}</code> -- Deletes the topic with the given `topicId` if exists</li>
	</ul>
</details>

<details>
	<summary>Subscription Management</summary>
	<ul>
		<li><code>GET /subscriptions</code> -- Retrieves all the subscriptions for the current project</li>
		<li><code>GET /topics/{topicId}/subscriptions</code> -- Retrieves the subscriptions for the given topic</li>
		<li><code>POST /topics/{topicId}/subscriptions/{subscriptionId}</code> -- Creates a subscription for the given topic</li>
		<li><code>DELETE /subscriptions/{subscriptionId}</code> -- Deletes the subscription with the given id</li>
	</ul>
</details>
