SUMMARY = "usbredir libraries and utilities"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4b54a1fd55a448865a0b32d41598759d \
"

DEPENDS = "libusb1 glib-2.0"

SRCREV = "5fc0e1c43194d948545941d408f4c10d084eb6ed"

SRC_URI = "git://gitlab.freedesktop.org/spice/usbredir;branch=main;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig

BBCLASSEXTEND = "native nativesdk"
