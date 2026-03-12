SUMMARY = "Babl is a dynamic, any to any, pixel format conversion library"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6a8e020838b23406c81b19c1d46df6"

GIR_MESON_OPTION = "enable-gir"
VALA_MESON_OPTION = "enable-vapi"

inherit setuptools3 gnomebase gobject-introspection vala

DEPENDS += "lcms"

SRC_URI = "https://download.gimp.org/pub/${BPN}/0.1/${BP}.tar.xz"
SRC_URI[sha256sum] = "6851f705cda38f2df08a4ba8618279ce30d0a46f957fe6aa325b7b7de297bed2"

FILES:${PN} += "${libdir}/${BPN}-${@gnome_verdir("${PV}")}"

BBCLASSEXTEND = "native"
