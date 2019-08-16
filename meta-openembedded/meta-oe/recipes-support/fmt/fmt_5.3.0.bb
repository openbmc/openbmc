SUMMARY = "open-source formatting library for C++"
DESCRIPTION = "{fmt} is an open-source formatting library for C++. It can be used as a safe and fast alternative to (s)printf and iostreams."
HOMEPAGE = "https://fmt.dev"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=c2e38bc8629eac247a73b65c1548b2f0"
SRCREV = "9e554999ce02cf86fcdfe74fe740c4fe3f5a56d5"
PV .= "+git${SRCPV}"

SRC_URI += "git://github.com/fmtlib/fmt"

S = "${WORKDIR}/git"

inherit cmake
inherit ptest

EXTRA_OECMAKE += "-DBUILD_SHARED_LIBS=ON"
