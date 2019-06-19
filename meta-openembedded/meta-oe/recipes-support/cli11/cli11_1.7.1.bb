SUMMARY = "C++11 command line parser"
DESCRIPTION = "A command line parser for C++11 and beyond that provides a rich feature set with a simple and intuitive interface."
HOMEPAGE = "https://github.com/CLIUtils/CLI11"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8489f3831fc7b75264c1d5e346251a74"
SRCREV = "49ac989a9527ee9bb496de9ded7b4872c2e0e5ca"
PV .= "+git${SRCPV}"

SRC_URI += "gitsm://github.com/CLIUtils/CLI11 \
            file://0001-Add-CLANG_TIDY-check.patch \
           "

S = "${WORKDIR}/git"

inherit cmake
inherit ptest

EXTRA_OECMAKE += "-DCLANG_TIDY=OFF"
DEPENDS += "boost"
