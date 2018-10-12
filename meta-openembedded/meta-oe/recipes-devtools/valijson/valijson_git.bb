SUMMARY = "Header-only C++ library for JSON Schema validation"
HOMEPAGE = "https://github.com/tristanpenman/valijson"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=015106c62262b2383f6c72063f0998f2"

SRC_URI = "git://github.com/tristanpenman/valijson.git"
PV = "0.1+git${SRCPV}"

SRCREV = "c2f22fddf599d04dc33fcd7ed257c698a05345d9"

S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DINSTALL_HEADERS=1 -DBUILD_TESTS=0"

# valijson is a header only C++ library, so the main package will be empty.
RDEPENDS_${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"

