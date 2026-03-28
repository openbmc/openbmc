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

SRC_URI[archive.sha256sum] = "a4138aa754b4584c46de91fad1d685e27b12bc2457de761863b6be02d84c4862"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

TARGET_LDFLAGS:append = " ${DEBUG_PREFIX_MAP}"

RRECOMMENDS:${PN} = "adwaita-icon-theme"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
