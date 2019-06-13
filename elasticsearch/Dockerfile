FROM gradle:5.4.1-jdk12 AS builder

USER gradle
COPY . /app/

WORKDIR /app/ingest-textanalytics
RUN gradle clean check assembleZip

FROM docker.elastic.co/elasticsearch/elasticsearch-oss:7.1.1

RUN bin/elasticsearch-plugin install --batch ingest-attachment

COPY --from=builder /app/ingest-textanalytics/build/distributions/ingest-textanalytics-0.0.1-SNAPSHOT-deploy.zip /plugins/
RUN bin/elasticsearch-plugin install --batch file:///plugins/ingest-textanalytics-0.0.1-SNAPSHOT-deploy.zip
