FROM maven:3.6-jdk-8-slim AS builder

# Get Click Count job and compile it
COPY ./streamapp /opt/flink-stream-app
WORKDIR /opt/flink-stream-app
RUN mvn clean install


###############################################################################
# Build Flink Image
###############################################################################

FROM flink:1.9.0-scala_2.11

WORKDIR /opt/flink/bin

# Copy Stream App
COPY --from=builder /opt/flink-stream-app/target/stream-app-*.jar /opt/StreamApp.jar