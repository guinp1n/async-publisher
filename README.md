
# Async MQTT Message Publisher

This Java program, named **AsyncPublisher**, is designed to publish MQTT (Message Queuing Telemetry Transport) messages to a broker with asynchronous processing. It utilizes the HiveMQ MQTT client library to achieve this functionality.

## Purpose

The **AsyncPublisher** program is meant to provide a flexible way to publish a large number of MQTT messages to a broker for testing or demonstration purposes. It is especially useful for scenarios where you want to test the performance and behavior of MQTT brokers under heavy publishing loads.

## Command Line Parameters

The program can be executed from the command line, and it accepts the following parameters:

-   `--host`: The MQTT broker host. Default: localhost.
-   `--port`: The MQTT broker port. Default: 1883.
-   `--secure`: Use TLS for secure communication. Default: false.
-   `-u`, `--user`: Username for authentication. Default: None.
-   `-pw`, `--password`: Password or passphrase for authentication. Default: None.
-   `--topicPrefix`: Prefix for MQTT topics. Default: 'test/'.
-   `--topicNumber`: Number of different topics to publish to. Default: 1000.
-   `--messageNumber`: Total number of messages to publish. Default: 1000.
-   `--qos`: Quality of Service level for published messages. Default: 1.
-   `--silent`: Suppress excessive output. Default: false.

## How It Works

1.  The program establishes an MQTT client connection to the specified broker.
2.  It sets up the connection parameters based on the provided command line arguments.
3.  The program then generates and publishes messages to the broker asynchronously.
4.  Messages are published to different topics with sequential numbers, and the message payload includes information about the message index, topic, and QoS level.
5.  After all messages are published, the program calculates and prints the total time taken for the operation and the average time per message.

## Building from the source

`./gradlew clean shadowJar`

## Running from the jar

For example, connecting to HiveMQ Cloud and publishing 1000 messages distributed between 10 different topics like 'test/0', 'test/2',.. 'test/9' using QoS 0

```
java -jar ./build/libs/async-publisher.jar --host starter-1.a01.euc1.aws.hivemq.cloud --port 8883 --secure --user MyUser --password MyPassword --qos 0 --messageNumber 1000 --topicNumber 10
```

For example, connecting to localhost:

```
java -jar ./build/libs/async-publisher.jar --host localhost --port 1883 --qos 0 --messageNumber 1000 --topicNumber 10
```

For example, getting help with the program arguments:

```
java -jar ./build/libs/async-publisher.jar --help
```

Make sure to replace and adjust the command line parameters as needed.

Please note that this program is provided under the Apache License 2.0, so ensure compliance with the license terms if you choose to use or distribute it.

For more information about the HiveMQ MQTT client library, refer to the [HiveMQ MQTT Client Library documentation](https://hivemq.github.io/hivemq-mqtt-client/).

## License

This program is licensed under the Apache License, Version 2.0. You can find a copy of the license [here](http://www.apache.org/licenses/LICENSE-2.0).
