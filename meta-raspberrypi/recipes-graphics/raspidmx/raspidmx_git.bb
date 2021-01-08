SUMMARY = "Some examples using the DispmanX API on the Raspberry Pi"
HOMEPAGE = "https://github.com/AndrewFromMelbourne/raspidmx"
SECTION = "graphics"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=52962875ab02c36df6cde47b1f463024"

COMPATIBLE_HOST = "null"
COMPATIBLE_HOST_rpi = "${@bb.utils.contains('MACHINE_FEATURES', 'vc4graphics', 'null', '(.*)', d)}"

SRC_URI = "git://github.com/AndrewFromMelbourne/raspidmx;protocol=https \
           file://0001-gitignore-add-archives-from-lib-directory.patch \
           file://0002-add-install-targets-to-Makefiles.patch \
           file://0003-switch-to-pkg-config.patch \
           file://0004-add-libvchostif-to-link.patch \
           file://0005-change-library-linking-order.patch \
           file://0006-game-Makefile-install-sample-png-files.patch \
           file://0007-Makefile-reorganize.patch \
           "

PV = "0.0+git${SRCPV}"
SRCREV = "e2ee6faa0d01a5ece06bcc74a47f37d7e6837310"

S = "${WORKDIR}/git"

inherit pkgconfig

DEPENDS += "libpng userland"

do_install () {
	oe_runmake 'DESTDIR=${D}' 'TARGET=install'
}
