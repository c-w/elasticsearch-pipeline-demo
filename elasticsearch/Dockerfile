FROM gradle:5.4.1-jdk12 AS builder

USER gradle

COPY --chown=gradle:gradle ingest-azurestorage /app/ingest-azurestorage/
WORKDIR /app/ingest-azurestorage
RUN gradle clean check assembleZip

COPY --chown=gradle:gradle ingest-textanalytics /app/ingest-textanalytics/
WORKDIR /app/ingest-textanalytics
RUN gradle clean check assembleZip

FROM docker.elastic.co/elasticsearch/elasticsearch-oss:7.1.1

RUN bin/elasticsearch-plugin install --batch ingest-attachment

RUN bin/elasticsearch-plugin install https://github.com/fnocera/elasticsearch-ingest-opennlp/releases/download/7.1.1.1a/ingest-opennlp-SNAPSHOT.zip \
 && bin/ingest-opennlp/download-models

COPY --from=builder /app/ingest-azurestorage/build/distributions/ingest-azurestorage-0.0.1-SNAPSHOT-deploy.zip /plugins/
RUN bin/elasticsearch-plugin install --batch file:///plugins/ingest-azurestorage-0.0.1-SNAPSHOT-deploy.zip

COPY --from=builder /app/ingest-textanalytics/build/distributions/ingest-textanalytics-0.0.1-SNAPSHOT-deploy.zip /plugins/
RUN bin/elasticsearch-plugin install --batch file:///plugins/ingest-textanalytics-0.0.1-SNAPSHOT-deploy.zip
