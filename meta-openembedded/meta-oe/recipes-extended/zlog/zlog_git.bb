DESCRIPTION = "Zlog is a pure C logging library"
HOMEPAGE = "https://github.com/HardySimpson/zlog"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

PV = "1.2.12+git${SRCPV}"

SRCREV = "13904dab2878aa2654d0c20fb8600a3dc5f2dd68"
SRC_URI = "git://github.com/HardySimpson/zlog \
           file://0001-event.c-Cast-pthread_t-to-unsigned-long-instead-of-u.patch \
           "

S = "${WORKDIR}/git"

inherit pkgconfig

EXTRA_OEMAKE = "CC='${CC}' LD='${LD}'"

do_install() {
    oe_runmake install PREFIX=${D}${exec_prefix} INSTALL=install
}
