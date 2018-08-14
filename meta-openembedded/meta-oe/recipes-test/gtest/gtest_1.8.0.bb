DESCRIPTION = "Google's framework for writing C++ tests"
HOMEPAGE = "https://github.com/google/googletest"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://googlemock/LICENSE;md5=cbbd27594afd089daa160d3a16dd515a \
                    file://googletest/LICENSE;md5=cbbd27594afd089daa160d3a16dd515a"

PROVIDES += "gmock"

S = "${WORKDIR}/git"
SRCREV = "ec44c6c1675c25b9827aacd08c02433cccde7780"
SRC_URI = "\
    git://github.com/google/googletest.git;protocol=https; \
    file://Add-pkg-config-support.patch \
"

inherit cmake

ALLOW_EMPTY_${PN} = "1"
ALLOW_EMPTY_${PN}-dbg = "1"

RDEPENDS_${PN}-dev += "${PN}-staticdev"

BBCLASSEXTEND = "native nativesdk"
