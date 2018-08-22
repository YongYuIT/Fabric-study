mkdir -p /tmp/kafka-logs
cd /usr/local/kafka/bin
ln -sf bash /bin/sh
./kafka-server-start.sh -daemon /usr/local/kafka/config/server.properties
/bin/bash
