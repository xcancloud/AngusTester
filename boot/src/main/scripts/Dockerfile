#
# Copyright (c) 2021   XCan Company
#
#        http://www.xcan.company
#
# Licensed under the XCBL(XCan Business License) Version 1.0.
# Detail XCBL license at:
#
# http://www.xcan.company/licenses/XCBL-1.0
#

FROM openjdk:11.0.1-jre-slim-stretch

MAINTAINER http://www.xcan.company

WORKDIR /

RUN mkdir logs && touch logs/gc.log

ARG JAR_FILE
ADD ${JAR_FILE} ${JAR_FILE}
ENV JAR_FILE=$JAR_FILE

COPY startenv.sh /startenv.sh
RUN chmod +x /startenv.sh
RUN sh /startenv.sh

EXPOSE 1830

ENTRYPOINT java $JAVA_OPTS -jar $JAR_FILE