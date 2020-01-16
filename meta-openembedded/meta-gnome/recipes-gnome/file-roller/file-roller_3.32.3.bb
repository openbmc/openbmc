SUMMARY = "An archive manager utility for the  GNOME Environment"
LICENSE="GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
    libarchive \
    libnotify \
"

inherit gnomebase gsettings itstool gnome-help gettext upstream-version-is-even

SRC_URI[archive.md5sum] = "5e098bb254d34f48a9b5761b468f4240"
SRC_URI[archive.sha256sum] = "be111fb877dc1eb487ec5d6e2b72ba5defe1ab8033a6a6b9b9044a2a7787e22a"

PACKAGECONFIG[nautilus] = "-Dnautilus-actions=true,-Dnautilus-actions=false,nautilus"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${libdir}/nautilus \
"
