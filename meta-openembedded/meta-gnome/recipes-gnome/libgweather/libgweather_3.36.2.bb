SUMMARY = "A library to access weather information from online services"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext gtk-doc vala features_check upstream-version-is-even

SRC_URI[archive.md5sum] = "ff399cf89e97a3e574ae05db5617b96b"
SRC_URI[archive.sha256sum] = "ee1201a8fc25c14e940d3b26db49a34947c8aebf67dee01ee67fbcb06ecb37a0"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
UNKNOWN_CONFIGURE_WHITELIST:append = " introspection"

GTKDOC_MESON_OPTION = "gtk_doc"

DEPENDS = " \
    gtk+3 \
    json-glib \
    libsoup-2.4 \
    geocode-glib \
"
