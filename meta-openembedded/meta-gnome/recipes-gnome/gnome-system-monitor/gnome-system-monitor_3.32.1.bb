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
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gsettings gnome-help itstool gtk-icon-cache features_check gettext upstream-version-is-even

REQUIRED_DISTRO_FEATURES = "x11 polkit"

SRC_URI[archive.md5sum] = "37356a1b8c20939adc6f47f739d2e49a"
SRC_URI[archive.sha256sum] = "48c131335091bd927862f40ef56400f997981df2acfc82abea662bf91a1ea4f1"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

RRECOMMENDS_${PN} = "adwaita-icon-theme"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
