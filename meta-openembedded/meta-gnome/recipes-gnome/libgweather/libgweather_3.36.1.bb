SUMMARY = "A library to access weather information from online services"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext gtk-doc vala features_check upstream-version-is-even

SRC_URI[archive.md5sum] = "d6081108f9c224c5cb594f8ccb025db9"
SRC_URI[archive.sha256sum] = "de2709f0ee233b20116d5fa9861d406071798c4aa37830ca25f5ef2c0083e450"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST_append = " introspection"

GTKDOC_MESON_OPTION = "gtk_doc"

DEPENDS = " \
    gtk+3 \
    json-glib \
    libsoup-2.4 \
    geocode-glib \
"
