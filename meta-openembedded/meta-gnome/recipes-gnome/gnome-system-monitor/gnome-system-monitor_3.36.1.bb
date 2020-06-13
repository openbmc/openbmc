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

SRC_URI[archive.md5sum] = "b1443ab12c3b7bcca940d28754e5b948"
SRC_URI[archive.sha256sum] = "866b47ab0f4c75b0ec57d6300337a7373463aaad5df95eddfe5354c22be7bca1"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

RRECOMMENDS_${PN} = "adwaita-icon-theme"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
