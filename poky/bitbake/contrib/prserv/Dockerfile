# SPDX-License-Identifier: MIT
#
# Copyright (c) 2022 Daniel Gomez <daniel@qtec.com>
#
# Dockerfile to build a bitbake PR service container
#
# From the root of the bitbake repository, run:
#
#   docker build -f contrib/prserv/Dockerfile . -t prserv
#
# Running examples:
#
# 1. PR Service in RW mode, port 18585:
#
# docker run --detach --tty \
# --env PORT=18585 \
# --publish 18585:18585 \
# --volume $PWD:/var/lib/bbprserv \
# prserv
#
# 2. PR Service in RO mode, default port (8585) and custom LOGFILE:
#
# docker run --detach --tty \
# --env DBMODE="--read-only" \
# --env LOGFILE=/var/lib/bbprserv/prservro.log \
# --publish 8585:8585 \
# --volume $PWD:/var/lib/bbprserv \
# prserv
#

FROM alpine:3.14.4

RUN apk add --no-cache python3

COPY bin/bitbake-prserv /opt/bbprserv/bin/
COPY lib/prserv /opt/bbprserv/lib/prserv/
COPY lib/bb /opt/bbprserv/lib/bb/
COPY lib/codegen.py /opt/bbprserv/lib/codegen.py
COPY lib/ply /opt/bbprserv/lib/ply/
COPY lib/bs4 /opt/bbprserv/lib/bs4/

ENV PATH=$PATH:/opt/bbprserv/bin

RUN mkdir -p /var/lib/bbprserv

ENV DBFILE=/var/lib/bbprserv/prserv.sqlite3 \
    LOGFILE=/var/lib/bbprserv/prserv.log \
    LOGLEVEL=debug \
    HOST=0.0.0.0 \
    PORT=8585 \
    DBMODE=""

ENTRYPOINT [ "/bin/sh", "-c", \
"bitbake-prserv \
--file=$DBFILE \
--log=$LOGFILE \
--loglevel=$LOGLEVEL \
--start \
--host=$HOST \
--port=$PORT \
$DBMODE \
&& tail -f $LOGFILE"]
