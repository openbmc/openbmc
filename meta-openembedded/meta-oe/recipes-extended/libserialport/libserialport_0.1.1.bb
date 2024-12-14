DESCRIPTION = "libserialport is a minimal, cross-platform shared library written in C that is intended to take care of the OS-specific details when writing software that uses serial ports."
HOMEPAGE = "https://sigrok.org/wiki/Libserialport"

LICENSE = "LGPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

inherit autotools

SRC_URI = "http://sigrok.org/download/source/libserialport/libserialport-${PV}.tar.gz"

SRC_URI[sha256sum] = "4a2af9d9c3ff488e92fb75b4ba38b35bcf9b8a66df04773eba2a7bbf1fa7529d"
