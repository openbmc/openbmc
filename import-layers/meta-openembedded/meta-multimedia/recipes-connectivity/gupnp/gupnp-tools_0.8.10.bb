SUMMARY = "Tools for GUPnP"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://src/network-light/main.c;beginline=1;endline=21;md5=2c39b3a000495dabd4932f231c7efed8"
DEPENDS = "gupnp gupnp-av gtk+3"
RRECOMMENDS_${PN} = "adwaita-icon-theme"

SRC_URI = "http://download.gnome.org/sources/${BPN}/0.8/${BPN}-${PV}.tar.xz"
SRC_URI[md5sum] = "ad8423d036e98bc6f877528d6b846f6a"
SRC_URI[sha256sum] = "592c53289ff1cd70e676405c56ca87b28d2da37a385d34a3bb9b944ba9108d17"

inherit autotools pkgconfig
