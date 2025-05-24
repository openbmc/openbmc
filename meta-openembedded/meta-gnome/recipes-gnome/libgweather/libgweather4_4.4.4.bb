SUMMARY = "A library to access weather information from online services"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBN = "libgweather"
S = "${WORKDIR}/${GNOMEBN}-${PV}"

inherit gnomebase gsettings gobject-introspection gettext gi-docgen vala features_check

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI += "file://0001-Allow-building-gir-in-cross-environments.patch"
SRC_URI[archive.sha256sum] = "7017677753cdf7d1fdc355e4bfcdb1eba8369793a8df24d241427a939cbf4283"

GTKDOC_MESON_OPTION = "gtk_doc"
VALA_MESON_OPTION = "enable_vala"

export GI_TYPELIB_PATH = "${STAGING_LIBDIR}/girepository-1.0/"

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
