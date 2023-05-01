SUMMARY = "GUsb is a GObject wrapper for libusb1"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "glib-2.0 libusb"

inherit meson gobject-introspection gtk-doc gettext vala

PACKAGECONFIG:class-target ??= "${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'vapi', '', d)}"
PACKAGECONFIG[vapi] = "-Dvapi=true,-Dvapi=false"

EXTRA_OEMESON:append:class-native = " -Dtests=false -Dintrospection=false"

SRC_URI = "git://github.com/hughsie/libgusb.git;branch=main;protocol=https"
SRCREV = "332d5b987ffecb824426e88518e05547faf2b520"
S = "${WORKDIR}/git"

BBCLASSEXTEND = "native"
