DESCRIPTION = "libsigrok is a shared library written in C, which provides the basic hardware access drivers for logic analyzers and other supported devices, as well as input/output file format support."
HOMEPAGE = "http://sigrok.org/wiki/Main_Page"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "glib-2.0 libzip"

PACKAGECONFIG[serialport] = "--with-libserialport,--without-libserialport,libserialport"
PACKAGECONFIG[ftdi] = "--with-libftdi,--without-libftdi,libftdi"
PACKAGECONFIG[usb] = "--with-libusb,--without-libusb,libusb"
PACKAGECONFIG[cxx] = "--enable-cxx,--disable-cxx,glibmm doxygen-native"
PACKAGECONFIG[bluez5] = "--with-libbluez,--without-libbluez,bluez5"
PACKAGECONFIG[hidapi] = "--with-libhidapi,--without-libhidapi,hidapi"

PACKAGECONFIG ??= "serialport ftdi usb"

inherit autotools pkgconfig mime

SRC_URI = "http://sigrok.org/download/source/libsigrok/libsigrok-${PV}.tar.gz"

SRC_URI[md5sum] = "e258d471b6d5eaa58daf927a0dc3ba67"
SRC_URI[sha256sum] = "4d341f90b6220d3e8cb251dacf726c41165285612248f2c52d15df4590a1ce3c"

FILES:${PN} += "${datadir}/*"
