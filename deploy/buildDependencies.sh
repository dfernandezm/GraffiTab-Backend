#!/bin/bash

source deploy/deployEnvironment.sh
./gradlew dependencies -Penv=$ENVNAME