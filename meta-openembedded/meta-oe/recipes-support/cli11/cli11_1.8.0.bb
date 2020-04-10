SUMMARY = "C++11 command line parser"
DESCRIPTION = "A command line parser for C++11 and beyond that provides a rich feature set with a simple and intuitive interface."
HOMEPAGE = "https://github.com/CLIUtils/CLI11"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b73927b18d5c6cd8d2ed28a6ad539733"
SRCREV = "13becaddb657eacd090537719a669d66d393b8b2"
PV .= "+git${SRCPV}"

SRC_URI += "gitsm://github.com/CLIUtils/CLI11 \
            file://0001-Add-CLANG_TIDY-check.patch \
            file://0001-Use-GNUInstallDirs-instead-of-hard-coded-path.patch \
           "

S = "${WORKDIR}/git"

inherit cmake
inherit ptest

EXTRA_OECMAKE += "-DCLANG_TIDY=OFF"
DEPENDS += "boost"

# cli11 is a header only C++ library, so the main package will be empty.
RDEPENDS_${PN}-dev = ""
