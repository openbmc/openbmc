SUMMARY = "A library to access weather information from online services"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "meson"
GNOMEBN = "libgweather"
S = "${WORKDIR}/${GNOMEBN}-${PV}"

inherit gnomebase gsettings gobject-introspection gettext gtk-doc vala

SRC_URI += "file://0001-Allow-building-gir-in-cross-environments.patch"
SRC_URI[archive.sha256sum] = "440d44801b6f72b48c676e5e37f9109cfee1394fd74cc92725e1b1ba4fae834c"

GTKDOC_MESON_OPTION = "gtk_doc"

DEPENDS = " \
    geocode-glib \
    gtk+3 \
    json-glib \
    libsoup-2.4 \
    python3-pygobject-native \
"

FILES:${PN} += " \
    ${datadir}/libgweather-4 \
    ${libdir}/libgweather-4 \
"
