DESCRIPTION = "The Openobex project is an open source implementation of the \
Object Exchange (OBEX) protocol."
HOMEPAGE = "http://openobex.triq.net"
SECTION = "libs"
DEPENDS = "virtual/libusb0"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES','bluez5','bluez5','bluez4',d)}"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a \
                    file://COPYING.LIB;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

SRC_URI = "http://www.kernel.org/pub/linux/bluetooth/openobex-${PV}.tar.gz \
           file://disable-cable-test.patch \
           file://libusb_crosscompile_check.patch \
           file://separate_builddir.patch"

SRC_URI[md5sum] = "0d83dc86445a46a1b9750107ba7ab65c"
SRC_URI[sha256sum] = "e602047570799a47ecb028420bda8f2cef41310e5a99d084de10aa9422935e65"

inherit autotools binconfig pkgconfig

EXTRA_OECONF = "--enable-apps --enable-syslog"

do_install_append() {
    install -d ${D}${datadir}/aclocal
    install -m 0644 ${S}/openobex.m4 ${D}${datadir}/aclocal
}

PACKAGES += "openobex-apps"
FILES_${PN} = "${libdir}/lib*.so.*"
FILES_${PN}-dev += "${bindir}/openobex-config"
FILES_${PN}-apps = "${bindir}/*"
DEBIAN_NOAUTONAME_${PN}-apps = "1"

