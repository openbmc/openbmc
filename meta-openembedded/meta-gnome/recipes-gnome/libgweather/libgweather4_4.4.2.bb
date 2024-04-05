SUMMARY = "A library to access weather information from online services"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBN = "libgweather"
S = "${WORKDIR}/${GNOMEBN}-${PV}"

inherit gnomebase gsettings gobject-introspection gettext gi-docgen vala features_check

REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI += "file://0001-Allow-building-gir-in-cross-environments.patch"
SRC_URI[archive.sha256sum] = "a6e427b4770ada48945f3aa906af710fc833cff0d42df91f1828302740d794ec"

GTKDOC_MESON_OPTION = "gtk_doc"
VALA_MESON_OPTION = "enable_vala"

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
