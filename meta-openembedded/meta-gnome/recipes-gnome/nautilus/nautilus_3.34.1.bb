SUMMARY = "File manager for GNOME"
SECTION = "x11/gnome"

LICENSE="GPLv3"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    fontconfig \
    gtk+3 \
    gnome-desktop3 \
    gsettings-desktop-schemas \
    gnome-autoar \
    tracker \
"

inherit gnomebase gsettings gobject-introspection gtk-doc gettext features_check upstream-version-is-even

SRC_URI[archive.md5sum] = "19e4f1d89fb9d0ff135d5b1974ce43b5"
SRC_URI[archive.sha256sum] = "37ce2c16a610c589dcc7660f9092446465568e38e29bce6ed8c24f2e8e0077f3"

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGECONFIG = "extensions"
PACKAGECONFIG[extensions] = "-Dextensions=true,-Dextensions=false, gexiv2 gstreamer1.0-plugins-base"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/gnome-shell \
"
