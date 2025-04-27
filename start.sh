#!/bin/bash

# 基础配置（可外部修改）
JAVA_HOME="/opt/jdk/jdk-11"
APP_JAR="target/matpower-web-0.0.1-SNAPSHOT.jar"
LIB_DIR="lib"
JVM_OPTS="-Xmx1G -Xms512M -XX:+UseG1GC -Dfile.encoding=UTF-8"
SPRING_OPTS="-Dserver.port=8082 -Dspring.profiles.active=prod"
MAIN_CLASS="org.springframework.boot.loader.JarLauncher"

# 检查依赖
if [ ! -d "$JAVA_HOME" ]; then
  echo "错误: JDK 未找到，请检查路径 $JAVA_HOME"
  exit 1
fi

if [ ! -f "$APP_JAR" ]; then
  echo "错误: 应用 JAR 文件未找到，请先构建项目: $APP_JAR"
  exit 1
fi

# 启动命令
"$JAVA_HOME/bin/java" \
  $JVM_OPTS \
  $SPRING_OPTS \
  -cp "$APP_JAR:$LIB_DIR/*" \
  $MAIN_CLASS
