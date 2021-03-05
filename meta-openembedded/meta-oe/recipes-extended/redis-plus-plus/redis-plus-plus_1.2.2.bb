DESCRIPTION = "C++ client for Redis based on hiredis"
HOMEPAGE = "https://github.com/sewenew/redis-plus-plus"
SECTION = "libs"

DEPENDS += "hiredis"
RDEPENDS_${PN} += "hiredis"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"
SRCREV = "8ac506e6eb0e5c5b2625785b67400bde705773a1"
SRC_URI = "git://github.com/sewenew/redis-plus-plus"

S = "${WORKDIR}/git"

inherit cmake

FILES_SOLIBSDEV = ""
FILES_${PN} += " ${libdir}/libredis++.so"
