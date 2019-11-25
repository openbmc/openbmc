SUMMARY = "GNOME editor"
SECTION = "x11/gnome"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    yelp-tools-native \
    libxml2-native \
    gdk-pixbuf-native \
    iso-codes \
    gtk+3 \
    gsettings-desktop-schemas \
    gtksourceview4 \
    libpeas \
    libsoup-2.4 \
    gspell \
    gtksourceview4 \
"

inherit gnomebase gsettings itstool gnome-help gobject-introspection gtk-doc vala gettext features_check upstream-version-is-even

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "ec6632fa73d9ea6c817e74a98d16eab6"
SRC_URI[archive.sha256sum] = "3e95e51d3ccb495a9ac95aa3ed7fe8fe37ccde7c678f37fa3cea684bc71d507e"

GTKDOC_MESON_OPTION = "documentation"

PACKAGECONFIG ??= "gvfs"
PACKAGECONFIG[gvfs] = "-Denable-gvfs-metadata=yes,-Denable-gvfs-metadata=no,,gvfs"

PACKAGES += "${PN}-python"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"

FILES_${PN}-python += " \
    ${PYTHON_SITEPACKAGES_DIR} \
"

