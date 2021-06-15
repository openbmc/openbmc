SUMMARY = "A fast and lightweight IDE"
HOMEPAGE = "http://www.geany.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd7b2c994af21d318bd2cd3b3f80c2d5"

DEPENDS = "gtk+3 libxml-parser-perl-native python3-docutils-native intltool-native"

inherit features_check autotools pkgconfig perlnative gettext mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://download.geany.org/${BP}.tar.bz2 \
           file://0001-configure-Upgrade-to-a-modern-Gettext.patch \
          "
SRC_URI[sha256sum] = "18c5756444c1d8bcd737c8ecfd4ef0b3607c924fc02560d4e8b78f6121531a18"

FILES_${PN} += "${datadir}/icons"

EXTRA_OECONF = "--disable-html-docs"

RRECOMMENDS_${PN} += "source-code-pro-fonts"
