#!/bin/bash

source deploy/deployEnvironment.sh
./gradlew updateDb -Penv=$ENVNAME