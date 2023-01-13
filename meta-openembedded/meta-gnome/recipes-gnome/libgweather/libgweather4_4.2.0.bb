SUMMARY = "A library to access weather information from online services"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "meson"
GNOMEBN = "libgweather"
S = "${WORKDIR}/${GNOMEBN}-${PV}"

inherit gnomebase gsettings gobject-introspection gettext gtk-doc vala features_check

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI += "file://0001-Allow-building-gir-in-cross-environments.patch"
SRC_URI[archive.sha256sum] = "af8a812da0d8976a000e1d62572c256086a817323fbf35b066dbfdd8d2ca6203"

GTKDOC_MESON_OPTION = "gtk_doc"

DEPENDS = " \
    geocode-glib \
    gtk4 \
    json-glib \
    libsoup-3.0 \
    python3-pygobject-native \
"

FILES:${PN} += " \
    ${datadir}/libgweather-4 \
    ${libdir}/libgweather-4 \
"
