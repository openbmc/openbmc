DESCRIPTION = "C++ bindings for ZeroMQ"
HOMEPAGE = "http://www.zeromq.org"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=db174eaf7b55a34a7c89551197f66e94"
DEPENDS = "zeromq"

SRCREV = "6aa3ab686e916cb0e62df7fa7d12e0b13ae9fae6"
PV = "4.2.3+git${SRCPV}"

SRC_URI = "git://github.com/zeromq/cppzmq.git"

S = "${WORKDIR}/git"

inherit cmake

PACKAGES = "${PN}-dev"

RDEPENDS_${PN}-dev = "zeromq-dev"
