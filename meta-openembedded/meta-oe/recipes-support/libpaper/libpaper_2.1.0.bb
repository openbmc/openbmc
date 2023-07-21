LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "https://github.com/rrthomas/libpaper/releases/download/v${PV}/libpaper-${PV}.tar.gz"
SRC_URI[sha256sum] = "474e9575e1235a0d8e3661f072de0193bab6ea1023363772f698a2cc39d640cf"

inherit perlnative autotools

BBCLASSEXTEND = "native"
