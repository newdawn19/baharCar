#!/bin/sh
rm -f tpid

JAVA_OPTS="-Dfile.encoding=UTF-8 -Xmx2048m -Xms2048m -Xss256k -Xmn1024m"
# 外部配置文件位置：默认 ./config，可用环境变量覆盖；profile 默认 dev
JAVA_OPTS="$JAVA_OPTS -Denv.properties.path=${BAHAR_CONFIG_PATH:-config} -Denv.profile=${BAHAR_PROFILE:-dev}"
java $JAVA_OPTS -jar "$1" &

echo $! > tpid

echo Start Success!
