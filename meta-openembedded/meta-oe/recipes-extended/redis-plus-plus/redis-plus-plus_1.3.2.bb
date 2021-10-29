DESCRIPTION = "C++ client for Redis based on hiredis"
HOMEPAGE = "https://github.com/sewenew/redis-plus-plus"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/sewenew/redis-plus-plus"
SRCREV = "e29c63db54653a660d7a0f556f670b7a6fce0a78"

S = "${WORKDIR}/git"

inherit cmake

DEPENDS += "hiredis"

RDEPENDS:${PN} += "hiredis"

FILES_SOLIBSDEV = ""
FILES:${PN} += " ${libdir}/libredis++.so*"

INSANE_SKIP:${PN} += "dev-so"
