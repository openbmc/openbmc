DESCRIPTION = "Open source network boot firmware"
HOMEPAGE = "http://ipxe.org"
LICENSE = "GPLv2"
DEPENDS = "binutils-native perl-native syslinux mtools-native cdrtools-native"
LIC_FILES_CHKSUM = "file://../COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

SRCREV = "8c43891db4eb131d019360ccfb619f235b17eb58"
PV = "gitr${SRCPV}"
PR = "r0"

SRC_URI = "git://git.ipxe.org/ipxe.git;protocol=https"

FILES_${PN} = "/usr/share/firmware/*.rom"

EXTRA_OEMAKE = "NO_WERROR=1"
#PARALLEL_MAKE=""

S = "${WORKDIR}/git/src"

do_configure() {
   sed -i s#^ISOLINUX_BIN[\ \\t]*=.*#ISOLINUX_BIN\ =\ ${STAGING_DIR_TARGET}/usr/lib/syslinux/isolinux.bin# arch/i386/Makefile
}

do_compile() {
   oe_runmake
}

do_install() {
    install -d ${D}/usr/share/firmware
    install ${S}/bin/*.rom ${D}/usr/share/firmware/
}
