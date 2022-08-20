ARG ELASTIC_VERSION

# https://www.docker.elastic.co/
FROM docker.elastic.co/logstash/logstash:${ELASTIC_VERSION}

# Add your logstash plugins setup here
# Example: RUN logstash-plugin install logstash-filter-json
RUN cd /usr/share
RUN mkdir wiber-back-log
RUN mkdir socket-service-log
RUN mkdir sms-service-log