#!/usr/bin/env bash

# wait-for-it.sh: Wait until a host:port is available
set -e

host="$1"
shift
port="$1"
shift
cmd="$@"

echo "⏳ Waiting for $host:$port..."
while ! nc -z "$host" "$port"; do
  sleep 1
done

echo "✅ $host:$port is available — executing: $cmd"
exec $cmd
