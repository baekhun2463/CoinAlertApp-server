#!/bin/bash
PROJECT_NAME="CoinAlertApp-server"
JAR_PATH="/home/ubuntu/github_action/build/libs/*.jar"
DEPLOY_PATH=/home/ubuntu/$PROJECT_NAME
DEPLOY_LOG_PATH="/home/ubuntu/$PROJECT_NAME/deploy.log"
DEPLOY_ERR_LOG_PATH="/home/ubuntu/$PROJECT_NAME/deploy_err.log"
APPLICATION_LOG_PATH="/home/ubuntu/$PROJECT_NAME/application.log"
BUILD_JAR=$(ls $JAR_PATH)
JAR_NAME=$(basename $BUILD_JAR)

if [ ! -d $DEPLOY_PATH ]; then
  echo "> $DEPLOY_PATH 폴더가 없으므로 생성합니다." >> $DEPLOY_LOG_PATH
  mkdir -p $DEPLOY_PATH
fi

echo "==== 배포 시작 : $(date +%c) ====" >> $DEPLOY_LOG_PATH


echo ">build 파일명 : $JAR_NAME" >> $DEPLOY_LOG_PATH
echo "> build 파일 복사" >> $DEPLOY_LOG_PATH
cp $BUILD_JAR $DEPLOY_PATH

echo "> 현재 동작중인 어플리케이션 pid 체크" >> $DEPLOY_LOG_PATH
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 동작중인 어플리케이션 존재 X" >> $DEPLOY_LOG_PATH
else
  echo "> 현재 동작중인 어플리케이션 존재 O" >> $DEPLOY_LOG_PATH
  echo "> 현재 동작중인 어플리케이션 강제 종료 진행" >> $DEPLOY_LOG_PATH
  echo "> kill -9 $CURRENT_PID" >> $DEPLOY_LOG_PATH
  kill -9 $CURRENT_PID
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포" >> $DEPLOY_LOG_PATH
nohup java -jar -Dspring.profiles.active=local $DEPLOY_JAR --server.port=8081 >> $APPLICATION_LOG_PATH 2> $DEPLOY_ERR_LOG_PATH &

sleep 3
echo "> 배포 종료 : $(date +%c)" >> $DEPLOY_LOG_PATH

