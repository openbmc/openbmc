DESCRIPTION = "libsigrok is a shared library written in C, which provides the basic hardware access drivers for logic analyzers and other supported devices, as well as input/output file format support."
HOMEPAGE = "http://sigrok.org/wiki/Main_Page"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "glib-2.0 libzip"

PACKAGECONFIG[serialport] = "--with-libserialport,--without-libserialport,libserialport"
PACKAGECONFIG[ftdi] = "--with-libftdi,--without-libftdi,libftdi"
PACKAGECONFIG[usb] = "--with-libusb,--without-libusb,libusb"
PACKAGECONFIG[cxx] = "--enable-cxx,--disable-cxx,glibmm doxygen-native"

PACKAGECONFIG ??= "serialport ftdi usb"

inherit autotools pkgconfig

SRC_URI = "http://sigrok.org/download/source/libsigrok/libsigrok-${PV}.tar.gz"

SRC_URI[md5sum] = "a3de9e52a660e51d27a6aca025d204a7"
SRC_URI[sha256sum] = "e40fde7af98d29e922e9d3cbe0a6c0569889153fc31e47b8b1afe4d846292b9c"

FILES_${PN} += "${datadir}/*"
