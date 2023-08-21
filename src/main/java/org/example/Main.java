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
import org.apache.commons.cli.*;


import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5AsyncClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class Main {

    static int topicNumber = 1;
    static int messageNumber = 1000;
    static MqttQos qos = MqttQos.AT_LEAST_ONCE;
    public static void main(final String[] args) throws InterruptedException {
        Options options = new Options();
        options.addOption("host", true, "MQTT broker host");
        options.addOption("port", true, "MQTT broker port");
        options.addOption("user", true, "Username");
        options.addOption("password", true, "Password");
        options.addOption("topicNumber", true, "Number of topics");
        options.addOption("qos", true, "QoS level");
        options.addOption("messageNumber", true, "Number of messages per topic");

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            String host = cmd.getOptionValue("host", "localhost");
            int port = Integer.parseInt(cmd.getOptionValue("port", "1883"));
            String user = cmd.getOptionValue("user", "");
            String password = cmd.getOptionValue("password", "");
            topicNumber = Integer.parseInt(cmd.getOptionValue("topicNumber", "1000"));
            qos = MqttQos.valueOf(cmd.getOptionValue("qos", "EXACTLY_ONCE"));
            messageNumber = Integer.parseInt(cmd.getOptionValue("messageNumber", "3000"));

            final Mqtt5AsyncClient client = Mqtt5Client.builder()
                    .serverHost(host)
                    .serverPort(port)
                    .buildAsync();

            client.connectWith()
                    .simpleAuth()
                    .username(user)
                    .password(password.getBytes())
                    .applySimpleAuth()
                    .send()
                    .thenAccept(connAck -> System.out.println("connected " + connAck))
                    .thenCompose(connAck -> publishMessages(client))
                    .thenAccept(publishResult -> System.out.println("published " + publishResult))
                    .thenCompose(v -> client.disconnect())
                    .thenAccept(v -> System.out.println("disconnected"));

            System.out.println("see that everything above is async");
            for (int i = 0; i < 5; i++) {
                TimeUnit.MILLISECONDS.sleep(50);
                System.out.println("...");
            }

        } catch (ParseException e) {
            System.err.println("Command line parsing failed. Reason: " + e.getMessage());
        }
    }

    private static CompletableFuture<Void> publishMessages(Mqtt5AsyncClient client) {
        CompletableFuture<Void> publishFuture = CompletableFuture.completedFuture(null);

        for (int topicIndex = 0; topicIndex < topicNumber ; topicIndex++) {
            for (int messageIndex = 0; messageIndex < (int) Math.ceil((double) messageNumber / topicNumber); messageIndex++) {
                final int currentTopicIndex = topicIndex;
                final int currentMessageIndex = messageIndex;

                publishFuture = publishFuture.thenCompose(v -> client.publishWith()
                                .topic("test/" + currentTopicIndex)
                                .payload(("Message " + currentMessageIndex).getBytes())
                                .qos(qos)
                                .send())
                        .thenAccept(publishResult -> System.out.println("Published message " +
                                currentMessageIndex + " to topic" + currentTopicIndex));
            }
        }

        return publishFuture;
    }
}
