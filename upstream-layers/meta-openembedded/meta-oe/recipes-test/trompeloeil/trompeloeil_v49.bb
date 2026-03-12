DESCRIPTION = "A popular statically typed mocking framework for C++14 and later"
HOMEPAGE = "https://trompeloeil.github.io"
LICENSE = "BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE_1_0.txt;md5=e4224ccaecb14d942c71d31bef20d78c"

SRC_URI = "git://github.com/rollbear/trompeloeil.git;branch=main;protocol=https;tag=${PV}"
SRCREV = "eaeb89c1ce9d354b0ba7eb921fd5712cdbd78adf"


inherit cmake

# Header-only library
RDEPENDS:${PN}-dev = ""
RRECOMMENDS:${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
