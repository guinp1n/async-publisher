/*
 * Copyright 2018-present HiveMQ and the HiveMQ Community
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import com.hivemq.client.mqtt.mqtt5.Mqtt5ClientBuilder;
import picocli.CommandLine;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

@CommandLine.Command(name = "publish", mixinStandardHelpOptions = true,
        description = "publishes N messages to M topics")
public class AsyncPublisher implements Callable<Integer> {
    private long startTime = System.nanoTime();;
    private long endTime;
    @CommandLine.Option(names = {"--host"}, description = "MQTT broker host", required = false, defaultValue = "localhost",showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    String host = "localhost";
    @CommandLine.Option(names = {"--port"}, description = "MQTT broker port", required = false, defaultValue = "1883",showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    int port = 1883;

    @CommandLine.Option(names = {"--secure"}, description = "Use TLS", required = false, defaultValue = "false",showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    boolean secure =false;

    @CommandLine.Option(names = {"--user"}, description = "Username", defaultValue = "", required = false,showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    String user = "";

    @CommandLine.Option(names = {"--password"}, description = "Passphrase", arity = "0..1", interactive = true, defaultValue = "", required = false,showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    String password = "";

    @CommandLine.Option(names = {"--topicPrefix"}, description = "Topic prefix", required = false, defaultValue = "test/",showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    String topicPrefix="test/";

    @CommandLine.Option(names = {"--topicNumber"}, description = "How many different topics", required = false, showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    int topicNumber=10;

    @CommandLine.Option(names = {"--messageNumber"}, description = "How many messages", required = false,showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    int messageNumber=10;

    @CommandLine.Option(names = {"--messageSizeBytes"}, description = "Size of payload of MQTT PUBLISH packet in bytes", required = false,showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    int messageSizeBytes=10;

    @CommandLine.Option(names = {"--qos"}, description = "QoS", required = false, showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    int qos=1;

    @CommandLine.Option(names = {"--verbose"}, description = "Verbose output", required = false,showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    boolean verbose = false;

    @CommandLine.Option(names = {"--clientId"}, description = "ClientId", required = false,showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
    String clientId="java_"+ startTime;

    @Override
    public Integer call() throws Exception {
        if (verbose) {
            System.out.println("clientId: " + clientId);
            System.out.println("host: " + host);
            System.out.println("messageNumber: " + messageNumber);
            System.out.println("messageSizeBytes: " + messageSizeBytes);
            System.out.println("password: " + password);
            System.out.println("port: " + port);
            System.out.println("qos: " + qos);
            System.out.println("secure: " + secure);
            System.out.println("topicNumber: " + topicNumber);
            System.out.println("topicPrefix: " + topicPrefix);
            System.out.println("user: " + user);
            System.out.println("verbose: " + verbose);
        }

        if (messageNumber < topicNumber){
            topicNumber=messageNumber;
        }

        final Mqtt5ClientBuilder clientBuilder = Mqtt5Client.builder()
                .identifier(clientId)
                .serverHost(host)
                .serverPort(port);

        if (secure) {
            clientBuilder.sslWithDefaultConfig();
        }

        final Mqtt5AsyncClient client = clientBuilder.buildAsync();

        CompletableFuture<Void> publishFuture = client.connectWith()
                .simpleAuth()
                .username(user)
                .password(password.getBytes())
                .applySimpleAuth()
                .send()
                .thenAccept(connAck -> {
                    if (verbose) {
                        System.out.println("connected " + connAck);
                    }
                })
                .thenCompose(connAck -> publishMessages(client))
                .thenAccept(publishResult -> {
                    if (verbose){
                        System.out.println("published " + (publishResult != null ? publishResult : ""));
                    }
                })
                .thenCompose(v -> client.disconnect())
                .thenAccept(v -> { if (verbose){System.out.println("disconnected");}});

        publishFuture.get(); // Wait for all tasks to complete

        // Record the end time
        endTime = System.nanoTime();

        long totalTimeNano = endTime - startTime;
        double totalTimeMillis = totalTimeNano / 1_000_000.0; // Convert to milliseconds
        if (verbose){
            System.out.println("Total time taken: " + totalTimeMillis + " ms");
        }

        double averageTimePerMessage = totalTimeMillis / messageNumber;
        if (verbose) {
            System.out.println("Average time per message: " + averageTimePerMessage + " ms");
        }

        System.out.println(String.format("%d,%d,%d,%d,%s,%.2f,%.2f",
                qos, messageNumber, messageSizeBytes, topicNumber, topicPrefix, totalTimeMillis, averageTimePerMessage));

        return 0;
    }

    private CompletableFuture<Void> publishMessages(Mqtt5AsyncClient client) {
        CompletableFuture<Void> publishFuture = CompletableFuture.completedFuture(null);
        int messagesPerTopic = (int) Math.ceil((double) messageNumber / topicNumber);
        int messageCount = 0; // Initialize the message counter

        while (messageCount < messageNumber) {
            int topicIndex = messageCount / messagesPerTopic;
            int messageIndex = messageCount % messagesPerTopic;

            final String topic = topicPrefix + topicIndex;
            final String payload = generatePayload(messageIndex, topic, qos, messageSizeBytes);

            publishFuture = publishFuture.thenCompose(v -> client.publishWith()
                            .topic(topic)
                            .payload(("Message #" + (1 + messageIndex)
                                    + " to topic " + topic
                                    + " QoS " + qos).getBytes())
                            .qos(MqttQos.fromCode(qos))
                            .send())
                    .thenAccept(publishResult -> {
                        if (verbose) {
                            System.out.println("Published " + (1+messageIndex) + " / " + messageNumber);
                        }
                    });

            messageCount++;
        }

        return publishFuture;
    }

    private String generatePayload(int messageIndex, String topic, int qos, int messageSize) {
        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append("Message #").append(1 + messageIndex)
                .append(" to topic ").append(topic)
                .append(" QoS ").append(qos);

        String payload = payloadBuilder.toString();
        while (payload.length() < messageSize) {
            payload += " "; // Padding with spaces to reach the desired size
        }

        return payload.substring(0, messageSize); // Truncate to desired size
    }
}






