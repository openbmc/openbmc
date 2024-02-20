SUMMARY = "C++11 command line parser"
DESCRIPTION = "A command line parser for C++11 and beyond that provides a rich feature set with a simple and intuitive interface."
HOMEPAGE = "https://github.com/CLIUtils/CLI11"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=afb20fe5f67e21074b10b864d9563c11"
SRCREV = "291c58789c031208f08f4f261a858b5b7083e8e2"
PV .= "+git"

SRC_URI += "gitsm://github.com/CLIUtils/CLI11;branch=main;protocol=https \
            https://github.com/philsquared/Catch/releases/download/v2.13.7/catch.hpp \
            file://0001-Do-not-download-the-catch-framework-during-configure.patch"

SRC_URI[sha256sum] = "ea379c4a3cb5799027b1eb451163dff065a3d641aaba23bf4e24ee6b536bd9bc"
S = "${WORKDIR}/git"

do_configure:prepend() {
    mkdir -p ${S}/tests/catch2
    cp ${DL_DIR}/catch.hpp ${S}/tests/catch2/catch.hpp
}

inherit cmake

# cli11 is a header only C++ library, so the main package will be empty.
RDEPENDS:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
