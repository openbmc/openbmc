SUMMARY = "usbredir libraries and utilities"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://COPYING.LIB;md5=4b54a1fd55a448865a0b32d41598759d \
"

DEPENDS = "libusb1 glib-2.0"

SRCREV = "2d373432e604960c1dcc14fb97098febe4d64025"

SRC_URI = "git://gitlab.freedesktop.org/spice/usbredir;branch=main;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig

BBCLASSEXTEND = "native nativesdk"
