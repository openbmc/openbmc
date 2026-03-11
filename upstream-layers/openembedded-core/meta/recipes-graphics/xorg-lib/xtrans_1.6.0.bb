SUMMARY = "XTrans: X Transport library"

DESCRIPTION = "The X Transport Interface is intended to combine all \
system and transport specific code into a single place.  This API should \
be used by all libraries, clients and servers of the X Window System. \
Use of this API should allow the addition of new types of transports and \
support for new platforms without making any changes to the source \
except in the X Transport Interface code."

require xorg-lib-common.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d6091702432f176651d4bf09e61bbe2d"

SRC_URI += "file://multilibfix.patch"

PE = "1"

DEV_PKG_DEPENDENCY = ""

inherit gettext

BBCLASSEXTEND = "native nativesdk"

SRC_URI[sha256sum] = "faafea166bf2451a173d9d593352940ec6404145c5d1da5c213423ce4d359e92"
