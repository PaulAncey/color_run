#!/bin/bash

./mvnw clean package && sudo cp -r target/color_run-1.0-SNAPSHOT.war /var/lib/tomcat10/webapps