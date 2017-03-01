DESCRIPTION = "Google's framework for writing C++ tests"
HOMEPAGE = "https://github.com/google/googletest"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://googletest/LICENSE;md5=cbbd27594afd089daa160d3a16dd515a"

SRC_URI = "\
    git://github.com/google/googletest.git;tag=release-1.8.0 \
    file://0001-Add-pkg-config-support.patch \
"

S = "${WORKDIR}/git"

inherit lib_package cmake

ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-dbg = "1"

RDEPENDS_${PN}-dev += "${PN}-staticdev"

BBCLASSEXTEND = "native nativesdk"
