DESCRIPTION = "Modern C++ network programming libraries."

# This library provides general purpose network functionality such as
# socket communication to agent libraries. It was designed to be merged
# into boost at some point and follows similar header library approach
# for most functionality.

SECTION = "libs"
LICENSE = "BSL-1.0 & MIT & Python-2.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "\
    git://github.com/cpp-netlib/cpp-netlib.git;protocol=https;branch=0.13-release  \
"
SRC_URI[sha256sum] = "0b9255bb0668d89867a1f367d770f12d7038db4f5b6111774ef032f669cccad4"

SRCREV = "ca95f04d140acf619892ee02a82e930dd91ff7d4"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

DEPENDS = "zlib boost openssl"

EXTRA_OECMAKE_append = " -DCPP-NETLIB_BUILD_TESTS=OFF -DCPP-NETLIB_BUILD_EXAMPLES=OFF"

