DESCRIPTION = "Google's framework for writing C++ tests"
HOMEPAGE = "https://github.com/google/googletest"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://googlemock/LICENSE;md5=cbbd27594afd089daa160d3a16dd515a \
                    file://googletest/LICENSE;md5=cbbd27594afd089daa160d3a16dd515a"

PROVIDES += "gmock"

SRC_URI = "\
    https://github.com/google/googletest/archive/release-${PV}.tar.gz \
    file://Add-pkg-config-support.patch \
"

SRC_URI[md5sum] = "16877098823401d1bf2ed7891d7dce36"
SRC_URI[sha256sum] = "58a6f4277ca2bc8565222b3bbd58a177609e9c488e8a72649359ba51450db7d8"

S = "${WORKDIR}/googletest-release-${PV}"

inherit cmake

ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-dbg = "1"

RDEPENDS_${PN}-dev += "${PN}-staticdev"

BBCLASSEXTEND = "native nativesdk"
