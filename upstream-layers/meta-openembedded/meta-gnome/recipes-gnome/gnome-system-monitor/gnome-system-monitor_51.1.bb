SUMMARY = "Gnome system monitor"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = " \
    catch2 \
    libxml2-native \
    glib-2.0-native \
    glibmm-2.68 \
    gtkmm4 \
    gtk4 \
    libadwaita \
    libgtop \
    librsvg \
"

inherit gnomebase gsettings gnome-help itstool gtk-icon-cache features_check gettext

ANY_OF_DISTRO_FEATURES = "${GTK3DISTROFEATURES}"
REQUIRED_DISTRO_FEATURES = "opengl"

SRC_URI[archive.sha256sum] = "acf0a61b02b18d20e29cc36cd275ed48fe5e2beeaac99140b299e659cb6493c6"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

TARGET_LDFLAGS:append = " ${DEBUG_PREFIX_MAP}"

RRECOMMENDS:${PN} = "adwaita-icon-theme"

FILES:${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
