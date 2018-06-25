SUMMARY = "A multi-platform support library with a focus on asynchronous I/O"
HOMEPAGE = "https://github.com/libuv/libuv"
BUGTRACKER = "https://github.com/libuv/libuv/issues"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=bb5ea0d651f4c3519327171906045775"

S = "${WORKDIR}/git"
SRCREV = "7452ef4e06a4f99ee26b694c65476401534f2725"
BRANCH = "v1.x"
SRC_URI = "git://github.com/libuv/libuv.git;protocol=https;branch=${BRANCH};"

inherit autotools

do_configure() {
    ${S}/autogen.sh || bbnote "${PN} failed to autogen.sh"
    oe_runconf
}

BBCLASSEXTEND = "native"
