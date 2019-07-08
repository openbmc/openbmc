SUMMARY = "C++11 command line parser"
DESCRIPTION = "A command line parser for C++11 and beyond that provides a rich feature set with a simple and intuitive interface."
HOMEPAGE = "https://github.com/CLIUtils/CLI11"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c271fee3ae28e11b24b97284d9f82887"
SRCREV = "bd4dc911847d0cde7a6b41dfa626a85aab213baf"
PV .= "+git${SRCPV}"

SRC_URI += "gitsm://github.com/CLIUtils/CLI11 \
            file://0001-Add-CLANG_TIDY-check.patch \
           "

S = "${WORKDIR}/git"

inherit cmake
inherit ptest

EXTRA_OECMAKE += "-DCLANG_TIDY=OFF"
DEPENDS += "boost"
