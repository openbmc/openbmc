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
    blueprint-compiler-native \
"

inherit gtk-icon-cache pkgconfig gnomebase gsettings gettext gi-docgen upstream-version-is-even bash-completion features_check

REQUIRED_DISTRO_FEATURES += "opengl polkit pulseaudio systemd"

SRC_URI = "https://download.gnome.org/sources/gnome-control-center/${@oe.utils.trim_version('${PV}', 1)}/gnome-control-center-${PV}.tar.xz"
SRC_URI[sha256sum] = "c23ae220d6c1237d285925de7801e0e36338b9cc1a8bb51c2e37e715e6b503ad"

SRC_URI += "file://0001-Add-meson-option-to-pass-sysroot.patch"

PACKAGECONFIG ??= "ibus ${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[cups] = ",,cups,cups system-config-printer cups-pk-helper"
PACKAGECONFIG[ibus] = "-Dibus=true, -Dibus=false, ibus"
PACKAGECONFIG[x11] = "-Dx11=true, -Dx11=false, virtual/libx11"
PACKAGECONFIG[file-share] = ",,,gnome-user-share"
PACKAGECONFIG[media-share] = ",,,rygel-meta tumbler"
PACKAGECONFIG[malcontent] = "-Dmalcontent=true,-Dmalcontent=false,malcontent,malcontent-ui"
PACKAGECONFIG[power-profiles] = ",,,power-profiles-daemon"

EXTRA_OEMESON += "-Doe_sysroot=${STAGING_DIR_HOST}"
GIDOCGEN_MESON_OPTION = 'documentation'

export XDG_DATA_DIRS = "${STAGING_DATADIR}"
export GI_TYPELIB_PATH = "${RECIPE_SYSROOT}${libdir}/girepository-1.0"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/gnome-shell \
    ${datadir}/metainfo \
"

FILES:${PN}-dev += "${datadir}/gettext"

RDEPENDS:${PN} += "gsettings-desktop-schemas tecla"
