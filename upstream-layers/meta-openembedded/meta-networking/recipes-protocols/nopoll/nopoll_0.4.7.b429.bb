SUMMARY = "OpenSource WebSocket Toolkit"
DESCRIPTION = "noPoll is a OpenSource WebSocket implementation (RFC 6455), \
written in ansi C, that allows building pure WebSocket solutions or to \
provide WebSocket support to existing TCP oriented applications.\
\
noPoll provides support for WebSocket (ws://) and TLS (secure) WebSocket (wss://),\
allowing message based (handler notified) programming or stream oriented access."

HOMEPAGE = "http://www.aspl.es/nopoll/"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=f0504124678c1b3158146e0630229298 \
                   "
DEPENDS = "openssl"
SRC_URI = "http://www.aspl.es/nopoll/downloads/nopoll-${PV}.tar.gz \
          "
SRC_URI[sha256sum] = "d5c020fec25e3fa486c308249833d06bed0d76bde9a72fd5d73cfb057c320366"

inherit autotools pkgconfig

EXTRA_OECONF += "--disable-nopoll-doc"

LDFLAGS += "-lpthread"
