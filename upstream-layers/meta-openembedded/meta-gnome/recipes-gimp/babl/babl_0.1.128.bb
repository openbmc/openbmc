SUMMARY = "Babl is a dynamic, any to any, pixel format conversion library"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6a8e020838b23406c81b19c1d46df6"

GIR_MESON_OPTION = "enable-gir"
VALA_MESON_OPTION = "enable-vapi"

inherit setuptools3 gnomebase gobject-introspection vala

DEPENDS += "lcms"

SRC_URI = "https://download.gimp.org/pub/${BPN}/0.1/${BP}.tar.xz"
SRC_URI[sha256sum] = "e676507b421d414d49db8627415dc1eadbd787536baa7d6fe94a1f1971cbe73e"

FILES:${PN} += "${libdir}/${BPN}-${@gnome_verdir("${PV}")}"

BBCLASSEXTEND = "native"
