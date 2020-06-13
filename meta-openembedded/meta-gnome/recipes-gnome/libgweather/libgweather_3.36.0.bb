SUMMARY = "A library to access weather information from online services"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext gtk-doc vala features_check upstream-version-is-even

SRC_URI[archive.md5sum] = "32e43c6b35bec88a5549ab62c71913cb"
SRC_URI[archive.sha256sum] = "d2ffeec01788d03d1bbf35113fc2f054c6c3600721088f827bcc31e5c603a32d"

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
