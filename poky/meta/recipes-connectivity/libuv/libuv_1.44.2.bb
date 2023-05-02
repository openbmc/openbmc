SUMMARY = "A multi-platform support library with a focus on asynchronous I/O"
HOMEPAGE = "https://github.com/libuv/libuv"
DESCRIPTION = "libuv is a multi-platform support library with a focus on asynchronous I/O. It was primarily developed for use by Node.js, but it's also used by Luvit, Julia, pyuv, and others."
BUGTRACKER = "https://github.com/libuv/libuv/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ad93ca1fffe931537fcf64f6fcce084d"

SRCREV = "0c1fa696aa502eb749c2c4735005f41ba00a27b8"
SRC_URI = "git://github.com/libuv/libuv.git;branch=v1.x;protocol=https"
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit autotools

do_configure() {
    ${S}/autogen.sh || bbnote "${PN} failed to autogen.sh"
    oe_runconf
}

BBCLASSEXTEND = "native"
