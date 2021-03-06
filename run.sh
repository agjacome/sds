#!/bin/bash

export SDS_PORT=9000
export SDS_PATH="/sds"

export SDS_DATA_DIR="$(pwd)/share"

export SDS_SLICK_DRIVER="slick.driver.MySQLDriver$"
export SDS_JDBC_DRIVER="org.mariadb.jdbc.Driver"
export SDS_JDBC_URL="jdbc:mysql://localhost:3306/sds?user=sds_user&password=sds_pass"

sbt stage && ./target/universal/stage/bin/sds -J-Xmx4096M -J-Xms4096M -J-Xss20M -J-server -J-XX:+UseConcMarkSweepGC -J-XX:+CMSClassUnloadingEnabled -DapplyEvolutions.default=true
