SUMMARY = "iftop does for network usage what top(1) does for CPU usage"
HOMEPAGE = "http://www.ex-parrot.com/pdw/iftop/"
SECTION = "net"
DEPENDS = "libpcap ncurses"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=76498170798db0f4f0fb685a225f702f"

SRC_URI = "http://www.ex-parrot.com/pdw/iftop/download/iftop-${PV}.tar.gz \
           file://iftop-1.0-gcc10.patch \
          "
SRC_URI[md5sum] = "7e6decb4958e8a4890cccac335239f24"
SRC_URI[sha256sum] = "f733eeea371a7577f8fe353d86dd88d16f5b2a2e702bd96f5ffb2c197d9b4f97"

inherit autotools-brokensep

