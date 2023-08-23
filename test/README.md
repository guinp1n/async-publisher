## Asynchronous Publisher Tester

This Bash script facilitates the testing of an asynchronous message publisher using the provided JAR file. It allows you to vary Quality of Service (QoS) levels and message parameters to assess performance. The script generates statistics in CSV format, which can be useful for analyzing and comparing different test runs.

### Program Functionality

The script iterates through various QoS levels and message parameters to execute the asynchronous message publisher multiple times. It generates statistics for each run and compiles them into a CSV file. The main steps include:

1.  Setting the QoS levels (0, 1, and 2) for message publishing.
2.  Varying the number of topics and messages based on the specified input (default: 999 messages).
3.  Executing the asynchronous message publisher JAR with the provided parameters.
4.  Collecting and recording the results of each run in a statistics CSV file.
5.  Adjusting the number of topics for subsequent iterations using a step size calculated from the square root of the current topic number.

### Command Line Arguments

The script accepts the following optional command line argument:

-   `messageNumber`: The total number of messages to be published across all runs (default: 999). This value affects the range and density of topics.

### Usage

To run the script, open a terminal and navigate to the script's directory. Then execute the following command:

```
./test.sh [messageNumber]
```

Replace `test.sh` with the actual name of the script file.

If you want to customize the number of messages for the test runs, provide the desired `messageNumber` value as an argument.

### Requirements

-   Bash (Bourne Again Shell)
-   Java Runtime Environment (JRE) to execute the JAR file

Ensure that the JAR file path (`JAR_PATH`) is correctly specified in the script. Also, make sure the JAR file contains the necessary functionalities for asynchronous message publishing.

### Output

The script generates a CSV file named `stats-${messageNumber}.csv` containing the following columns:

-   `qos`: Quality of Service level used for the run.
-   `messageNumber`: Total number of messages for the run.
-   `topicNumber`: Number of topics for the run.
-   `topicPrefix`: Prefix for topic names (not explicitly defined in the script).
-   `totalTimeMillis`: Total time taken for the run in milliseconds.
-   `avgTimeMillis`: Average time taken per message in milliseconds.

Each run's statistics are appended to the CSV file.

### Note

This README provides an overview of the script's functionality and usage. Make sure to review and modify paths, parameters, and other configurations as needed to match your environment and requirements.