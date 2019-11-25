SUMMARY = "An archive manager utility for the  GNOME Environment"
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
    nautilus \
    libarchive \
    libnotify \
"

inherit gnomebase gsettings itstool gnome-help gettext upstream-version-is-even

SRC_URI[archive.md5sum] = "e79715bb2400df83c0f67cc0cce2b655"
SRC_URI[archive.sha256sum] = "c60a79e0baf74cb1c09a1c8f5ffe0d6e311227ca14ecc5b1156beb3715341a71"

PACKAGECONFIG[nautilus] = "-Dnautilus-actions=true,-Dnautilus-actions=false,nautilus"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${libdir}/nautilus \
"
