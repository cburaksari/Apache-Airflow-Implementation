FROM apache/airflow:latest

USER root

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        openjdk-17-jdk-headless && \
    rm -rf /var/lib/apt/lists/*

USER airflow

ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64

COPY --chown=airflow:airflow ./dags /opt/airflow/dags
COPY --chown=airflow:airflow ./target /opt/airflow/target
