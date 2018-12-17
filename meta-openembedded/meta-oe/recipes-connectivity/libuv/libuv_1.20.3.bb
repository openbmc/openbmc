SUMMARY = "A multi-platform support library with a focus on asynchronous I/O"
HOMEPAGE = "https://github.com/libuv/libuv"
BUGTRACKER = "https://github.com/libuv/libuv/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a68902a430e32200263d182d44924d47"

S = "${WORKDIR}/git"
SRCREV = "8cfd67e59195251dff793ee47c185c9d6a8f3818"
BRANCH = "v1.x"
SRC_URI = "git://github.com/libuv/libuv.git;protocol=https;branch=${BRANCH};"

inherit autotools

do_configure() {
    ${S}/autogen.sh || bbnote "${PN} failed to autogen.sh"
    oe_runconf
}

BBCLASSEXTEND = "native"
