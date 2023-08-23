@echo off

:: Path to the JAR file
set "JAR_PATH=..\async-publisher\build\libs\async-publisher.jar"

set "host=starter1.a01.euc1.aws.hivemq.cloud"
set "port=8883"

set "messageNumber=%1"
if "%messageNumber%"=="" set "messageNumber=999"
set "statistics=stats-%messageNumber%.csv"

echo qos,messageNumber,topicNumber,topicPrefix,totalTimeMillis,avgTimeMillis > %statistics%

set "runCount=0"
for %%q in (0 1 2) do (
    set "qos=%%q"
    set "topicNumber=%messageNumber%"
    :loop
    if %topicNumber% lss 1 goto :endloop
    echo Running #%runCount% with qos=!qos!, topicNumber=!topicNumber!, messageNumber=%messageNumber%
    for /f "delims=" %%r in ('java -jar "%JAR_PATH%" --host %host% --port %port% --secure ^
        --user Starter1 --password Starter1 ^
        --topicNumber !topicNumber! --messageNumber %messageNumber% --qos !qos%'
    ) do (
        echo %%r >> %statistics%
    )
    echo ------------------------------------------------------

    :: Calculate the next step size using the square root of the current topicNumber
    set /a "step_size=topicNumber / 2"

    :: Update topicNumber for the next iteration
    set /a "topicNumber-=step_size"

    :: Increment the runCount variable
    set /a "runCount+=1"
    goto :loop
    :endloop
)
