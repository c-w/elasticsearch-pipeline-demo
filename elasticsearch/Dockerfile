FROM registry.centos.org/rhsyseng/elasticsearch:6.2.1

USER root

RUN curl -O https://artifacts.elastic.co/downloads/elasticsearch-plugins/ingest-attachment/ingest-attachment-6.2.1.zip 
RUN mv ingest-attachment-6.2.1.zip /usr/share/elasticsearch/bin/
RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install file:///usr/share/elasticsearch/bin/ingest-attachment-6.2.1.zip

USER elasticsearch


