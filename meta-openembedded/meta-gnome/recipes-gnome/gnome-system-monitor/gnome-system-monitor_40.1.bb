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

SRC_URI[archive.sha256sum] = "fdb30f4367907aab86d7c2bb76a7b773ba850b765c9666a39f42abfe22691d1a"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

RRECOMMENDS:${PN} = "adwaita-icon-theme"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
