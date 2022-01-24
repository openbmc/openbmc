SUMMARY = "GNOME disk utility"
LICENSE = "GPLv2"
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

SRC_URI += " file://0001-build-fix-arguments-of-i18n.merge_file.patch"
SRC_URI[archive.sha256sum] = "8743c98fd656062ef862933efe30c5be4c6b322ec02eee154ec70d08ed0895df"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"
