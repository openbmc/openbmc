SUMMARY = "Babl is a dynamic, any to any, pixel format conversion library"
LICENSE = "LGPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6a6a8e020838b23406c81b19c1d46df6"

GNOMEBASEBUILDCLASS = "meson"

GIR_MESON_OPTION = "enable-gir"

inherit setuptools3 gnomebase gobject-introspection vala

DEPENDS += "lcms"

SRC_URI = "https://download.gimp.org/pub/${BPN}/0.1/${BP}.tar.xz"
SRC_URI[sha256sum] = "a88bb28506575f95158c8c89df6e23686e50c8b9fea412bf49fe8b80002d84f0"

FILES:${PN} += "${libdir}/${BPN}-${@gnome_verdir("${PV}")}"

BBCLASSEXTEND = "native"
