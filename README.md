[About Ayah Alrefai](https://github.com/AyahAlrifai/AyahAlrifai/blob/main/README.md)

# Getting Started

### RabbitMQ Docker Image

```powershell
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=userName -e RABBITMQ_DEFAULT_PASS=password rabbitmq:3.12-management
```
then run docker image.

### Update application.properties File
update the following prop in application.properties

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=port
spring.rabbitmq.username=username
spring.rabbitmq.password=password
```

### Swagger URL

[http://localhost:8088/rabbitmq/swagger-ui/index.html#/](http://localhost:8088/rabbitmq/swagger-ui/index.html#/)

### Expiry Time Example

#### Example 1

Send this request 3 times.

```bash
curl -X 'GET' \
  'http://localhost:8088/rabbitmq/direct-exchange?message=HelloWorld&routingKey=log1&expiration=0' \
  -H 'accept: */*'
```
- q1 `x-message-ttl` equal 3000ml.
- q1 `prefetch count` equal 2.
- q1 is connect to `dead-letter-exchange`.
- Each message need 4000ms to execute in q1.
- So q1 will get first message and second message, but third message will be routing
to dead letter exchange.

```text
Message: HelloWorld RoutingKey: log1
q1 ------------> Received Message: HelloWorld
q1 ------------> Waiting 4000ms
Message: HelloWorld RoutingKey: log1
Message: HelloWorld RoutingKey: log1
deadLetter ------------> Received Message: HelloWorld
q1 ------------> Received Message: HelloWorld
q1 ------------> Waiting 4000ms
```

#### Example 2

Send this request 3 times.

```bash
curl -X 'GET' \
  'http://localhost:8088/rabbitmq/direct-exchange?message=HelloWorld&routingKey=log2&expiration=0' \
  -H 'accept: */*'
```
- q2 `x-message-ttl` equal 3000ml.
- q2 `prefetch count` equal 2.
- Each message need 4000ms to execute in q2.
- so q2 will get first message and second message, but third message will be deleted because q2
  not link with dead letter exchange.

```text
Message: HelloWorld RoutingKey: log2
q2 ------------> Received Message: HelloWorld
q2 ------------> Waiting 4000ms
Message: HelloWorld RoutingKey: log2
Message: HelloWorld RoutingKey: log2
q2 ------------> Received Message: HelloWorld
q2 ------------> Waiting 4000ms
```

#### Example 3

Send this request 3 times.

```bash
curl -X 'GET' \
  'http://localhost:8088/rabbitmq/direct-exchange?message=HelloWorld&routingKey=log1&expiration=2000' \
  -H 'accept: */*'
```

- q1 `x-message-ttl` equal 3000ml.
- q1 `prefetch count` equal 2.
- q1 is connect to `dead-letter-exchange`.
- Each message need 4000ms to execute in q1.
- The expiration for each message is 2000 and 2000 less than 3000, so the `x-message-ttl` will not override by message expiration time, so it will work same as $$Example 1$$

```text
Message: HelloWorld RoutingKey: log1
q1 ------------> Received Message: HelloWorld
q1 ------------> Waiting 4000ms
Message: HelloWorld RoutingKey: log1
Message: HelloWorld RoutingKey: log1
deadLetter ------------> Received Message: HelloWorld
q1 ------------> Received Message: HelloWorld
q1 ------------> Waiting 4000ms
```

#### Example 4

Send this request 3 times.

```bash
curl -X 'GET' \
  'http://localhost:8088/rabbitmq/direct-exchange?message=HelloWorld&routingKey=log1&expiration=5000' \
  -H 'accept: */*'
```

- q1 `x-message-ttl` equal 3000ml.
- q1 `prefetch count` equal 2.
- q1 is connect to `dead-letter-exchange`.
- Each message need 4000ms to execute in q1.
- The expiration for each message is 5000 and 5000 greater than 3000, so the `x-message-ttl` will be override by message expiration time, so q1 will receive all messages.

```text
Message: HelloWorld RoutingKey: log1
q1 ------------> Received Message: HelloWorld
q1 ------------> Waiting 4000ms
Message: HelloWorld RoutingKey: log1
Message: HelloWorld RoutingKey: log1
q1 ------------> Received Message: HelloWorld
q1 ------------> Waiting 4000ms
q1 ------------> Received Message: HelloWorld
q1 ------------> Waiting 4000ms
```

#### Example 5

Send this request 3 times.

```bash
curl -X 'GET' \
  'http://localhost:8088/rabbitmq/direct-exchange?message=HelloWorld&routingKey=log2&expiration=5000' \
  -H 'accept: */*'
```

- q2 `x-message-ttl` equal 3000ml.
- q2 `prefetch count` equal 2.
- Each message need 4000ms to execute in q1.
- The expiration for each message is 5000 and 5000 greater than 3000, so the `x-message-ttl` will be override by message expiration time, so q2 will receive all messages.

```text
Message: HelloWorld RoutingKey: log2
q2 ------------> Received Message: HelloWorld
q2 ------------> Waiting 4000ms
Message: HelloWorld RoutingKey: log2
Message: HelloWorld RoutingKey: log2
q2 ------------> Received Message: HelloWorld
q2 ------------> Waiting 4000ms
q2 ------------> Received Message: HelloWorld
q2 ------------> Waiting 4000ms
```

## Acknowledgment Example

#### Example 1

```bash
curl -X 'GET' \
  'http://localhost:8088/rabbitmq/direct-exchange?message=HelloWorld&routingKey=log3&expiration=0' \
  -H 'accept: */*'
```
Message Automatically deleted from queue when the consumer get the message.

```text
Message: HelloWorld RoutingKey: log3
q3 ------------> Received Message: HelloWorld
q3 ------------> Positive ACK 
```

#### Example 2

```bash
curl -X 'GET' \
  'http://localhost:8088/rabbitmq/direct-exchange?message=don%27t%20requeue&routingKey=log4&expiration=0' \
  -H 'accept: */*'
```
Message deleted from queue after consumer reject message without requeue it.

```text
Message: don't requeue RoutingKey: log4
q4 ------------> Received Message: don't requeue
q4 ------------> Negative ACK, don't requeue message
```

#### Example 3

```bash
curl -X 'GET' \
  'http://localhost:8088/rabbitmq/direct-exchange?message=don%27t%20requeue&routingKey=log4&expiration=0' \
  -H 'accept: */*'
```
Message requeue to dead letter exchange when consumer reject message with requeue it.

Note: when requeue message, the queue should link with dead letter exchange.

```text
Message: requeue RoutingKey: log4
q4 ------------> Received Message: requeue
q4 ------------> Negative ACK, requeue message
deadLetter ------------> Received Message: requeue
```

#### Example 4

```bash
curl -X 'GET' \
  'http://localhost:8088/rabbitmq/direct-exchange?message=requeuedd&routingKey=log4&expiration=0' \
  -H 'accept: */*'
```

In this case the message is in `unacked` list and will move to `ready` list after application stop.

```text
Message: requeuedd RoutingKey: log4
q4 ------------> Received Message: requeuedd
```

## Capacity Example

### Example 1

```bash
curl --location 'http://localhost:8088/rabbitmq/direct-exchange?message=1&routingKey=log5&expiration=0' \
--header 'accept: */*'
curl --location 'http://localhost:8088/rabbitmq/direct-exchange?message=2&routingKey=log5&expiration=0' \
--header 'accept: */*'
curl --location 'http://localhost:8088/rabbitmq/direct-exchange?message=3&routingKey=log5&expiration=0' \
--header 'accept: */*'
curl --location 'http://localhost:8088/rabbitmq/direct-exchange?message=4&routingKey=log5&expiration=0' \
--header 'accept: */*'
curl --location 'http://localhost:8088/rabbitmq/direct-exchange?message=5&routingKey=log5&expiration=0' \
--header 'accept: */*'
```

```text
Message: 1 RoutingKey: log5
q5 ------------> Received Message: 1
q5 ------------> Waiting 10000ms
Message: 2 RoutingKey: log5
Message: 3 RoutingKey: log5
Message: 4 RoutingKey: log5
q5 ------------> Received Message: 2
q5 ------------> Waiting 10000ms
Message: 5 RoutingKey: log5
q5 ------------> Received Message: 5
q5 ------------> Waiting 10000ms
```