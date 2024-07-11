SUMMARY = "GNOME Settings"
DESCRIPTION = "GNOME Settings is GNOME's main interface for configuration of various aspects of your desktop"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=75859989545e37968a99b631ef42722e"

GTKIC_VERSION = "4"

DEPENDS = " \
    accountsservice \
    colord-gtk \
    gcr \
    gdk-pixbuf \
    glib-2.0 \
    gnome-bluetooth \
    gnome-desktop \
    gnome-online-accounts \
    gnome-settings-daemon \
    gsettings-desktop-schemas \
    gtk4 \
    libadwaita \
    libepoxy \
    libgtop \
    libgudev \
    libnma \
    libpwquality \
    libxml2 \
    polkit \
    pulseaudio \
    samba \
    setxkbmap-native \
    tecla \
    udisks2 \
    upower \
"

inherit gtk-icon-cache pkgconfig gnomebase gsettings gettext upstream-version-is-even bash-completion features_check useradd

REQUIRED_DISTRO_FEATURES += "opengl polkit pulseaudio systemd x11"

SRC_URI += "file://0001-Add-meson-option-to-pass-sysroot.patch"
SRC_URI[archive.sha256sum] = "81792423019deda1d2a074c1ecf6a7e6ef3172c09cbe6f4d7995a4b5dea28312"

PACKAGECONFIG ??= "ibus ${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)}"
PACKAGECONFIG[cups] = ",,cups,cups"
PACKAGECONFIG[ibus] = "-Dibus=true, -Dibus=false, ibus"
PACKAGECONFIG[wayland] = "-Dwayland=true, -Dwayland=false, wayland"
PACKAGECONFIG[file-share] = ",,,gnome-user-share"
PACKAGECONFIG[media-share] = ",,,rygel-meta tumbler"

EXTRA_OEMESON += "-Doe_sysroot=${STAGING_DIR_HOST}"

export XDG_DATA_DIRS = "${STAGING_DATADIR}"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM:${PN} = "--system --no-create-home --user-group --home-dir ${sysconfdir}/polkit-1 polkitd"

do_install:append() {
    if [ -d ${D}${datadir}/polkit-1/rules.d ]; then
        chmod 700 ${D}${datadir}/polkit-1/rules.d
        chown polkitd:root ${D}${datadir}/polkit-1/rules.d
    fi
}

PACKAGE_DEBUG_SPLIT_STYLE = "debug-without-src"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
"

FILES:${PN}-dev += "${datadir}/gettext"

RDEPENDS:${PN} += "gsettings-desktop-schemas tecla system-config-printer cups-pk-helper"
