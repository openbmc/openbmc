DESCRIPTION = "Google's framework for writing C++ tests"
HOMEPAGE = "https://github.com/google/googletest"
SECTION = "libs"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cbbd27594afd089daa160d3a16dd515a"

PV = "1.11.0+git${SRCPV}"

PROVIDES += "gmock gtest"

S = "${WORKDIR}/git"
SRCREV = "e2239ee6043f73722e7aa812a459f54a28552929"
SRC_URI = "git://github.com/google/googletest.git;branch=main;protocol=https"

inherit cmake

ALLOW_EMPTY:${PN} = "1"
ALLOW_EMPTY:${PN}-dbg = "1"

RDEPENDS:${PN}-dev += "${PN}-staticdev"

BBCLASSEXTEND = "native nativesdk"

do_configure:prepend() {
    # explicitly use python3
    # the scripts are already python3 compatible since https://github.com/google/googletest/commit/d404af0d987a9c38cafce82a7e26ec8468c88361 and other fixes like this
    # but since this oe-core change http://git.openembedded.org/openembedded-core/commit/?id=5f8f16b17f66966ae91aeabc23e97de5ecd17447
    # there isn't python in HOSTTOOLS so "env python" fails
    sed -i 's@^#!/usr/bin/env python$@#!/usr/bin/env python3@g' ${S}/googlemock/scripts/*py ${S}/googlemock/scripts/generator/*py ${S}/googlemock/scripts/generator/cpp/*py ${S}/googlemock/test/*py ${S}/googletest/scripts/*py ${S}/googletest/test/*py
}
