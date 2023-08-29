#!/usr/bin/env bash

# Usage: bash script_name.sh [messageNumber] [messageSizeBytes]
# the topicNumber will vary from teh messageNumber DOWN to 1 with step varying as a square root from the current messageNumber

# Path to the JAR file
#/Users/dsamkova/Projects/async-publisher/test/async-publisher.jar
JAR_PATH="../build/libs/async-publisher.jar"

host='destarter-2gombb.a01.euc1.aws.hivemq.cloud'
port=8883

messageNumber=${1:-3600}
messageSizeBytes=${2:-200}
statistics="stats-${messageNumber}-${messageSizeBytes}.csv"

# Create the CSV file and write headers
#qos, messageNumber, topicNumber, topicPrefix, totalTimeMillis, averageTimePerMessage
echo "qos,messageNumber,messageSizeBytes,topicNumber,topicPrefix,totalTimeMillis,averageTimePerMessage" > $statistics

runCount=0
# Iterate over different qos levels
for qos in {0..2}; do
    # Set the initial topicNumber
    topicNumber=$messageNumber
    # Iterate over different topicNumber values from messageNumber to 1
    while (( topicNumber >= 1 )); do
        echo "Running #${runCount} with qos=${qos}, topicNumber=${topicNumber}, messageNumber=${messageNumber}"
        result=$(java -jar $JAR_PATH --host $host --port $port --secure \
          --user Starter1 --password Starter1 \
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
