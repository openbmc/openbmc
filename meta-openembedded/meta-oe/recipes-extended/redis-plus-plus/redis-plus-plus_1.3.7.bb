DESCRIPTION = "C++ client for Redis based on hiredis"
HOMEPAGE = "https://github.com/sewenew/redis-plus-plus"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/sewenew/redis-plus-plus;branch=master;protocol=https \
           file://0001-include-cstdint.patch"
SRCREV = "f3b19a8a1f609d1a1b79002802e5cf8c336dc262"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE += "-DREDIS_PLUS_PLUS_USE_TLS=ON"

DEPENDS += "hiredis openssl"

RDEPENDS:${PN} += "hiredis"

FILES_SOLIBSDEV = ""
FILES:${PN} += " ${libdir}/libredis++.so*"

INSANE_SKIP:${PN} += "dev-so"
