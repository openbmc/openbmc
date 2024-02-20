SUMMARY = "CommonMark parsing and rendering library and program in C"
HOMEPAGE = "https://github.com/commonmark/cmark"
LICENSE = "BSD-2-Clause & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=81f9cae6293cc0345a9144b78152ab62"

SRC_URI = "git://github.com/commonmark/cmark.git;branch=master;protocol=https"
SRCREV = "8fbf029685482827828b5858444157052f1b0a5f"
S = "${WORKDIR}/git"

inherit cmake lib_package

EXTRA_OECMAKE += " \
    -DCMARK_TESTS=OFF \
    -DCMARK_STATIC=OFF \
"
