DESCRIPTION = "C++ bindings for ZeroMQ"
HOMEPAGE = "http://www.zeromq.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=db174eaf7b55a34a7c89551197f66e94"
DEPENDS = "zeromq"

SRCREV = "267d300d1c99381a0fbc7e060ae2899e51f5e425"
PV = "4.8.0"

SRC_URI = "git://github.com/zeromq/cppzmq.git;branch=master"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DCPPZMQ_BUILD_TESTS=OFF"

PACKAGES = "${PN}-dev"

RDEPENDS:${PN}-dev = "zeromq-dev"
