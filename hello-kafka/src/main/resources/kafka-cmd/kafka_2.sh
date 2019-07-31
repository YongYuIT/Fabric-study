#!/usr/bin/env bash
echo "PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin" >> ~/.bashrc
source ~/.bashrc
mkdir -p /tmp/kafka-logs
cd /kafka/bin
ln -sf bash /bin/sh
rm -rf /kafka/config/server.properties
./kafka-server-start.sh -daemon /kafka-cmd/server_2.properties
/bin/bash
