SUMMARY = "OpenSource WebSocket Toolkit"
DESCRIPTION = "noPoll is a OpenSource WebSocket implementation (RFC 6455), \
written in ansi C, that allows building pure WebSocket solutions or to \
provide WebSocket support to existing TCP oriented applications.\
\
noPoll provides support for WebSocket (ws://) and TLS (secure) WebSocket (wss://),\
allowing message based (handler notified) programming or stream oriented access."

HOMEPAGE = "http://www.aspl.es/nopoll/"
LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=f0504124678c1b3158146e0630229298 \
                   "
DEPENDS = "openssl"
SRC_URI = "http://www.aspl.es/nopoll/downloads/nopoll-${PV}.tar.gz \
          "
SRC_URI[md5sum] = "8d333f158b5d5a8975a6149e6ef8db63"
SRC_URI[sha256sum] = "7f1b20f1d0525f30cdd2a4fc386d328b4cf98c6d11cef51fe62cd9491ba19ad9"

inherit autotools pkgconfig

EXTRA_OECONF += "--disable-nopoll-doc"

LDFLAGS += "-lpthread"
