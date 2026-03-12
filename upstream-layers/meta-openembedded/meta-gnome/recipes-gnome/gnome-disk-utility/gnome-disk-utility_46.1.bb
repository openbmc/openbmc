SUMMARY = "GNOME disk utility"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SECTION = "gnome"

DEPENDS = " \
    desktop-file-utils-native \
    gtk+3 \
    libcanberra \
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

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"

# As soon as elogind is of interest this needs rework: meson option is combo
PACKAGECONFIG[systemd] = "-Dlogind=libsystemd,-Dlogind=none,systemd"

SRC_URI[archive.sha256sum] = "c24e9439a04d70bcfae349ca134c7005435fe2b6f452114df878bff0b89bbffe"

EXTRA_OEMESON = "-Dman=false"

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

FILES:${PN} += " \
    ${datadir}/metainfo \
    ${datadir}/dbus-1 \
"
