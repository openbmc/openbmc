DESCRIPTION = "Google's framework for writing C++ tests"
HOMEPAGE = "http://code.google.com/p/googletest/"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cbbd27594afd089daa160d3a16dd515a"

SRC_URI = "\
    http://googletest.googlecode.com/files/${BPN}-${PV}.zip \
    file://0001-Add-install-command-for-libraries-and-headers.patch \
    file://0002-CMakeLists-gtest.pc.in-Add-pkg-config-support-to-gte.patch \
"

SRC_URI[md5sum] = "2d6ec8ccdf5c46b05ba54a9fd1d130d7"
SRC_URI[sha256sum] = "247ca18dd83f53deb1328be17e4b1be31514cedfc1e3424f672bf11fd7e0d60d"

inherit lib_package cmake

ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-dbg = "1"

RDEPENDS_${PN}-dev += "${PN}-staticdev"

BBCLASSEXTEND = "native nativesdk"
