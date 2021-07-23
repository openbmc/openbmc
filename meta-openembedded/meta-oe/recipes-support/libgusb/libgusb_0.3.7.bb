SUMMARY = "GUsb is a GObject wrapper for libusb1"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "glib-2.0 libusb"

inherit meson gobject-introspection gtk-doc gettext vala

SRC_URI = "git://github.com/hughsie/libgusb.git"
SRCREV = "ff9c606d9d72c7dd369ad220d92729556faaaf0c"
S = "${WORKDIR}/git"
