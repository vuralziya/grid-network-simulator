#!/bin/bash

set -x
set -m

/entrypoint.sh couchbase-server &

sleep 30

# Setup index and memory quota
curl -v -X POST http://grid-network-simulator_db_1:8091/pools/default -d memoryQuota=300

# Setup services
curl -v http://grid-network-simulator_db_1:8091/node/controller/setupServices -d services=kv

# Setup credentials
curl -v http://grid-network-simulator_db_1:8091/settings/web -d port=8091 -d username=Administrator -d password=password

curl -v -u Administrator:password -X POST http://grid-network-simulator_db_1:8091/pools/default/buckets -d name=SolarGrid -d ramQuotaMB=256

echo "Type: $TYPE"

if [ "$TYPE" = "WORKER" ]; then
  echo "Sleeping ..."
  sleep 30

  #IP=`hostname -s`
  IP=`hostname -I | cut -d ' ' -f1`
  echo "IP: " $IP

  echo "Auto Rebalance: $AUTO_REBALANCE"
  if [ "$AUTO_REBALANCE" = "true" ]; then
    couchbase-cli rebalance --cluster=$COUCHBASE_MASTER:8091 --user=Administrator --password=password --server-add=$IP --server-add-username=Administrator --server-add-password=password
  else
    couchbase-cli server-add --cluster=$COUCHBASE_MASTER:8091 --user=Administrator --password=password --server-add=$IP --server-add-username=Administrator --server-add-password=password
  fi;
fi;

fg 1