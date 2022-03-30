SUMMARY = "A library to access weather information from online services"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gobject-introspection gettext gtk-doc vala features_check upstream-version-is-even

SRC_URI[archive.sha256sum] = "ca4e8f2a4baaa9fc6d75d8856adb57056ef1cd6e55c775ba878ae141b6276ee6"

# gobject-introspection is mandatory and cannot be configured
REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"
GIR_MESON_OPTION = ""

GTKDOC_MESON_OPTION = "gtk_doc"

DEPENDS = " \
    geocode-glib \
    gtk+3 \
    json-glib \
    libsoup-2.4 \
    python3-pygobject-native \
"
