SUMMARY = "A multi-platform support library with a focus on asynchronous I/O"
HOMEPAGE = "https://github.com/libuv/libuv"
BUGTRACKER = "https://github.com/libuv/libuv/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a68902a430e32200263d182d44924d47"

SRC_URI = "https://github.com/libuv/libuv/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "cc2cf259442fbe85404e75691e8244e1"
SRC_URI[sha256sum] = "4afcdc84cd315b77c8e532e7b3fde43d536af0e2e835eafbd0e75518ed26dbed"

inherit autotools

do_configure() {
    ${S}/autogen.sh || bbnote "${PN} failed to autogen.sh"
    oe_runconf
}

BBCLASSEXTEND = "native"
