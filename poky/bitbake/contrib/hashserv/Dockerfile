# SPDX-License-Identifier: MIT
#
# Copyright (c) 2021 Joshua Watt <JPEWhacker@gmail.com>
#
# Dockerfile to build a bitbake hash equivalence server container
#
# From the root of the bitbake repository, run:
#
#   docker build -f contrib/hashserv/Dockerfile .
#

FROM alpine:3.13.1

RUN apk add --no-cache python3

COPY bin/bitbake-hashserv /opt/bbhashserv/bin/
COPY lib/hashserv /opt/bbhashserv/lib/hashserv/
COPY lib/bb /opt/bbhashserv/lib/bb/
COPY lib/codegen.py /opt/bbhashserv/lib/codegen.py
COPY lib/ply /opt/bbhashserv/lib/ply/
COPY lib/bs4 /opt/bbhashserv/lib/bs4/

ENTRYPOINT ["/opt/bbhashserv/bin/bitbake-hashserv"]
