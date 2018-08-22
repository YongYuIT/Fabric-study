mkdir -p /tmp/zookeeper
cd /tmp/zookeeper
echo 1 > myid
cd /home/zookeeper-3.4.12/bin/
ln -sf bash /bin/sh
sh zkServer.sh start
/bin/bash
