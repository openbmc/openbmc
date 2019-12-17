DESCRIPTION = "flashrom is a utility for identifying, reading, writing, verifying and erasing flash chips"
LICENSE = "GPLv2"
HOMEPAGE = "http://flashrom.org"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
DEPENDS = "pciutils libusb libusb-compat"

SRC_URI = "https://download.flashrom.org/releases/flashrom-v${PV}.tar.bz2 \
           "
SRC_URI[md5sum] = "91bab6c072e38a493bb4eb673e4fe0d6"
SRC_URI[sha256sum] = "aeada9c70c22421217c669356180c0deddd0b60876e63d2224e3260b90c14e19"

S = "${WORKDIR}/flashrom-v${PV}"

inherit pkgconfig

do_install() {
    oe_runmake PREFIX=${prefix} DESTDIR=${D} install
}
