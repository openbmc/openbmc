SUMMARY = "GNOME font viewer"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SECTION = "x11/gnome"

DEPENDS = " \
    gtk+3 \
    gnome-desktop3 \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gtk-icon-cache gettext features_check upstream-version-is-even mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "76004a8986ea622b09c408b01a6f42e5"
SRC_URI[archive.sha256sum] = "aa6f0583e5f93aec095e537f0638b29de3d02491f0131ef584a7c55d39d6b98b"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
    ${datadir}/thumbnailers \
"
