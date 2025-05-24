SUMMARY = "GUsb is a GObject wrapper for libusb1"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "glib-2.0 libusb json-glib"
RDEPENDS:${PN} = "hwdata"

inherit meson gobject-introspection gi-docgen gettext vala pkgconfig

PACKAGECONFIG:class-target ??= "${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'vapi', '', d)}"
PACKAGECONFIG[vapi] = "-Dvapi=true,-Dvapi=false"

EXTRA_OEMESON:class-native += "-Dtests=false  -Dintrospection=false"

GIDOCGEN_MESON_OPTION = 'docs'

SRC_URI = "git://github.com/hughsie/libgusb.git;branch=main;protocol=https"
SRCREV = "ed31c8134d80d006bd45450e84180be2a7c0742e"
S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
