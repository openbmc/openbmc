DESCRIPTION = "flashrom is a utility for identifying, reading, writing, verifying and erasing flash chips"
LICENSE = "GPLv2"
HOMEPAGE = "http://flashrom.org"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "pciutils libusb libusb-compat"

SRC_URI = "https://download.flashrom.org/releases/flashrom-${PV}.tar.bz2 \
           file://sst26.patch \
           file://0001-platform-Add-riscv-to-known-platforms.patch \
           file://0001-ch341a_spi-Avoid-deprecated-libusb-functions.patch \
           "
SRC_URI[md5sum] = "42d999990c735d88653627cefcc13b9a"
SRC_URI[sha256sum] = "3702fa215ba5fb5af8e54c852d239899cfa1389194c1e51cb2a170c4dc9dee64"

inherit pkgconfig

do_install() {
    oe_runmake PREFIX=${prefix} DESTDIR=${D} install
}
