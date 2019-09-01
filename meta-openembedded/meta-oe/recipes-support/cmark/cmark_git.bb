SUMMARY = "CommonMark parsing and rendering library and program in C"
HOMEPAGE = "https://github.com/commonmark/cmark"
LICENSE = "BSD-2-Clause & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=81f9cae6293cc0345a9144b78152ab62"

SRC_URI = "git://github.com/commonmark/cmark.git"
SRCREV = "8daa6b1495124f0b67e6034130e12d7be83e38bd"
S = "${WORKDIR}/git"

PV = "0.29.0"

inherit cmake lib_package

EXTRA_OECMAKE += " \
    -DCMARK_TESTS=OFF \
    -DCMARK_STATIC=OFF \
"
