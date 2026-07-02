SUMMARY = "Generic USB CCID smart card reader driver"
HOMEPAGE = "https://ccid.apdu.fr/"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4bf661c1e3793e55c8d1051bc5e0ae21"

DEPENDS = "virtual/libusb0 pcsc-lite"
RDEPENDS:${PN} = "pcsc-lite"

SRC_URI = "https://ccid.apdu.fr/files/ccid-${PV}.tar.xz"

SRC_URI[sha256sum] = "531dc29e7c1b9e22e1918f0767b625d52ce8a98f266eb2144c5cf5dbd29c0f67"

inherit meson pkgconfig

FILES:${PN} += "${libdir}/pcsc/"
FILES:${PN}-dbg += "${libdir}/pcsc/drivers/*/*/*/.debug"
