#!/usr/bin/env bash
echo "export PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin" >> ~/.bashrc
source ~/.bashrc
mkdir -p /tmp/zookeeper
cd /tmp/zookeeper
echo 3 > myid
cd /zookeeper/bin/
ln -sf bash /bin/sh
sh zkServer.sh start
/bin/bash