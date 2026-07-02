LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "https://github.com/rrthomas/libpaper/releases/download/v${PV}/libpaper-${PV}.tar.gz"
SRC_URI[sha256sum] = "1e330571690191874eca415ec76889dd11bab9887a2302d6a3665cd081c4d77b"

UPSTREAM_CHECK_URI = "https://github.com/rrthomas/libpaper/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit perlnative autotools

BBCLASSEXTEND = "native"
