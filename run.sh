#!/bin/sh

APP=NarwhalClient-1.0.2-SNAPSHOT

if [ ! -f target/${APP}-jar-with-dependencies.jar ]; then
	/bin/echo -n Building ...
	mvn clean install assembly:single -Dmaven.test.skip > /dev/null
	if [ $? -ne 0 ]; then
	    echo failed.
	    exit 1
	fi
	echo done.
fi

java -jar target/${APP}-jar-with-dependencies.jar $1

