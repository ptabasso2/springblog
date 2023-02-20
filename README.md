![GitHub](https://img.shields.io/github/license/ptabasso2/springblog?style=plastic)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/ptabasso2/springblog)
![GitHub last commit](https://img.shields.io/github/last-commit/ptabasso2/springblog)

# Rest application example


## Introduction


The sections of this tutorial are structured as follows

* Goal
* Pre-requisites
* Clone the repository
* Directory structure of the project
* Overview of the application
* Building the docker images and run the application through docker (Optional).
* Building the application and running it locally.
* Testing the application and generating load
* Building the application and running it on a kubernetes cluster
* Conclusion

In each section, we'll describe the required steps to take in order to reach the goal.


## Goal of this lab


The purpose of this lab is to help familiarizing and practising the various steps required to set up a simple spring application using Rest.

<p align="left">
  <img src="img/imgRest.png" width="850" />
</p>


## Pre-requisites


+ About 90 minutes
+ A java JDK (If building & running locally). Ex OpenJDK 11 or above
+ Gradle installed (If building & running locally). Ex Gradle 7.5.1
+ Git client
+ A Datadog account with a valid API key
+ Your favorite text editor or IDE (Ex Sublime Text, Atom, vscode...)
+ Docker and docker-compose.


## Clone the repository


<pre style="font-size: 12px">
[root@pt-instance-6:~/]$ git clone https://github.com/ptabasso2/springblog
[root@pt-instance-6:~/]$ cd springblog
[root@pt-instance-6:~/springblog]$ 
</pre>


## Directory structure of the project


The example below is the structure after having clone the project.

```shell
[root@pt-instance-6:~/springblog]$ tree
.
├── Dockerfiles
│   ├── Dockerfile.springback
│   ├── Dockerfile.springfront
├── README.md
├── dd-java-agent.jar
├── docker-compose.yml
├── img
├── springback
│   ├── build.gradle
│   ├── gradle
│   │   └── wrapper
│   │       ├── gradle-wrapper.jar
│   │       └── gradle-wrapper.properties
│   ├── gradlew
│   ├── logs
│   │   └── springback.log
│   ├── settings.gradle
│   └── src
│       └── main
│           ├── java
│           │   └── com
│           │       └── datadog
│           │           └── pej
│           │               └── back
│           │                   ├── BasicController.java
│           │                   ├── Quote.java
│           │                   ├── SpringBackApplication.java
│           │                   ├── Value.java
│           └── resources
│               └── application.yml
└── springfront
    ├── build.gradle
    ├── gradle
    │   └── wrapper
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    ├── gradlew
    ├── settings.gradle
    └── src
        └── main
            ├── java
            │   └── com
            │       └── datadog
            │           └── pej
            │               └── front
            │                   ├── BasicController.java
            │                   ├── Quote.java
            │                   ├── SpringFrontApplication.java
            │                   └── Value.java
            └── resources
                └── application.yml

```

## Overview of the application


The main components of this project can be described as follows:
+ Two distinct microservices (`springfront` and `springback`) communicating with each other through Rest. The backend service in turn does a bit of processing and issues two external http calls  </br>
+ The various docker files needed to build the images and the `docker-compose` configuration file to spin up the three containers (`dd-agent-dogfood-jmx`, `springfront`, `springback`).


## Building the docker images and run the application through docker (Optional).


This step is not mandatory. If you wish to have these services running locally you may skip this section and jump to the next [one](#local).


### Building the images ###

For the sake of effectiveness, you will find the required images preloaded into the following registry https://hub.docker.com/repositories/pejese </br>
But if you ever need to change/adapt the Dockerfiles and rebuild and push the images yourself, you may consider the following steps:

* First change the `image` key in the `docker-compose.yml` file to specify your repository/registry details. The example uses `pejese/springfront:v2` and `pejese/springback:v2` 


* Do make sure to set your API Key before running the following command:

````shell
[root@pt-instance-6:~/springblog]$ export DD_API_KEY=<Your api key>
````

* Then run the following:

````shell
[root@pt-instance-6:~/springblog]$ docker-compose up -d
Creating network "app" with driver "bridge"
Building springfront
Sending build context to Docker daemon    149MB
Step 1/9 : FROM adoptopenjdk/openjdk11:ubuntu-nightly-slim
 ---> 86b175442692
Step 2/9 : ENV PS1A="[\[\e[1m\]\[\e[38;5;46m\]\u\[\e[0m\]@\h:\[\e[1m\]\[\e[38;5;21m\]\w\[\e[0m\]]$ "
 ---> Using cache
 ---> 369ef1b8c1ec
Step 3/9 : ENV TZ="Europe/Paris"
 ---> Using cache
 ---> cd3ab6490891
Step 4/9 : RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
 ---> Using cache
 ---> 3886a1abc21a
Step 5/9 : RUN apt update  && apt -y install net-tools iputils-ping curl vim procps netcat wget gnupg2 apt-transport-https sudo lsof unzip git  && echo "alias ll='ls -lrt'" >> /root/.bashrc && echo 'PS1=$PS1A' >> ~/.bashrc && echo 'HISTFILESIZE=20000' >> ~/.bashrc && echo 'HISTSIZE=10000' >> ~/.bashrc
 ---> Using cache
 ---> d9802caad076
Step 6/9 : WORKDIR /app
 ---> Using cache
 ---> 5dbb822ee27b
Step 7/9 : COPY springfront/build/libs/spring-front.jar spring-front.jar
 ---> 479fdcd7749d
Step 8/9 : EXPOSE 8080
 ---> Running in 4592997fe4d9
Removing intermediate container 4592997fe4d9
 ---> fbd8ff70fce0
Step 9/9 : CMD java -jar spring-front.jar
 ---> Running in 8c76cb7fcb3c
Removing intermediate container 8c76cb7fcb3c
 ---> ebd58be12dac
Successfully built ebd58be12dac
Successfully tagged pejese/springfront:v2
WARNING: Image for service springfront was built because it did not already exist. To rebuild this image you must use `docker-compose build` or `docker-compose up --build`.
Building springback
Sending build context to Docker daemon    149MB
Step 1/9 : FROM adoptopenjdk/openjdk11:ubuntu-nightly-slim
 ---> 86b175442692
Step 2/9 : ENV PS1A="[\[\e[1m\]\[\e[38;5;46m\]\u\[\e[0m\]@\h:\[\e[1m\]\[\e[38;5;21m\]\w\[\e[0m\]]$ "
 ---> Using cache
 ---> 369ef1b8c1ec
Step 3/9 : ENV TZ="Europe/Paris"
 ---> Using cache
 ---> cd3ab6490891
Step 4/9 : RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
 ---> Using cache
 ---> 3886a1abc21a
Step 5/9 : RUN apt update  && apt -y install net-tools iputils-ping curl vim procps netcat wget gnupg2 apt-transport-https sudo lsof unzip git  && echo "alias ll='ls -lrt'" >> /root/.bashrc && echo 'PS1=$PS1A' >> ~/.bashrc && echo 'HISTFILESIZE=20000' >> ~/.bashrc && echo 'HISTSIZE=10000' >> ~/.bashrc
 ---> Using cache
 ---> d9802caad076
Step 6/9 : WORKDIR /app
 ---> Using cache
 ---> 5dbb822ee27b
Step 7/9 : COPY springback/build/libs/spring-back.jar spring-back.jar
 ---> 51b33bfd1377
Step 8/9 : EXPOSE 8088
 ---> Running in fd7e34ab787f
Removing intermediate container fd7e34ab787f
 ---> 991e54ed1c8d
Step 9/9 : CMD java -jar spring-back.jar --server.port=8088
 ---> Running in 680972680c6a
Removing intermediate container 680972680c6a
 ---> 3ce50627e909
Successfully built 3ce50627e909
Successfully tagged pejese/springback:v2
WARNING: Image for service springback was built because it did not already exist. To rebuild this image you must use `docker-compose build` or `docker-compose up --build`.
Creating dd-agent-dogfood-jmx ... done
Creating springfront          ... done
Creating springback           ... done
````

At this point the two images `springfront`, `springback` are built in the local repository (`pejese`) and the corresponding containers are up and running. You may want now to push those newly created images to your own remote image registry (ex: dockerhub or any other registry of your choice) by running `docker push`.



Make sure you are authenticated to your registry through the `docker login` command.

````shell
[root@pt-instance-6:~/springblog]$ docker login -u=<your user> -p=xxxxxxxxxxx
WARNING! Using --password via the CLI is insecure. Use --password-stdin.
WARNING! Your password will be stored unencrypted in /root/.docker/config.json.
Configure a credential helper to remove this warning. See
https://docs.docker.com/engine/reference/commandline/login/#credentials-store

Login Succeeded
````

And then

````shell
[root@pt-instance-6:~/springblog]$ docker push <your user>/springfront:v2
````

Another way of building the images is shown below and relies on docker commands instead of the docker-compose ones:

````shell
[root@pt-instance-6:~/springblog]$ docker build -f Dockerfiles/Dockerfile.springfront -t <your user>/springfront:v2 .
...
[root@pt-instance-6:~/springblog]$ docker login -u=<your user> -p=xxxxxxxxxxx
...
[root@pt-instance-6:~/springblog]$ docker push <your user>/springfront:v2
...
[root@pt-instance-6:~/springblog]$ docker run -it -d --name springfront -h springfront <your user>/springfront:v2
````
</br>

Let's check the status of our containers:

````shell
[root@pt-instance-6:~/springblog]$ docker-compose ps
        Name                      Command                  State                                                  Ports                                            
-------------------------------------------------------------------------------------------------------------------------------------------------------------------
dd-agent-dogfood-jmx   /bin/entrypoint.sh               Up (healthy)   0.0.0.0:8125->8125/tcp,:::8125->8125/tcp, 8125/udp, 0.0.0.0:8126->8126/tcp,:::8126->8126/tcp
springback             /bin/sh -c java -jar sprin ...   Up             0.0.0.0:8088->8088/tcp,:::8088->8088/tcp                                                    
springfront            /bin/sh -c java -jar sprin ...   Up             0.0.0.0:8080->8080/tcp,:::8080->8080/tcp 
````



And now testing them to see if the application is functional.

````shell
[root@pt-instance-6:~/springblog]$ curl localhost:8080/upstream
Quote{type='success', value=Value{id=9, quote='Alea jacta est'}}

````

Now as all the components are up and running, and every pieces work well together



When you are done with those services, you can tear them down by running this command

````shell
[root@pt-instance-6:~/springblog]$ docker-compose down
Stopping dd-agent-dogfood-jmx ... done
Stopping springfront          ... done
Stopping springback           ... done
Removing dd-agent-dogfood-jmx ... done
Removing springfront          ... done
Removing springback           ... done
Removing network app

````


## Building <a name="local"></a> the application and running it locally.


These steps assume that you have a JDK installed and configured for your environment. This tutorial has been tested with `OpenJDK 11.0.12`.
And you will also need to have gradle installed, the version used in this example is `7.5.1` 


### Starting the Datadog Agent first ###

First set your API Key:

````shell
[root@pt-instance-6:~/springblog]$ export DD_API_KEY=<Your api key>
````

Then let's run the agent. As docker is installed on our environment, we will use a dockerized version of the agent. 

But if you wish to have it deployed as a standalone service you will want to follow the instructions as per [Datadog Agent installation](https://app.datadoghq.com/account/settings?_gl=1*17qq65s*_gcl_aw*R0NMLjE2NzY0Mzg4NTcuQ2p3S0NBaUFfNnlmQmhCTkVpd0FrbVh5NTcxNlplWmxIQ3RBS0MwdWdUeWIyNnZSRGN1Q01YUHJoZHlYU2Zaekt4eTNNZjZST1I4SVVSb0NwT2NRQXZEX0J3RQ..*_ga*NzYyNTQxODI3LjE2MDExNDI4ODA.*_ga_KN80RDFSQK*MTY3NjgwOTY3NS4zMC4xLjE2NzY4MDk3MDQuMzEuMC4w#agent/ubuntu)



By default, the Datadog Agent is enabled in your `datadog.yaml` file under `apm_config` with `enabled: true` and listens for trace data at `http://localhost:8126`


````shell
[root@pt-instance-6:~/springblog]$ docker run -d --network app --name dd-agent-dogfood-jmx -v /var/run/docker.sock:/var/run/docker.sock:ro \
-v /proc/:/host/proc/:ro \
-v /sys/fs/cgroup/:/host/sys/fs/cgroup:ro \
-v /home/pej/conf.d/:/etc/datadog-agent/conf.d \
-p 8126:8126 -p 8125:8125/udp \
-e DD_API_KEY \
-e DD_APM_ENABLED=true \
-e DD_APM_NON_LOCAL_TRAFFIC=true -e DD_PROCESS_AGENT_ENABLED=true -e DD_DOGSTATSD_NON_LOCAL_TRAFFIC="true" -e DD_LOG_LEVEL=debug \
-e DD_LOGS_ENABLED=true \
-e DD_LOGS_CONFIG_CONTAINER_COLLECT_ALL=true \
-e DD_CONTAINER_EXCLUDE_LOGS="name:datadog-agent" \
-e SD_JMX_ENABLE=true \
gcr.io/datadoghq/agent:latest-jmx
Unable to find image 'gcr.io/datadoghq/agent:latest-jmx' locally
latest-jmx: Pulling from datadoghq/agent
8dbf11a29570: Pull complete 
Digest: sha256:c7fe7c8d15f259185ab0c60dbfb7f5cbc67d09b5749af0d2fee45cefe2ccb05f
Status: Downloaded newer image for gcr.io/datadoghq/agent:latest-jmx
2d1eec89c2196d298d1e3edf1e9f879c0fc3be593d96f1469cfacc2cacfc18b4
````

In order to instrument our services, we will also need to use a java tracing library (`dd-java-agent.jar`). 
 
To install the java tracing client, download `dd-java-agent.jar`, which contains the Agent class files
`wget -O dd-java-agent.jar 'https://dtdg.co/latest-java-tracer'`

But you can skip this as the client is already available in this repo. Now let's build, instrument and run our services.


### Building and running **springback** ###

````shell
[root@pt-instance-6:~/springblog]$ cd springback
[root@pt-instance-6:~/springblog/springback]$ gradle build

BUILD SUCCESSFUL in 10s
[root@pt-instance-6:~/springblog/springback]$ nohup java -jar springback/build/libs/spring-back.jar --server.port=8088 &
[1] 19870
[root@pt-instance-6:~/springblog/springback]$ nohup: ignoring input and appending output to 'nohup.out'
````

We can check that the service is running by taking a look at content of the `nohup.out`

````shell
[root@pt-instance-6:~/springblog/springback]$ cat nohup.out

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.6.1)

2023-02-19 12:42:30 [main] INFO  c.d.pej.back.SpringBackApplication -   - Starting SpringBackApplication using Java 11.0.12 on pt-instance-6 with PID 19870 (/root/springblog/springback/build/libs/spring-back.jar started by root in /root/springblog)
2023-02-19 12:42:30 [main] INFO  c.d.pej.back.SpringBackApplication -   - No active profile set, falling back to default profiles: default
2023-02-19 12:42:32 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer -   - Tomcat initialized with port(s): 8088 (http)
2023-02-19 12:42:32 [main] INFO  o.a.catalina.core.StandardService -   - Starting service [Tomcat]
2023-02-19 12:42:32 [main] INFO  o.a.catalina.core.StandardEngine -   - Starting Servlet engine: [Apache Tomcat/9.0.55]
2023-02-19 12:42:32 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] -   - Initializing Spring embedded WebApplicationContext
2023-02-19 12:42:32 [main] INFO  o.s.b.w.s.c.ServletWebServerApplicationContext -   - Root WebApplicationContext: initialization completed in 1556 ms
2023-02-19 12:42:33 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer -   - Tomcat started on port(s): 8088 (http) with context path ''
2023-02-19 12:42:33 [main] INFO  c.d.pej.back.SpringBackApplication -   - Started SpringBackApplication in 3.324 seconds (JVM running for 3.994)
2023-02-19 12:42:33 [main] INFO  c.d.pej.back.SpringBackApplication -   - test

````

The service is started and listens on port `8088` and exposes an endpoint `/downstream` that will be hit by the `springfront` service. 
Let's now build and test `springfront`


### Building and running **springfront** ###

````shell
[root@pt-instance-6:~/springblog/springback]$ cd ../springfront
[root@pt-instance-6:~/springblog/springfront]$ gradle build

BUILD SUCCESSFUL in 7s
[root@pt-instance-6:~/springblog/springfront]$ nohup java -jar build/libs/spring-front.jar &
[1] 20545
[root@pt-instance-6:~/springblog/springfront]$ nohup: ignoring input and appending output to 'nohup.out'

[root@pt-instance-6:~/springblog/springfront]$ cat nohup.out

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::        (v2.1.0.RELEASE)

2023-02-19 12:49:10 [main] INFO  c.d.pej.front.SpringFrontApplication -   - Starting SpringFrontApplication on pt-instance-6 with PID 20545 (/root/springblog/springfront/build/libs/spring-front.jar started by root in /root/springblog/springfront)
2023-02-19 12:49:10 [main] INFO  c.d.pej.front.SpringFrontApplication -   - No active profile set, falling back to default profiles: default
2023-02-19 12:49:11 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer -   - Tomcat initialized with port(s): 8080 (http)
2023-02-19 12:49:11 [main] INFO  o.a.catalina.core.StandardService -   - Starting service [Tomcat]
2023-02-19 12:49:11 [main] INFO  o.a.catalina.core.StandardEngine -   - Starting Servlet Engine: Apache Tomcat/9.0.12
2023-02-19 12:49:11 [main] INFO  o.a.c.core.AprLifecycleListener -   - The APR based Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: [/usr/java/packages/lib:/usr/lib64:/lib64:/lib:/usr/lib]
2023-02-19 12:49:11 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] -   - Initializing Spring embedded WebApplicationContext
2023-02-19 12:49:11 [main] INFO  o.s.web.context.ContextLoader -   - Root WebApplicationContext: initialization completed in 1713 ms
2023-02-19 12:49:12 [main] INFO  o.s.b.w.s.ServletRegistrationBean -   - Servlet dispatcherServlet mapped to [/]
2023-02-19 12:49:12 [main] INFO  o.s.b.w.s.FilterRegistrationBean -   - Mapping filter: 'characterEncodingFilter' to: [/*]
2023-02-19 12:49:12 [main] INFO  o.s.b.w.s.FilterRegistrationBean -   - Mapping filter: 'hiddenHttpMethodFilter' to: [/*]
2023-02-19 12:49:12 [main] INFO  o.s.b.w.s.FilterRegistrationBean -   - Mapping filter: 'formContentFilter' to: [/*]
2023-02-19 12:49:12 [main] INFO  o.s.b.w.s.FilterRegistrationBean -   - Mapping filter: 'requestContextFilter' to: [/*]
2023-02-19 12:49:12 [main] INFO  o.s.s.c.ThreadPoolTaskExecutor -   - Initializing ExecutorService 'applicationTaskExecutor'
2023-02-19 12:49:12 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer -   - Tomcat started on port(s): 8080 (http) with context path ''
2023-02-19 12:49:12 [main] INFO  c.d.pej.front.SpringFrontApplication -   - Started SpringFrontApplication in 3.376 seconds (JVM running for 4.027)
2023-02-19 12:49:12 [main] INFO  c.d.pej.front.SpringFrontApplication -   - Initial call 
````

The service is started and listens on port `8080`. And exposes an `/upstream` endpoint. We can quickly check that the communication takes place by issuing this curl command to hit the `/upstream` endpoint exposed by `springfront`

````shell
[root@pt-instance-6:~/springblog/springfront]$ curl localhost:8080/upstream
Quote{type='success', values=Values{id=6, quote='Alea jacta est'}}
````

Everything is now in place. We can now start instrumenting those services.
In order to do so we will need to add some options to the jvm when lauching the services. As there are two services, each set of options will have specific details related to them (Essentially the service names that will be used in the Datadog APM UI)

For `springfront` we will use the following:

````shell
-javaagent:/root/springblog/dd-java-agent.jar -Ddd.service=springfront -Ddd.env=dev -Ddd.version=12 -Ddd.trace.sample.rate=1 -Ddd.logs.injection=true -Ddd.profiling.enabled=true -XX:FlightRecorderOptions=stackdepth=256 -Ddd.trace.http.client.split-by-domain=true -Ddd.tags=env:dev

````

For `springback` we will use the following:


````shell
-javaagent:/root/springblog/dd-java-agent.jar -Ddd.service=springback -Ddd.env=dev -Ddd.version=12 -Ddd.trace.sample.rate=1 -Ddd.logs.injection=true -Ddd.profiling.enabled=true -XX:FlightRecorderOptions=stackdepth=256 -Ddd.trace.http.client.split-by-domain=true -Ddd.tags=env:dev

````


Let's change the commands we used before to launch both services accordingly:

`springback`

````shell
[root@pt-instance-6:~/springblog/springback]$ cd ..
[root@pt-instance-6:~/springblog]$ nohup java -javaagent:/root/springblog/dd-java-agent.jar -Ddd.service=springback -Ddd.env=dev -Ddd.version=12 -Ddd.trace.sample.rate=1 -Ddd.logs.injection=true -Ddd.profiling.enabled=true -XX:FlightRecorderOptions=stackdepth=256 -Ddd.trace.http.client.split-by-domain=true -Ddd.tags=env:dev -jar springback/build/libs/spring-back.jar --server.port=8088 &
[1] 19870
[root@pt-instance-6:~/springblog]$ nohup: ignoring input and appending output to 'nohup.out'
````

We can check that the service is running by taking a look at content of the `nohup.out`
You can see that before the spring banner gets displayed, some entries tied to the instrumention operations that are taking place.

````shell
[dd.trace 2023-02-19 13:10:58:462 +0000] [main] INFO com.datadog.appsec.AppSecSystem - AppSec is ENABLED_INACTIVE with powerwaf(libddwaf: 1.5.1) no rules loaded
[dd.trace 2023-02-19 13:10:58:516 +0000] [dd-telemetry] WARN datadog.telemetry.TelemetryRunnable - IOException on HTTP request to Telemetry Intake Service: java.net.UnknownHostException: dd-agent-dogfood-jmx
[dd.trace 2023-02-19 13:10:58:669 +0000] [main] INFO com.datadog.profiling.context.PerSpanTracingContextTrackerFactory - Using JFR time ticks provider
[dd.trace 2023-02-19 13:10:58:670 +0000] [main] INFO com.datadog.profiling.context.allocator.heap.HeapAllocator - HeapAllocator created with the limit of 33554432 bytes
[dd.trace 2023-02-19 13:10:58:797 +0000] [dd-task-scheduler] INFO datadog.trace.agent.core.StatusLogger - DATADOG TRACER CONFIGURATION {"version":"1.2.0~a26b8ca0ff","os_name":"Linux","os_version":"5.19.0-1017-gcp","architecture":"amd64","lang":"jvm","lang_version":"11.0.12","jvm_vendor":"Oracle Corporation","jvm_version":"11.0.12+7","java_class_version":"55.0","http_nonProxyHosts":"null","http_proxyHost":"null","enabled":true,"service":"springback","agent_url":"http://localhost:8126","agent_error":true,"debug":false,"analytics_enabled":false,"sample_rate":1.0,"sampling_rules":[{},{}],"priority_sampling_enabled":true,"logs_correlation_enabled":true,"profiling_enabled":true,"remote_config_enabled":true,"debugger_enabled":false,"appsec_enabled":"ENABLED_INACTIVE","telemetry_enabled":true,"dd_version":"12","health_checks_enabled":true,"configuration_file":"no config file present","runtime_id":"d458dbce-099b-4adf-8220-2bbd273b62fa","logging_settings":{"levelInBrackets":false,"dateTimeFormat":"'[dd.trace 'yyyy-MM-dd HH:mm:ss:SSS Z']'","logFile":"System.err","configurationFile":"simplelogger.properties","showShortLogName":false,"showDateTime":true,"showLogName":true,"showThreadName":true,"defaultLogLevel":"INFO","warnLevelString":"WARN","embedException":false},"cws_enabled":false,"cws_tls_refresh":5000}

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.6.1)

...
````


`springfront`

````shell
[root@pt-instance-6:~/springblog]$ nohup java -javaagent:/root/springblog/dd-java-agent.jar -Ddd.service=springfront -Ddd.env=dev -Ddd.version=12 -Ddd.trace.sample.rate=1 -Ddd.logs.injection=true -Ddd.profiling.enabled=true -XX:FlightRecorderOptions=stackdepth=256 -Ddd.trace.http.client.split-by-domain=true -Ddd.tags=env:dev -jar springfront/build/libs/spring-front.jar
[1] 19789
[root@pt-instance-6:~/springblog]$ nohup: ignoring input and appending output to 'nohup.out'
````


## Testing the application and generating load


Now by running a few curl commands on `/upstream`, and after a few seconds, we can check that our services are being instrumented and that the details are reflected in this trace flamegraph. 

<p align="left">
  <img src="img/imgRest3.png" width="650" />
</p>

</br>

Besides we can also visualize the topology representation of this call

<p align="left">
  <img src="img/imgRest4.png" width="650" />
</p>


## Building <a name="k8s"></a> the application and running it on a kubernetes cluster


### Building the cluster ###

Let's first build a cluster. In this tutorial we are going to create a 3 node cluster on a google cloud (GKE). You may of course consider any other cloud provider to do so

The first steps consists of creating the cluste using the command line utility `gcloud` and pass the necessary information pertaining to the type, region, sizing, disk, network...required to spin up our cluster. We are going to set a tag for this cluster `pej-cluster-1-nw-tag` that will be used during the firewall configuration to match the rule and the services tied to this cluster.


Our cluster name is `pej-cluster-1`

````shell
[root@pt-instance-6:~/springblog]$ gcloud beta container --project "datadog-project" clusters create "pej-cluster-1" --zone "us-central1-c" --no-enable-basic-auth --cluster-version "1.24.9-gke.2000" --release-channel "regular" --machine-type "e2-medium" --image-type "COS_CONTAINERD" --disk-type "pd-balanced" --disk-size "100" --metadata disable-legacy-endpoints=true --scopes "https://www.googleapis.com/auth/devstorage.read_only","https://www.googleapis.com/auth/logging.write","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management.readonly","https://www.googleapis.com/auth/trace.append" --max-pods-per-node "110" --num-nodes "3" --logging=SYSTEM,WORKLOAD --monitoring=SYSTEM --enable-ip-alias --network "projects/datadog-project/global/networks/pej-network" --subnetwork "projects/datadog-project/regions/us-central1/subnetworks/pej-network" --no-enable-intra-node-visibility --default-max-pods-per-node "110" --no-enable-master-authorized-networks --addons HorizontalPodAutoscaling,HttpLoadBalancing,GcePersistentDiskCsiDriver --enable-autoupgrade --enable-autorepair --max-surge-upgrade 1 --max-unavailable-upgrade 0 --enable-shielded-nodes --tags "pej-cluster-1-nw-tag" --node-locations "us-central1-c"

Default change: During creation of nodepools or autoscaling configuration changes for cluster versions greater than 1.24.1-gke.800 a default location policy is applied. For Spot and PVM it defaults to ANY, and for all other VM kinds a BALANCED policy is used. To change the default values use the `--location-policy` flag.
Note: The Pod address range limits the maximum size of the cluster. Please refer to https://cloud.google.com/kubernetes-engine/docs/how-to/flexible-pod-cidr to learn how to optimize IP address allocation.
Creating cluster pej-cluster-1 in us-central1-c... Cluster is being health-checked (master is healthy)...done.                                                                                       
Created [https://container.googleapis.com/v1beta1/projects/datadog-project/zones/us-central1-c/clusters/pej-cluster-1].
To inspect the contents of your cluster, go to: https://console.cloud.google.com/kubernetes/workload_/gcloud/us-central1-c/pej-cluster-1?project=datadog-project
kubeconfig entry generated for pej-cluster-1.
NAME           LOCATION       MASTER_VERSION   MASTER_IP      MACHINE_TYPE  NODE_VERSION     NUM_NODES  STATUS
pej-cluster-1  us-central1-c  1.24.9-gke.2000  34.28.253.115  e2-medium     1.24.9-gke.2000  3          RUNNING


Updates are available for some Google Cloud CLI components.  To install them,
please run:
  $ gcloud components update
````


### Configuring the firewall rule ###

Now that the cluster is created let's configure the firewall rules by running the following gcloud command. This rule will allow the source range of IP addresses to use specific protocols and ports to access the applications and services running on the cluster. 


````shell
[root@pt-instance-6:~/springblog]$ gcloud compute --project=datadog-project firewall-rules create pej-cluster-1-fw --direction=INGRESS --priority=1000 --network=pej-network --action=ALLOW --rules=tcp:22,tcp:80,tcp:8080,tcp:8088 --source-ranges=109.24.121.20/32 --target-tags=pej-cluster-1-nw-tag
Creating firewall...⠹Created [https://www.googleapis.com/compute/v1/projects/datadog-project/global/firewalls/pej-cluster-1-fw].                                                                     
Creating firewall...done.                                                                                                                                                                            
NAME              NETWORK      DIRECTION  PRIORITY  ALLOW                                     DENY  DISABLED
pej-cluster-1-fw  pej-network  INGRESS    1000      tcp:22,tcp:80,tcp:8080,tcp:8088,tcp:9088        False
````


### Deploying the Datadog Agent ###


Now that the cluster is up and running and the firewall rule configured, the Datadog Agent deployed through the helm chart, we are ready to deploy our pods and k8s services that will be used to access the application endpoint.

The deployment manifest (`deployment.yaml`) is provided and can be used as is. It contains the details to create two pods (one for `springfront` and a second for `springback`) and two services (one for accessing `springfront` from the internet and that is of LoadBalancer type and second that will allow `springfront` to communicate with `springback` directly inside the cluster)


````shell
[root@pt-instance-6:~/springblog]$ kubectl apply -f k8s/deployment.yaml 
deployment.apps/springfront created
deployment.apps/springback created
service/springfront created
service/springback created
````


### Component state ###

Our pods and services have been created successfully and we can now check their respective state. We can also check the log content from one of the pod. 

State of the pods: 

````shell
[root@pt-instance-6:~/springblog]$ kubectl get pods
NAME                           READY   STATUS    RESTARTS   AGE
springback-754bf5764b-gfn7s    1/1     Running   0          36s
springfront-75ffb9cc79-drcht   1/1     Running   0          36s
````


Logs of one of the pods (`springback`):

````shell
[root@pt-instance-6:~/springblog]$ kubectl logs springback-754bf5764b-gfn7s
Picked up JAVA_TOOL_OPTIONS: -javaagent:/app/javaagent/dd-java-agent.jar  -Ddd.env=dev -Ddd.service=springback  -Ddd.version=12 -Ddd.tags=env:dev -Ddd.trace.sample.rate=1 -Ddd.logs.injection=true  -Ddd.profiling.enabled=true -XX:FlightRecorderOptions=stackdepth=256  -Ddd.trace.http.client.split-by-domain=true

[dd.trace 2023-02-20 00:31:39:399 +0100] [main] INFO com.datadog.appsec.AppSecSystem - AppSec is ENABLED_INACTIVE with powerwaf(libddwaf: 1.6.2) no rules loaded
[dd.trace 2023-02-20 00:31:39:578 +0100] [dd-task-scheduler] INFO datadog.trace.agent.core.StatusLogger - DATADOG TRACER CONFIGURATION {"version":"1.8.3~d70013d2e6","os_name":"Linux","os_version":"5.10.147+","architecture":"amd64","lang":"jvm","lang_version":"11.0.17-beta","jvm_vendor":"Eclipse Adoptium","jvm_version":"11.0.17-beta+7-202210061739","java_class_version":"55.0","http_nonProxyHosts":"null","http_proxyHost":"null","enabled":true,"service":"springback","agent_url":"http://10.128.15.237:8126","agent_error":true,"debug":false,"analytics_enabled":false,"sample_rate":1.0,"sampling_rules":[{},{}],"priority_sampling_enabled":true,"logs_correlation_enabled":true,"profiling_enabled":true,"remote_config_enabled":true,"debugger_enabled":false,"appsec_enabled":"ENABLED_INACTIVE","telemetry_enabled":true,"dd_version":"12","health_checks_enabled":true,"configuration_file":"no config file present","runtime_id":"902ce94d-8cc5-44c9-b9fe-3aec4f8c37f7","logging_settings":{"levelInBrackets":false,"dateTimeFormat":"'[dd.trace 'yyyy-MM-dd HH:mm:ss:SSS Z']'","logFile":"System.err","configurationFile":"simplelogger.properties","showShortLogName":false,"showDateTime":true,"showLogName":true,"showThreadName":true,"defaultLogLevel":"INFO","warnLevelString":"WARN","embedException":false},"cws_enabled":false,"cws_tls_refresh":5000,"datadog_profiler_enabled":false,"datadog_profiler_safe":false}

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.6.1)

2023-02-20 00:31:45 [main] INFO  c.d.pej.back.SpringBackApplication -   - Starting SpringBackApplication using Java 11.0.17-beta on springback-754bf5764b-gfn7s with PID 7 (/app/spring-back.jar started by root in /app)
2023-02-20 00:31:45 [main] INFO  c.d.pej.back.SpringBackApplication -   - No active profile set, falling back to default profiles: default
2023-02-20 00:31:51 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer -   - Tomcat initialized with port(s): 8088 (http)
2023-02-20 00:31:51 [main] INFO  o.a.catalina.core.StandardService -   - Starting service [Tomcat]
2023-02-20 00:31:51 [main] INFO  o.a.catalina.core.StandardEngine -   - Starting Servlet engine: [Apache Tomcat/9.0.55]
2023-02-20 00:31:52 [main] INFO  o.a.c.c.C.[Tomcat].[localhost].[/] -   - Initializing Spring embedded WebApplicationContext
2023-02-20 00:31:52 [main] INFO  o.s.b.w.s.c.ServletWebServerApplicationContext -   - Root WebApplicationContext: initialization completed in 6034 ms
2023-02-20 00:31:54 [main] INFO  o.s.b.w.e.tomcat.TomcatWebServer -   - Tomcat started on port(s): 8088 (http) with context path ''
2023-02-20 00:31:54 [main] INFO  c.d.pej.back.SpringBackApplication -   - Started SpringBackApplication in 12.155 seconds (JVM running for 18.265)
2023-02-20 00:31:54 [main] INFO  c.d.pej.back.SpringBackApplication -   - test
````

The output above shows that the service has started and that the Datadog java tracing library has started to instrument the service. 


State of the k8s services (`LoadBalancer` and `ClusterIP`)

````shell
[root@pt-instance-6:~/springblog]$ kubectl get svc
NAME          TYPE           CLUSTER-IP     EXTERNAL-IP     PORT(S)          AGE
kubernetes    ClusterIP      10.20.0.1      <none>          443/TCP          11m
springback    ClusterIP      10.20.7.220    <none>          8088/TCP         81s
springfront   LoadBalancer   10.20.11.109   34.133.204.98   8080:32753/TCP   81s
````

### Testing the application ###

We can now curl the enpoint by using the external IP of the cluster (34.133.204.98) on port 8080 which is the listening port for `springfront`. 

````shell
[root@pt-instance-6:~/springblog]$ curl 34.133.204.98:8080/upstream
Quote{type='success', values=Values{id=5, quote='Alea jacta est'}}
````


### Deleting the cluster ###

````shell
[root@pt-instance-6:~/springblog]$ gcloud container clusters delete pej-cluster-1 --zone "us-central1-c"
The following clusters will be deleted.
 - [pej-cluster-1] in [us-central1-c]

Do you want to continue (Y/n)?  y

Deleting cluster pej-cluster-1...done.                                                                                                                                                               
Deleted [https://container.googleapis.com/v1/projects/datadog-project/zones/us-central1-c/clusters/pej-cluster-1].
````

### Deleting the firewall rule ###

````shell
[root@pt-instance-6:~/springblog]$ gcloud compute firewall-rules delete pej-cluster-1-fw
The following firewalls will be deleted:
 - [pej-cluster-1-fw]

Do you want to continue (Y/n)?  Y

Deleted [https://www.googleapis.com/compute/v1/projects/datadog-project/global/firewalls/pej-cluster-1-fw].
````

## End
