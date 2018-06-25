SUMMARY = "Gnome system monitor"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = " \
    intltool-native \
    gnome-common-native \
    glib-2.0-native \
    gtkmm3 \
    libgtop \
    librsvg \
    libwnck3 \
"

inherit gnomebase distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI[archive.md5sum] = "fcd59867c07f8c4853b1e28d60cbc037"
SRC_URI[archive.sha256sum] = "f848a8c2ca5e164cf09d3a205dd49e4e4bf4b60d43b0969c10443eb519d0e6b3"
SRC_URI += "file://0001-help-remove-YELP-macro.patch"

RRECOMMENDS_${PN} = "adwaita-icon-theme"

FILES_${PN} += " \
    ${datadir}/icons \
    ${datadir}/dbus-1 \
    ${datadir}/gnome/autostart \
"

FILES_${PN}-doc += " \
    ${datadir}/omf \
    ${datadir}/gnome/help \
"
