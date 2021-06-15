SUMMARY = "single-file header-only version of a C++20-like span for C++98, C++11 and later"
HOMEPAGE = "https://github.com/martinmoene/span-lite"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI += "git://github.com/martinmoene/span-lite"
SRCREV = "126453198fe2236b31ddfa7353e95d3a06c478ab"

S = "${WORKDIR}/git"

inherit cmake
inherit ptest
