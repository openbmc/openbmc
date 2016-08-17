SUMMARY = "Control basic functions in socketcan from userspace"
HOMEPAGE = "http://www.pengutronix.de"
SECTION = "libs/network"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://src/libsocketcan.c;beginline=3;endline=17;md5=97e38adced4385d8fba1ae2437cedee1"

SRCREV = "e1a224bf1c409adf0c02b07a90deada634e54b88"

SRC_URI = "git://git.pengutronix.de/git/tools/libsocketcan.git;protocol=git \
    file://0001-Use-strcmp-instead-of-sizeof-on-char-string.patch \
"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

do_configure_prepend() {
    sed -i -e s:tests/GNUmakefile::g -e s:trunk:0.0.9: ${S}/configure.ac
}
