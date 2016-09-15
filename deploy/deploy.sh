#!/bin/bash

source deploy/deployEnvironment.sh

# Default to Dev

DO_USER=$DO_DEV_USER
DO_SRV01_DOMAIN=$DO_DEV01_DOMAIN
DO_SRV02_DOMAIN=$DO_DEV02_DOMAIN
DO_DEPLOYMENT_DIR=$DEV_DEPLOYMENT_DIR

if [ $ENVIRON == $PRD_ENV_NAME ]
then
 DO_USER=$DO_PRD_USER
 DO_SRV01_DOMAIN=$DO_PRD01_DOMAIN
 DO_SRV02_DOMAIN=$DO_PRD02_DOMAIN
 DO_DEPLOYMENT_DIR=$PRD_DEPLOYMENT_DIR
fi

if [ $ENVIRON == $DEV_ENV_NAME ]
then
 DO_USER=$DO_DEV_USER
 DO_SRV01_DOMAIN=$DO_DEV01_DOMAIN
 DO_SRV02_DOMAIN=$DO_DEV02_DOMAIN
 DO_DEPLOYMENT_DIR=$DEV_DEPLOYMENT_DIR
fi

echo "Generating JAR archive to deploy, env is $ENVNAME"
./gradlew stage -Penv=$ENVNAME

echo "Properties:"
echo "DO_USER = $DO_USER"
echo "DO_SRV01_DOMAIN = $DO_SRV01_DOMAIN"
echo "DO_SRV02_DOMAIN = $DO_SRV02_DOMAIN"
echo "DEPLOYMENT_DIR = $DO_DEPLOYMENT_DIR"

echo "==== Deploying to Digital Ocean - ${ENVIRON}01 ===="
scp graffitab.jar $DO_USER@$DO_SRV01_DOMAIN:$DO_DEPLOYMENT_DIR
scp digitalOcean/start.sh $DO_USER@$DO_SRV01_DOMAIN:$DO_DEPLOYMENT_DIR
scp digitalOcean/stop.sh $DO_USER@$DO_SRV01_DOMAIN:$DO_DEPLOYMENT_DIR
scp deploy/pollForStartup.sh $DO_USER@$DO_SRV01_DOMAIN:$DO_DEPLOYMENT_DIR

echo "==== Stopping application - ${ENVIRON}01 ===="
ssh $DO_USER@$DO_SRV01_DOMAIN 'source ~/environment.sh && chmod +x $DO_DEPLOYMENT_DIR/stop.sh' 2>&1
ssh $DO_USER@$DO_SRV01_DOMAIN 'source ~/environment.sh && bash $DO_DEPLOYMENT_DIR/stop.sh' 2>&1
ssh $DO_USER@$DO_SRV01_DOMAIN 'source ~/environment.sh && chmod +x $DO_DEPLOYMENT_DIR/start.sh' 2>&1

echo "==== Starting application in ${ENVIRON}01 after deployment ===="
# We run the start script as sudo because Tomcat is bound to port 80 on the machine, a privileged port. This can only be done by root.
# We could have changed the port to another one and modify the load balancer configuration accordingly, but this is fine
# for now
ssh $DO_USER@$DO_SRV01_DOMAIN 'source ~/environment.sh && cd $DO_DEPLOYMENT_DIR && sudo nohup ./start.sh > start.log &' 2>&1

echo "= Waiting for startup in ${ENVIRON}01 ="
ssh $DO_USER@$DO_SRV01_DOMAIN 'source ~/environment.sh && chmod +x $DO_DEPLOYMENT_DIR/pollForStartup.sh && bash $DO_DEPLOYMENT_DIR/pollForStartup.sh' 2>&1

echo "==== Deploying to Digital Ocean - ${ENVIRON}02 ===="
scp graffitab.jar $DO_USER@$DO_SRV02_DOMAIN:$DO_DEPLOYMENT_DIR
scp digitalOcean/start.sh $DO_USER@$DO_SRV02_DOMAIN:$DO_DEPLOYMENT_DIR
scp digitalOcean/stop.sh $DO_USER@$DO_SRV02_DOMAIN:$DO_DEPLOYMENT_DIR
scp deploy/pollForStartup.sh $DO_USER@$DO_SRV02_DOMAIN:$DO_DEPLOYMENT_DIR

echo "==== Stopping application - ${ENVIRON}02 ===="
ssh $DO_USER@$DO_SRV02_DOMAIN 'source ~/environment.sh && bash $DO_DEPLOYMENT_DIR/stop.sh' 2>&1
ssh $DO_USER@$DO_SRV02_DOMAIN 'source ~/environment.sh && chmod +x $DO_DEPLOYMENT_DIR/start.sh' 2>&1

echo "==== Starting application in ${ENVIRON}02 after deployment ===="
# We run the start script as sudo because Tomcat is bound to port 80 on the machine, a privileged port. This can only be done by root.
# We could have changed the port to another one and modify the load balancer configuration accordingly, but this is fine
# for now
ssh $DO_USER@$DO_SRV02_DOMAIN 'source ~/environment.sh && cd $DO_DEPLOYMENT_DIR && sudo nohup ./start.sh > start.log &' 2>&1

echo "= Waiting for startup in in ${ENVIRON}02 ="
ssh $DO_USER@$DO_SRV02_DOMAIN 'source ~/environment.sh && chmod +x $DO_DEPLOYMENT_DIR/pollForStartup.sh && bash $DO_DEPLOYMENT_DIR/pollForStartup.sh' 2>&1

echo "Application deployed and started successfully in both app servers: SUCCESS"