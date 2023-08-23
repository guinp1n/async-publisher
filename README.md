
# Async MQTT Message Publisher


**AsyncPublisher** is a Java program designed for publishing MQTT (Message Queuing Telemetry Transport) messages to a broker asynchronously. It utilizes the HiveMQ MQTT client library to achieve this functionality.

## Purpose

The primary purpose of this program is to enable efficient and flexible publishing of a specified number of MQTT messages to a broker. It's particularly useful for scenarios where you want to test the performance and behavior of MQTT brokers under various conditions.

## Command Line Parameters

The program can be executed from the command line and accepts the following parameters:

-   `--host`: MQTT broker host address. Default: localhost.
-   `--port`: MQTT broker port number. Default: 1883.
-   `--secure`: Use TLS for secure communication. Default: false.
-   `--user`: Username for authentication. Default: None.
-   `--password`: Password or passphrase for authentication. Default: None.
-   `--topicPrefix`: Prefix for MQTT topics. Default: 'test/'.
-   `--topicNumber`: Number of different topics to publish to. Default: 10.
-   `--messageNumber`: Total number of messages to publish. Default: 10.
-   `--qos`: Quality of Service level for published messages. Default: 1.
-   `--verbose`: Enable verbose output. Default: false.
-   `--clientId`: Client id. Default: 'java_0123456789'.

## How It Works

1.  The program establishes an MQTT client connection to the specified broker.
2.  It sets up the connection parameters based on the provided command line arguments.
3.  The program generates and publishes messages to the broker asynchronously.
4.  Messages are published to different topics with sequential numbers, and the message payload includes information about the message index, topic, and QoS level.
5.  After all messages are published, the program calculates and prints the total time taken for the operation and the average time per message.
6.  The program can output the collected statistics as CSV-formatted data, including QoS level, message number, topic number, topic prefix, total time, and average time per message.

## Building From The Source

`./gradlew clean shadowJar`

## Running From The JAR

For example, connecting to HiveMQ Cloud and publishing 1000 messages distributed between 10 different topics like 'test/0', 'test/2',.. 'test/9' using QoS 0:

```
java -jar ./build/libs/async-publisher.jar --host starter-1.a01.euc1.aws.hivemq.cloud --port 8883 --secure \
    --user MyUser --password MyPassword \
    --qos 0 --messageNumber 1000 --topicNumber 10 \
    --verbose
```

For example, connecting to localhost:

```
java -jar ./build/libs/async-publisher.jar --host localhost --port 1883 \
    --qos 0 --messageNumber 10 --topicNumber 10 \
    --verbose
```
Make sure to replace and adjust the command line parameters as needed.

For example, getting help with the program arguments:

```
java -jar ./build/libs/async-publisher.jar --help
```




Please note that this program is provided under the Apache License 2.0, so ensure compliance with the license terms if you choose to use or distribute it.

For more information about the HiveMQ MQTT client library, refer to the [HiveMQ MQTT Client Library documentation](https://hivemq.github.io/hivemq-mqtt-client/).

## License

This program is licensed under the Apache License, Version 2.0. You can find a copy of the license [here](http://www.apache.org/licenses/LICENSE-2.0).
