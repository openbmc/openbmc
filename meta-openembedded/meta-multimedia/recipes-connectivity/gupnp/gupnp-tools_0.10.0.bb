SUMMARY = "Tools for GUPnP"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://src/network-light/main.c;beginline=1;endline=21;md5=2c39b3a000495dabd4932f231c7efed8"
DEPENDS = "gupnp gupnp-av gtk+3 glib-2.0-native intltool-native"
RRECOMMENDS_${PN} = "adwaita-icon-theme"

SRC_URI = "http://download.gnome.org/sources/${BPN}/0.10/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "8c66d4aca756478615761a26ed7c8dba"
SRC_URI[sha256sum] = "41da7ff5ba8e2425adcb64ca5e04c81f57ca20ec6fdb84923939fdad42c6a18d"
inherit features_check meson pkgconfig gettext gtk-icon-cache

REQUIRED_DISTRO_FEATURES = "x11"
