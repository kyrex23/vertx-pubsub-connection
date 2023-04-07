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

* `GET /topics` -- Retrieves the current topics
* `POST /topics/{topicId}` -- Creates a new topic with the given `topicId` name
