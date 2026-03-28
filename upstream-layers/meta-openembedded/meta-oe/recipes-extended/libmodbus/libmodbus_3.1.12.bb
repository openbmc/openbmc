SUMMARY = "A Modbus library for Linux, Mac OS, FreeBSD and Windows"
DESCRIPTION = "libmodbus is a free software library to send/receive data with a device which respects the Modbus protocol. This library can use a serial port or an Ethernet connection."
HOMEPAGE = "http://www.libmodbus.org/"
SECTION = "libs"

LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c"

SRC_URI = "git://github.com/stephane/libmodbus;branch=master;protocol=https;tag=v${PV}"
SRCREV = "9af6c16074df566551bca0a7c37443e48f216289"


inherit autotools pkgconfig

PACKAGECONFIG ??= ""
PACKAGECONFIG[test] = "--enable-tests,--disable-tests,,"
