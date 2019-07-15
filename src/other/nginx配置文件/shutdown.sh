#!/bin/bash

SERVICE_HOME=/usr/local/Cellar/nginx/1.17.0

echo "----stop nginx program----"
PID=$( ps -ef|grep "$SERVICE_HOME" |grep nginx | awk '{print $2}')
echo $PID
if [ "$PID" ]
    then
        echo $PID
        kill $PID
fi
PID=$( ps -ef|grep "$SERVICE_HOME" | grep nginx |awk '{pring $2}' )
#[ -n "$PID" ]a && echo "nginx still exists!!!shutdown failed" && exit 254
