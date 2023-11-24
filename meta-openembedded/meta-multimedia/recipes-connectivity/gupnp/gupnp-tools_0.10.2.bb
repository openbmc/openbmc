SUMMARY = "Tools for GUPnP"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://src/network-light/main.c;beginline=1;endline=21;md5=2c39b3a000495dabd4932f231c7efed8"

DEPENDS = "gupnp gupnp-av gtk+3 glib-2.0-native intltool-native"

inherit features_check meson pkgconfig gettext gtk-icon-cache

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "http://download.gnome.org/sources/${BPN}/0.10/${BPN}-${PV}.tar.xz \
    file://0001-build-Fix-wrap-file-syntax.patch \
"
SRC_URI[sha256sum] = "6de49ef4b375b8a164f74b766168b1184e0d28196b6b07a4f5341f08dfd85d6c"

RRECOMMENDS:${PN} = "adwaita-icon-theme"
