#!/bin/sh
# wait-for-it.sh

set -e

host="$1"
port="$2"
shift 2
cmd="$@"

until curl -f "http://$host:$port/actuator/health"; do
  echo "Waiting for $host:$port to be available..."
  sleep 5
done

echo "$host:$port is available! Executing command..."
exec $cmd