SUMMARY = "Access, organize and share your photos on GNOME"
SECTION = "x11/gnome"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464"

DEPENDS = " \
    glib-2.0-native \
    gdk-pixbuf-native \
    librsvg-native \
    gtk+3 \
    babl \
    gegl \
    geocode-glib \
    gnome-online-accounts \
    grilo \
    gsettings-desktop-schemas \
    libdazzle \
    libgdata \
    gfbgraph \
    tracker \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gettext upstream-version-is-even gnome-help features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "1dd0d477eac4707e8cfe9f35e26d1f29"
SRC_URI[archive.sha256sum] = "3c59c76ef28618ec055a1799d1040287b90a0b021feb0a02b1eac28e9c2eb41a"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
"
