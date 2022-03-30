SUMMARY = "GNOME disk utility"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SECTION = "x11/gnome"

DEPENDS = " \
    gtk+3 \
    libdvdread \
    libcanberra \
    libnotify \
    libsecret \
    libpwquality \
    udisks2 \
    libhandy \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gtk-icon-cache gettext features_check mime-xdg

REQUIRED_DISTRO_FEATURES = "x11 polkit"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

# As soon as elogind is of interest this needs rework: meson option is combo
PACKAGECONFIG[systemd] = "-Dlogind=libsystemd,-Dlogind=none,systemd"

SRC_URI[archive.sha256sum] = "1b6564454d67426322cb3bfc5a5558653bfc7dfeea2ae0825b1d08629f01090b"

EXTRA_OEMESON = "-Dman=false"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"
