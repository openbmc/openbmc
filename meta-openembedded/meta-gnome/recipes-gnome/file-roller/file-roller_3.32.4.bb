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

inherit gnomebase gsettings itstool gnome-help gettext upstream-version-is-even mime-xdg

SRC_URI[archive.md5sum] = "21982b8d298cb2994a99494399dcb2d5"
SRC_URI[archive.sha256sum] = "1b3b2cbfcb444e0d150bfd063add9d5181c8ab24f0593e5a8894399297214017"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagekit', '', d)}"

PACKAGECONFIG[nautilus] = "-Dnautilus-actions=true,-Dnautilus-actions=false,nautilus"
PACKAGECONFIG[packagekit] = "-Dpackagekit=true,-Dpackagekit=false,"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${libdir}/nautilus \
"
