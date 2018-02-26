DESCRIPTION = "flashrom is a utility for identifying, reading, writing, verifying and erasing flash chips"
LICENSE = "GPLv2"
HOMEPAGE = "http://flashrom.org"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "pciutils libusb libusb-compat"

SRC_URI = "http://download.flashrom.org/releases/flashrom-${PV}.tar.bz2 \
           file://0001-spi-Define-_XOPEN_SOURCE-to-enable-ffs-libc-API.patch \
           file://sst26.patch \
           file://0001-Remove-undefined-behavior.patch \
           "
SRC_URI[md5sum] = "aab9c98925d9cfb5ffb28b67a6112530"
SRC_URI[sha256sum] = "cb3156b0f63eb192024b76c0814135930297aac41f80761a5d293de769783c45"

inherit pkgconfig

do_install() {
    oe_runmake PREFIX=${prefix} DESTDIR=${D} install
}
