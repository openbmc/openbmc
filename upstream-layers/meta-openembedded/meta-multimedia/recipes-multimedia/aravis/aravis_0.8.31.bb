SUMMARY = "A vision library for genicam based cameras"
DESCRIPTION = "\
    Aravis is a glib/gobject based library for video acquisition using Genicam cameras.\
    It currently implements the gigabit ethernet and USB3 protocols used by industrial cameras.\
    It also provides a basic ethernet camera simulator and a simple video viewer.\
"
HOMEPAGE = "https://github.com/AravisProject/aravis"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS += "\
    glib-2.0 \
    glib-2.0-native \
    libxml2 \
    zlib \
"

SRC_URI = "https://github.com/AravisProject/aravis/releases/download/${PV}/${BPN}-${PV}.tar.xz"
SRC_URI[sha256sum] = "9c4ebe6273ed3abe466cb6ed8fa5c132bdd7e9a9298ca43fa0212c4311a084da"

EXTRA_OEMESON += "-Dtests=false"

GIR_MESON_ENABLE_FLAG = 'enabled'
GIR_MESON_DISABLE_FLAG = 'disabled'
GIDOCGEN_MESON_OPTION = "documentation"
GIDOCGEN_MESON_ENABLE_FLAG = "enabled"
GIDOCGEN_MESON_DISABLE_FLAG = "disabled"

inherit meson pkgconfig gi-docgen gobject-introspection

PACKAGECONFIG ?= "gstreamer usb ${@bb.utils.contains_any('DISTRO_FEATURES', '${GTK3DISTROFEATURES}', 'viewer', '', d)}"
PACKAGECONFIG[gstreamer] = "-Dgst-plugin=enabled, -Dgst-plugin=disabled,gstreamer1.0 gstreamer1.0-plugins-base,"
PACKAGECONFIG[usb] = "-Dusb=enabled, -Dusb=disabled, libusb1,"
PACKAGECONFIG[viewer] = "-Dviewer=enabled, -Dviewer=disabled, gtk+3 gstreamer1.0-plugins-base,"

FILES:${PN} += "\
    ${datadir} \
    ${libdir}/gstreamer-1.0/libgstaravis.0.8.so \
"
