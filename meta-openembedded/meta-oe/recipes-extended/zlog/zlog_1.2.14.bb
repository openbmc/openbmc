DESCRIPTION = "Zlog is a pure C logging library"
HOMEPAGE = "https://github.com/HardySimpson/zlog"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRCREV = "8fc78c3c65cb705953a2f3f9a813c3ef3c8b2270"
SRC_URI = "git://github.com/HardySimpson/zlog"

S = "${WORKDIR}/git"

inherit pkgconfig

EXTRA_OEMAKE = "CC='${CC}' LD='${LD}' LIBRARY_PATH=${baselib}"

do_install() {
    oe_runmake install PREFIX=${D}${exec_prefix} INSTALL=install
}
