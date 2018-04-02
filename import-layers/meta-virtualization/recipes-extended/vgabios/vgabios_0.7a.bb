DESCRIPTION = "Plex86/Bochs LGPL VGABios"
HOMEPAGE = "http://www.nongnu.org/vgabios/"
LICENSE = "LGPLv2.1"
SECTION = "firmware"

DEPENDS = "dev86-native biossums-native"

LIC_FILES_CHKSUM = "file://COPYING;md5=dcf3c825659e82539645da41a7908589"

SRC_URI =  "http://savannah.gnu.org/download/vgabios/${BPN}-${PV}.tgz"

SRC_URI[md5sum] = "2c0fe5c0ca08082a9293e3a7b23dc900"
SRC_URI[sha256sum] = "9d24c33d4bfb7831e2069cf3644936a53ef3de21d467872b54ce2ea30881b865"

PR = "r0"

FILES_${PN} = "/usr/share/firmware/${PN}-${PV}*.bin"
FILES_${PN}-dbg = "/usr/share/firmware/${PN}-${PV}*.debug.bin"

S = "${WORKDIR}/${PN}-${PV}"

do_configure() {
    # Override to use the native-built biossums tool:
    sed 's,./biossums,biossums,' -i Makefile
    sed 's,$(CC) -o biossums biossums.c,touch biossums,' -i Makefile
}

do_install() {
    install -d ${D}/usr/share/firmware
    install -m 0644 VGABIOS-lgpl-latest.bin ${D}/usr/share/firmware/${PN}-${PV}.bin
    install -m 0644 VGABIOS-lgpl-latest.cirrus.bin ${D}/usr/share/firmware/${PN}-${PV}.cirrus.bin
}

