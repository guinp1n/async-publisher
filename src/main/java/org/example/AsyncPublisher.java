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
    private long startTime;
    private long endTime;
    @CommandLine.Option(names = {"--host"}, description = "MQTT broker host (Default: localhost)", required = false)
    String host = "localhost";
    @CommandLine.Option(names = {"--port"}, description = "MQTT broker port (Default: 1883)", required = false)
    int port = 1883;

    @CommandLine.Option(names = {"--secure"}, description = "Use TLS (Default: false)", required = false)
    boolean secure =false;

    @CommandLine.Option(names = {"--user"}, description = "Username", defaultValue = "", required = false)
    String user = "";

    @CommandLine.Option(names = {"--password"}, description = "Passphrase", arity = "0..1", interactive = true, defaultValue = "", required = false)
    String password = "";

    @CommandLine.Option(names = {"--topicPrefix"}, description = "Topic prefix (Default: 'test/')", required = false)
    String topicPrefix="test/";

    @CommandLine.Option(names = {"--topicNumber"}, description = "How many different topics (Default: 1000)", required = false)
    int topicNumber=1000;

    @CommandLine.Option(names = {"--messageNumber"}, description = "How many messages (Default: 1000)", required = false)
    int messageNumber=1000;

    @CommandLine.Option(names = {"--qos"}, description = "QoS (Default: 1)", required = false)
    int qos=1;

    @CommandLine.Option(names = {"--verbose"}, description = "Verbose output", required = false)
    boolean verbose = false;

    @Override
    public Integer call() throws Exception {
        if (verbose) {
            System.out.println("host: " + host);
            System.out.println("port: " + port);
            System.out.println("secure: " + secure);
            System.out.println("user: " + user);
            System.out.println("password: " + password);
            System.out.println("topicPrefix: " + topicPrefix);
            System.out.println("topicNumber: " + topicNumber);
            System.out.println("messageNumber: " + messageNumber);
            System.out.println("qos: " + qos);
        }

        if (messageNumber < topicNumber){
            topicNumber=messageNumber;
        }

        final Mqtt5ClientBuilder clientBuilder = Mqtt5Client.builder()
                .identifier("IAmJavaClient")
                .serverHost(host)
                .serverPort(port);

        if (secure) {
            clientBuilder.sslWithDefaultConfig();
        }

        final Mqtt5AsyncClient client = clientBuilder.buildAsync();

        // Record the start time
        startTime = System.nanoTime();

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

        System.out.println(String.format("%s,%d,%d,%s,%.2f,%.2f",
                qos, messageNumber, topicNumber, topicPrefix, totalTimeMillis, averageTimePerMessage));

        return 0;
    }

    private CompletableFuture<Void> publishMessages(Mqtt5AsyncClient client) {
        CompletableFuture<Void> publishFuture = CompletableFuture.completedFuture(null);
        int messagesPerTopic = (int) Math.ceil((double) messageNumber / topicNumber);
        int messageCounter = 0; // Initialize the message counter

        for (int topicIndex = 0; topicIndex < topicNumber ; topicIndex++) {
            for (int messageIndex = 0; messageIndex < messagesPerTopic; messageIndex++) {
                final int currentTopicIndex = topicIndex;
                final int currentMessageIndex = messageIndex;
                final String currentTopic = topicPrefix + currentTopicIndex;
                final int sequentialMessageNumber = messageCounter++; // Increment the counter

                publishFuture = publishFuture.thenCompose(v -> client.publishWith()
                                .topic(currentTopic)
                                .payload(("Message #" + (1+currentMessageIndex)
                                        + " to topic " + currentTopic
                                        + " QoS " + qos).getBytes())
                                .qos(MqttQos.fromCode(qos))
                                .send())
                        .thenAccept(publishResult -> {
                            if (verbose) {
                                System.out.println("Published " + sequentialMessageNumber + " / " + messageNumber);
                            }
                        });
            }
        }

        return publishFuture;
    }
}






