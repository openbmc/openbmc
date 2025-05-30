SUMMARY = "Gnome system monitor"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = " \
    catch2 \
    gnome-common-native \
    libxml2-native \
    glib-2.0-native \
    glibmm-2.68 \
    gtkmm4 \
    gtk4 \
    libadwaita \
    libgtop \
    librsvg \
    polkit \
    libhandy \
"


inherit gnomebase gsettings gnome-help itstool gtk-icon-cache features_check gettext

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "polkit"

SRC_URI[archive.sha256sum] = "133c3a6e286366e659fd2d80582409a7bd5edae456e63c4f69ccf66ed6fc6638"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

RRECOMMENDS:${PN} = "adwaita-icon-theme"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
