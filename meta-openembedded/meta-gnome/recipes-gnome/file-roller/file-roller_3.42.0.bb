SUMMARY = "An archive manager utility for the  GNOME Environment"
LICENSE="GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

GNOMEBASEBUILDCLASS = "meson"

DEPENDS = " \
    glib-2.0-native \
    gtk+3 \
    libarchive \
    libnotify \
    libhandy \
"

inherit gnomebase gsettings itstool gnome-help gettext upstream-version-is-even mime-xdg

SRC_URI[archive.sha256sum] = "1c438e6d53ec10ff4f2eb5b22d7bbf28a7c2a84957ab64a751c1cdf3c52302c7"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'packagekit', '', d)}"

PACKAGECONFIG[nautilus] = "-Dnautilus-actions=enabled,-Dnautilus-actions=disabled,nautilus"
PACKAGECONFIG[packagekit] = "-Dpackagekit=true,-Dpackagekit=false,"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${libdir}/nautilus \
"
