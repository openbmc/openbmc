SUMMARY = "A library to access weather information from online services"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext gtk-doc vala features_check upstream-version-is-even

SRC_URI[archive.md5sum] = "52c3b1e27887fc88f862c92c42d930c1"
SRC_URI[archive.sha256sum] = "02245395d639d9749fe2d19b7e66b64a152b9509ab0e5aad92514538b9c6f1b9"

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
