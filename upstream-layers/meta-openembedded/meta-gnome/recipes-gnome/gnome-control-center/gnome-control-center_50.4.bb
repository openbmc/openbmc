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
SRC_URI[sha256sum] = "5856c73999bedf45e74f73bac3d61e2b5ec1689f8bcf1943737baeee8204fb11"

SRC_URI += "file://0001-Add-meson-option-to-pass-sysroot.patch"

UPSTREAM_CHECK_URI = "https://gitlab.gnome.org/GNOME/gnome-control-center/-/tags"
UPSTREAM_CHECK_REGEX = "(?P<pver>\d+(\.\d+)+)"

PACKAGECONFIG ??= "ibus"
PACKAGECONFIG[cups] = ",,cups,cups system-config-printer cups-pk-helper"
PACKAGECONFIG[ibus] = "-Dibus=true, -Dibus=false, ibus"
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
RCONFLICTS:${PN} += "gnome-online-accounts-gtk"
