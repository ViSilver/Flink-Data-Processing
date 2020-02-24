###############################################################################
# Build Flink Image
###############################################################################

FROM flink:1.9.0-scala_2.11

ARG JAR_FILE

WORKDIR /opt/flink/bin

# Copy Stream App
COPY ${JAR_FILE} /opt/StreamApp.jar
