## Spring boot with logback & profile  

> application.properties  

```aidl
logging.config=classpath:logback-spring.xml
```  

> logback-spring.xml  

```aidl
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!--<include resource="org/springframework/boot/logging/logback/base.xml"/>-->
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <define name="jarLocation" class="demo.util.CustomPropertyDefiner"/>
    <shutdownHook class="ch.qos.logback.core.hook.DelayingShutdownHook"/>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %p [%c{1}] %m%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${jarLocation}/logs/server.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- rollover hourly -->
            <fileNamePattern>${jarLocation}/logs/server-%d{yyyy-MM-dd-'h'HH}.log.zip</fileNamePattern>
            <!-- ~1 month -->
            <maxHistory>720</maxHistory>
            <totalSizeCap>10GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %p [%c{1}] %m%n</pattern>
        </encoder>
    </appender>

    <appender name="ASYNC" class="ch.qos.logback.classic.AsyncAppender">
        <!-- Don't discard INFO, DEBUG, TRACE events in case of queue is 80% full -->
        <discardingThreshold>0</discardingThreshold>
        <!-- Default is 256 -->
        <!-- Logger will block incoming events (log calls) until queue will free some space -->
        <!-- (the smaller value -> flush occurs often) -->
        <queueSize>100</queueSize>
        <appender-ref ref="FILE"/>
    </appender>

    <springProfile name="local">
        <root level="INFO">
            <appender-ref ref="STDOUT"/>
        </root>
    </springProfile>

    <springProfile name="!local">
        <root level="WARN">
            <appender-ref ref="STDOUT"/>
            <appender-ref ref="ASYNC"/>
        </root>
    </springProfile>
</configuration>
```