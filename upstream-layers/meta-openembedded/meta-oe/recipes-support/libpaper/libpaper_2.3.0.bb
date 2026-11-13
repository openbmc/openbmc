LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "https://github.com/rrthomas/libpaper/releases/download/v${PV}/libpaper-${PV}.tar.gz"
SRC_URI[sha256sum] = "882b1c7636052fc9a318caa20292b35616b588824b70e7053018262b29b1409a"

UPSTREAM_CHECK_URI = "https://github.com/rrthomas/libpaper/releases"
UPSTREAM_CHECK_REGEX = "releases/tag/v(?P<pver>\d+(\.\d+)+)"

inherit perlnative autotools

BBCLASSEXTEND = "native"
