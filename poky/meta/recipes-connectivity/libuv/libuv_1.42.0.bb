SUMMARY = "A multi-platform support library with a focus on asynchronous I/O"
HOMEPAGE = "https://github.com/libuv/libuv"
DESCRIPTION = "libuv is a multi-platform support library with a focus on asynchronous I/O. It was primarily developed for use by Node.js, but it's also used by Luvit, Julia, pyuv, and others."
BUGTRACKER = "https://github.com/libuv/libuv/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a68902a430e32200263d182d44924d47"

SRCREV = "6ce14710da7079eb248868171f6343bc409ea3a4"
SRC_URI = "git://github.com/libuv/libuv;branch=v1.x"

S = "${WORKDIR}/git"

inherit autotools

do_configure() {
    ${S}/autogen.sh || bbnote "${PN} failed to autogen.sh"
    oe_runconf
}

BBCLASSEXTEND = "native"
