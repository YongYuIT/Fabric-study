#!/usr/bin/env bash
echo "PATH=$PATH:$JAVA_HOME/bin:$JAVA_HOME/jre/bin" >> ~/.bashrc
source ~/.bashrc
mkdir -p /tmp/zookeeper
cd /tmp/zookeeper
echo 2 > myid
cd /zookeeper/bin/
ln -sf bash /bin/sh
sh zkServer.sh start
/bin/bash