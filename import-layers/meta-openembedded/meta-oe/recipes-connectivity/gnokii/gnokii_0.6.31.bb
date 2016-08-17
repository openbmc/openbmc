SUMMARY = "Cellphone tools and driver software"
SECTION = "console/network"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

SRC_URI = "http://www.gnokii.org/download/gnokii/gnokii-${PV}.tar.bz2"

DEPENDS = "glib-2.0"
X11DEPENDS = " libxpm gtk+"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[bluez4] = "--enable-bluetooth,--disable-bluetooth,bluez4"
PACKAGECONFIG[libical] = "--enable-libical,--disable-libical,libical"
PACKAGECONFIG[pcsc-lite] = "--enable-libpcsclite,--disable-libpcsclite,pcsc-lite"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[usb] = "--enable-libusb,--disable-libusb,virtual/libusb0"
PACKAGECONFIG[x11] = ",--without-x,${X11DEPENDS}"

inherit autotools pkgconfig

PACKAGES += "libgnokii libgnokii-dev"

EXTRA_OECONF = "--disable-smsd"

FILES_${PN} = "${bindir} ${sbindir}"
FILES_libgnokii-dev = "${includedir} ${libdir}/lib*.so ${libdir}/*.la \
                ${libdir}/*.a ${libdir}/*.o ${libdir}/pkgconfig \
	        /lib/*.a /lib/*.o ${datadir}/aclocal"
FILES_${PN}-dev = ""
FILES_libgnokii = "${libdir}/libgnokii.so.*"

SRC_URI[md5sum] = "d9627f4a1152d3ea7806df4532850d5f"
SRC_URI[sha256sum] = "8f5a083b05c1a66a3402ca5cd80084e14c2c0632c991bb53b03c78e9adb02501"
