SUMMARY = "Gnome system monitor"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = " \
    itstool-native \
    gnome-common-native \
    libxml2-native \
    glib-2.0-native \
    gtkmm3 \
    libgtop \
    librsvg \
    polkit \
"

GNOMEBASEBUILDCLASS = "meson"

inherit gnomebase gconf gtk-icon-cache distro_features_check gettext

REQUIRED_DISTRO_FEATURES = "x11 polkit"

SRC_URI[archive.md5sum] = "6c2c5c5bf8f15d3e6985faacbd3055dd"
SRC_URI[archive.sha256sum] = "af00c4a42dd1db17d9abe88edb11c7751b20982db1a8a2a467bab206fd8a77e3"
SRC_URI += "file://0001-Do-not-build-help-we-do-not-have-yelp-yet.patch"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)}"
PACKAGECONFIG[systemd] = "-Dsystemd=true, -Dsystemd=false, systemd"

RRECOMMENDS_${PN} = "adwaita-icon-theme"

FILES_${PN} += " \
    ${datadir}/dbus-1 \
    ${datadir}/metainfo \
"
