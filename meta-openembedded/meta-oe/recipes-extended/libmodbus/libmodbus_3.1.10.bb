SUMMARY = "A Modbus library for Linux, Mac OS, FreeBSD and Windows"
DESCRIPTION = "libmodbus is a free software library to send/receive data with a device which respects the Modbus protocol. This library can use a serial port or an Ethernet connection."
HOMEPAGE = "http://www.libmodbus.org/"
SECTION = "libs"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/stephane/libmodbus;branch=master;protocol=https"
SRCREV = "2cbafa3113e276c3697d297f68e88d112b53174d"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[test] = "--enable-tests,--disable-tests,,"
