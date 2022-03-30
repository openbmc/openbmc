DESCRIPTION = "Zlog is a pure C logging library"
HOMEPAGE = "https://github.com/HardySimpson/zlog"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

SRCREV = "876099f3c66033f3de11d79f63814766b1021dbe"
SRC_URI = "git://github.com/HardySimpson/zlog;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit pkgconfig

EXTRA_OEMAKE = "CC='${CC}' LD='${LD}' LIBRARY_PATH=${baselib}"

do_install() {
    oe_runmake install PREFIX=${D}${exec_prefix} INSTALL=install
}
