SUMMARY = "execstack tool"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"

DEPENDS = "binutils-native elfutils-native"

SRC_URI = "git://git.yoctoproject.org/prelink-cross;protocol=https;branch=master"
SRCREV = "ff2561c02ade96c5d4d56ddd4e27ff064840a176"
PV = "1.0+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools native

do_compile() {
    oe_runmake -C ${B}/src execstack
}

do_install() {
    oe_runmake -C ${B}/src install-binPROGRAMS DESTDIR="${D}"
}
