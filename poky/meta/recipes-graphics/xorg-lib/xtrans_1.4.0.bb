SUMMARY = "XTrans: X Transport library"

DESCRIPTION = "The X Transport Interface is intended to combine all \
system and transport specific code into a single place.  This API should \
be used by all libraries, clients and servers of the X Window System. \
Use of this API should allow the addition of new types of transports and \
support for new platforms without making any changes to the source \
except in the X Transport Interface code."

require xorg-lib-common.inc

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=49347921d4d5268021a999f250edc9ca"

SRC_URI += "file://multilibfix.patch"

PE = "1"

RDEPENDS_${PN}-dev = ""

inherit gettext

BBCLASSEXTEND = "native nativesdk"

SRC_URI[md5sum] = "ce2fb8100c6647ee81451ebe388b17ad"
SRC_URI[sha256sum] = "377c4491593c417946efcd2c7600d1e62639f7a8bbca391887e2c4679807d773"
