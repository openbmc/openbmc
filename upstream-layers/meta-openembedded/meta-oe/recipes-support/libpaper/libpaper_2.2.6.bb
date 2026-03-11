LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "https://github.com/rrthomas/libpaper/releases/download/v${PV}/libpaper-${PV}.tar.gz"
SRC_URI[sha256sum] = "500d39dc58768ee09688738c8b5bfe07640ba2fd6c25a6dc78810eb69c719e93"

UPSTREAM_CHECK_URI = "https://github.com/rrthomas/libpaper/releases"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

inherit perlnative autotools

BBCLASSEXTEND = "native"
