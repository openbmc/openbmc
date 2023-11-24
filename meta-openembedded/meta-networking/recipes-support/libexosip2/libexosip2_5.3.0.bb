SUMMARY = "Extend  the  capability  of the oSIP library"
DESCRIPTION = "eXosip is a GPL library that  extend  the  capability  of \
the oSIP library. It aims  to  implement  a  simple  high \
layer API to control SIP (rfc3261) for sessions establishements \
and common extensions."
HOMEPAGE = "http://savannah.gnu.org/projects/exosip"
SECTION = "libs"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://src/eXosip.c;beginline=1;endline=32;md5=db3c08b79afd8f4d5e5dc8f0a5dc687e"
DEPENDS = "libosip2"

SRC_URI = "${SAVANNAH_NONGNU_MIRROR}/exosip/${BPN}-${PV}.tar.gz"
SRC_URI[sha256sum] = "5b7823986431ea5cedc9f095d6964ace966f093b2ae7d0b08404788bfcebc9c2"

inherit autotools pkgconfig
