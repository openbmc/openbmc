LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "https://github.com/rrthomas/libpaper/releases/download/v${PV}/libpaper-${PV}.tar.gz"
SRC_URI[sha256sum] = "1fda0cf64efa46b9684a4ccc17df4386c4cc83254805419222c064bf62ea001f"

inherit perlnative autotools

BBCLASSEXTEND = "native"
