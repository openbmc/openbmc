SUMMARY = "Gnome system monitor"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = " \
    gnome-common-native \
    libxml2-native \
    glib-2.0-native \
    gtkmm3 \
    libgtop \
    librsvg \
    polkit \
    libhandy \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gnome-help itstool gtk-icon-cache features_check gettext

REQUIRED_DISTRO_FEATURES = "x11 polkit"

def gnome_verdir(v):
    return oe.utils.trim_version(v, 1)

SRC_URI[archive.sha256sum] = "c7fc44c61949b794b0289968ebbbcc0c15f8cfc3d9e45bfaa81ed45c12139e5f"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

RRECOMMENDS:${PN} = "adwaita-icon-theme"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
