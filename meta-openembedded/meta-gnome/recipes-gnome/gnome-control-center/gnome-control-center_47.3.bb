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
    ${@' libxslt-native docbook-xsl-stylesheets-native' if d.getVar('GIDOCGEN_ENABLED') == 'True' else ''} \
"

inherit gtk-icon-cache pkgconfig gnomebase gsettings gettext gi-docgen upstream-version-is-even bash-completion features_check

REQUIRED_DISTRO_FEATURES += "opengl polkit pulseaudio systemd x11"

SRC_URI += "file://0001-Add-meson-option-to-pass-sysroot.patch"
SRC_URI[archive.sha256sum] = "78381d5a7f1d5b297c9a19611145ee5e0584f06ac575ed08ad070a0c07bbeaa2"

PACKAGECONFIG ??= "ibus ${@bb.utils.filter('DISTRO_FEATURES', 'wayland', d)}"
PACKAGECONFIG[cups] = ",,cups,cups system-config-printer cups-pk-helper"
PACKAGECONFIG[ibus] = "-Dibus=true, -Dibus=false, ibus"
PACKAGECONFIG[wayland] = "-Dwayland=true, -Dwayland=false, wayland"
PACKAGECONFIG[file-share] = ",,,gnome-user-share"
PACKAGECONFIG[media-share] = ",,,rygel-meta tumbler"
PACKAGECONFIG[malcontent] = "-Dmalcontent=true,-Dmalcontent=false,malcontent,malcontent-ui"

EXTRA_OEMESON += "-Doe_sysroot=${STAGING_DIR_HOST}"
GIDOCGEN_MESON_OPTION = 'documentation'

export XDG_DATA_DIRS = "${STAGING_DATADIR}"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
"

FILES:${PN}-dev += "${datadir}/gettext"

RDEPENDS:${PN} += "gsettings-desktop-schemas tecla"
