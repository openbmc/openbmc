DESCRIPTION = "C++ client for Redis based on hiredis"
HOMEPAGE = "https://github.com/sewenew/redis-plus-plus"
SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRC_URI = "git://github.com/sewenew/redis-plus-plus;branch=master;protocol=https \
           file://0001-cmake-Use-CMAKE_INSTALL_LIBDIR-from-GNUInstallDirs.patch \
          "
SRCREV = "389ffdf9e72035ea2096b03cda7f4a6809ae6363"

S = "${WORKDIR}/git"

inherit cmake

DEPENDS += "hiredis"

RDEPENDS:${PN} += "hiredis"

FILES_SOLIBSDEV = ""
FILES:${PN} += " ${libdir}/libredis++.so*"

INSANE_SKIP:${PN} += "dev-so"
