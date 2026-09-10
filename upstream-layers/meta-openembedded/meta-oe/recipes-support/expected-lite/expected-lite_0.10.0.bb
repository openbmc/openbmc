SUMMARY = "Expected objects in C++11 and later in a single-file header-only library"
DESCRIPTION = "A single-file header-only library to represent value objects \
               that either contain a valid value or an error, providing a \
               backport of the C++23 std::expected for use with C++11 and \
               later."
HOMEPAGE = "https://github.com/martinmoene/expected-lite"
SECTION = "libs"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "git://github.com/martinmoene/expected-lite.git;protocol=https;branch=master;tag=v${PV}"
SRCREV = "3583e95bad46f5af7d8a826632bc68a6f7281551"

inherit cmake

BBCLASSEXTEND = "native nativesdk"
