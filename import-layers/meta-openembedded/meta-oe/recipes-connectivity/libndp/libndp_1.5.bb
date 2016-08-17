SUMMARY = "Library for IPv6 Neighbor Discovery Protocol"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "http://libndp.org/files/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "beb82e8d75d8382d1b7c0bb0f68be429"
SRC_URI[sha256sum] = "faf116ab70ce9514ec4e8573556025debea08f606e7f38b616de1f26e120c795"

inherit autotools
