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
SRC_URI[md5sum] = "8c411cd0f3c0479aed28c4cf7b114fbb"
SRC_URI[sha256sum] = "f5fbf8aaa16a77b0f265d8c847eb06cb3e68f2b1a50737466dae81181618654c"

inherit autotools pkgconfig

EXTRA_OECONF += "--disable-nopoll-doc"

LDFLAGS += "-lpthread"
