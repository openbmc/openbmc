SUMMARY = "GUsb is a GObject wrapper for libusb1"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS = "glib-2.0 libusb"

inherit meson gobject-introspection gtk-doc gettext vala

PACKAGECONFIG ??= "${@bb.utils.contains('GI_DATA_ENABLED', 'True', 'vapi', '', d)}"
PACKAGECONFIG[vapi] = "-Dvapi=true,-Dvapi=false"

SRC_URI = "git://github.com/hughsie/libgusb.git;branch=main;protocol=https"
SRCREV = "582f33178a986e74543de8ced087865009f8fef0"
S = "${WORKDIR}/git"
