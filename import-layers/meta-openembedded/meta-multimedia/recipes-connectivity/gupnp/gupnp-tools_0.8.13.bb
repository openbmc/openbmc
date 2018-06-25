SUMMARY = "Tools for GUPnP"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://src/network-light/main.c;beginline=1;endline=21;md5=2c39b3a000495dabd4932f231c7efed8"
DEPENDS = "gupnp gupnp-av gtk+3 intltool-native"
RRECOMMENDS_${PN} = "adwaita-icon-theme"

SRC_URI = "http://download.gnome.org/sources/${BPN}/0.8/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "212d85ebce89b32468b59e1164050d40"
SRC_URI[sha256sum] = "aa3decb9d532c0e2e505adc592f431fd81c0231ded2981129e87da13712479ed"
inherit distro_features_check autotools pkgconfig gettext

REQUIRED_DISTRO_FEATURES = "x11"
