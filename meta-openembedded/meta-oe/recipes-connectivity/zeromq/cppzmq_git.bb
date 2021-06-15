DESCRIPTION = "C++ bindings for ZeroMQ"
HOMEPAGE = "http://www.zeromq.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=db174eaf7b55a34a7c89551197f66e94"
DEPENDS = "zeromq"

SRCREV = "76bf169fd67b8e99c1b0e6490029d9cd5ef97666"
PV = "4.7.1"

SRC_URI = "git://github.com/zeromq/cppzmq.git;branch=bugfix-4-7-1"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DCPPZMQ_BUILD_TESTS=OFF"

PACKAGES = "${PN}-dev"

RDEPENDS_${PN}-dev = "zeromq-dev"
