SUMMARY = "GUsb is a GObject wrapper for libusb1"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "glib-2.0 libusb"

inherit meson gobject-introspection gtk-doc gettext vala

SRC_URI = "git://github.com/hughsie/libgusb.git \
           file://0001-generate-version-script-Don-t-hard-code-the-path-of-.patch \
           "
SRCREV = "377917fed85476d615f72279d0c97bc391d0f191"
S = "${WORKDIR}/git"
