#!/usr/bin/env bash

# Path to the JAR file
JAR_PATH="../async-publisher/build/libs/async-publisher.jar"

host='starter1.a01.euc1.aws.hivemq.cloud'
port=8883

messageNumber=${1:-999}
statistics="stats-${messageNumber}.csv"

# Create the CSV file and write headers
echo "qos,messageNumber,topicNumber,topicPrefix,totalTimeMillis,avgTimeMillis" > $statistics

runCount=0
# Iterate over different qos levels
for qos in {0..2}; do
    # Set the initial topicNumber
    topicNumber=$messageNumber
    # Iterate over different topicNumber values from messageNumber to 1
    while (( topicNumber >= 1 )); do
        echo "Running #${runCount} with qos=${qos}, topicNumber=${topicNumber}, messageNumber=${messageNumber}"
        result=$(java -jar $JAR_PATH --host $host --port $port --secure \
          --user MyUser --password MyPassword \
          --topicNumber ${topicNumber} --messageNumber ${messageNumber} --qos $qos)

        echo "$result" >> $statistics
        echo "------------------------------------------------------"

        # Calculate the next step size using the square root of the current topicNumber
        step_size=$(echo "scale=0; sqrt($topicNumber)" | bc -l)

        # Update topicNumber for the next iteration
        topicNumber=$((topicNumber - step_size))

        # Increment the runCount variable
        ((runCount++))
    done
done
