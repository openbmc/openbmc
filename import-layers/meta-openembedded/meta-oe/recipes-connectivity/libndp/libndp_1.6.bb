SUMMARY = "Library for IPv6 Neighbor Discovery Protocol"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "http://libndp.org/files/${BPN}-${PV}.tar.gz"
SRC_URI[md5sum] = "1e54d26bcb4a4110bc3f90c5dd04f1a7"
SRC_URI[sha256sum] = "0c7dfa84e013bd5e569ef2c6292a6f72cfaf14f4ff77a77425e52edc33ffac0e"

inherit autotools
