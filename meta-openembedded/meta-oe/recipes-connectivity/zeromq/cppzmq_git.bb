DESCRIPTION = "C++ bindings for ZeroMQ"
HOMEPAGE = "http://www.zeromq.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=db174eaf7b55a34a7c89551197f66e94"
DEPENDS = "zeromq"

SRCREV = "e67fa0e16d7964876228952f60693f5c03ea0cc1"
PV = "4.5.0"

SRC_URI = "git://github.com/zeromq/cppzmq.git"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DCPPZMQ_BUILD_TESTS=OFF"

PACKAGES = "${PN}-dev"

RDEPENDS_${PN}-dev = "zeromq-dev"
