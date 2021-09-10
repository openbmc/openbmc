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

ENTRYPOINT ["/opt/bbhashserv/bin/bitbake-hashserv"]
