LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "https://github.com/rrthomas/libpaper/releases/download/v${PV}/libpaper-${PV}.tar.gz"
SRC_URI[sha256sum] = "7be50974ce0df0c74e7587f10b04272cd53fd675cb6a1273ae1cc5c9cc9cab09"

UPSTREAM_CHECK_URI = "https://github.com/rrthomas/libpaper/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit perlnative autotools

BBCLASSEXTEND = "native"
