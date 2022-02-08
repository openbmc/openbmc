SUMMARY = "single-file header-only version of a C++20-like span for C++98, C++11 and later"
HOMEPAGE = "https://github.com/martinmoene/span-lite"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI += "git://github.com/martinmoene/span-lite;branch=master;protocol=https"
SRCREV = "e03d1166ccc8481d993dc02aae703966301a5e6e"

S = "${WORKDIR}/git"

inherit cmake
inherit ptest
