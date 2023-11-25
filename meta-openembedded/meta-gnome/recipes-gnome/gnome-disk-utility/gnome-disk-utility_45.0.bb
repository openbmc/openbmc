SUMMARY = "GNOME disk utility"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SECTION = "x11/gnome"

DEPENDS = " \
    desktop-file-utils-native \
    gtk+3 \
    libdvdread \
    libnotify \
    libsecret \
    libpwquality \
    udisks2 \
    libhandy \
    xz \
"


inherit gnomebase gsettings gtk-icon-cache gettext features_check mime-xdg

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "polkit"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd x11', d)}"

# As soon as elogind is of interest this needs rework: meson option is combo
PACKAGECONFIG[systemd] = "-Dlogind=libsystemd,-Dlogind=none,systemd"
PACKAGECONFIG[x11] = ",,libcanberra"

SRC_URI:append = " ${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'file://0001-gnome-disk-utility-remove-libcanberra-dependency.patch', '', d)}"
SRC_URI[archive.sha256sum] = "3d8625faa99047bc4aefd29921ad728ab4d700cff86e0f2ec67e8dba877d0dd3"

EXTRA_OEMESON = "-Dman=false"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"
