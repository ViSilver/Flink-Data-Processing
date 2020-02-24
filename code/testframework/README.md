Prerequisites: Have Java, Maven, and Docker installed on your computer

Run Docker

Run this project by executing: `$mvn clean test`

What is this command doing:
1. It will compile the project and will run the test
2. In order to test the RabbitMQ, there will be used a docker container instead of the RabbitMQ instance
3. Thus, before the tests are executed, a docker image will be pulled: `rabbitmq:management`
4. A container will be run out of this image
5. The testing framework will connect to this container by dynamically finding out the exported port
6. The testing framework will create a queue
7. The testing framework will send a message to the queue and will listen to it

For a higher flexibility, it may be used a `GenericContainer` instead of `RabbitMQContainer`.
