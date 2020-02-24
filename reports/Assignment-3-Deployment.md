# Deployment

In order to deploy the platform, it is neede to be done several steps:

1. Install the stream analytics app and create a docker image out of it:
```bash
$ cd ./code/flink-stream-app/streamapp
$ mvn clean install
```

Wait for application to build successfully.

2. After that, we must enter docker directory:
```bash
$ cd ../../docker/bdp
$ docker-compose up -d
```

This will deploy our platform. In order to check if the platform has successfully deployed, please check the following links:
- [RabbitMq](http://localhost:15672/#/queues)
- [Flink Dashboard](http://localhost:8081/#/job/running)

Run `docker-compose down` to shut down the platform.

3. After checking that the above mentioned links are accessible and our infrastructure is up and runnin, we have to proceed to testing our application.
```bash
$ cd ../../testframework
$ mvn clean test
```

This will run our test that will read the data from the file located in `src/test/resources/data` folder, will insertthem as JSON to `alerts` exchange, and will create a listener for `my.output` queue.


 Note that at step one, the built application will build the logic for analyzing `Equipment Failed` alert messages. In order to test other analytics, change the main class in the `pom.xml` file of the streamapp project to one of those: `PowerGridVoltageCount` or `TotalBatteryVoltageCount`.

 Rebuild project. Redeploy the plarform. Rerun the test.

 In order to run the test that introduces error to the messages, move the annotation `@RunOnlyThis` from the second scenario to the third one. Rerun `mvn clean test`. It should only execute the last test scenario.

