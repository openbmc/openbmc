SUMMARY = "A fast and lightweight IDE"
HOMEPAGE = "http://www.geany.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd7b2c994af21d318bd2cd3b3f80c2d5"

DEPENDS = "gtk+3 libxml-parser-perl-native python3-docutils-native intltool-native"

inherit features_check autotools pkgconfig perlnative gettext mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://download.geany.org/${BP}.tar.bz2"
SRC_URI[md5sum] = "53216a43345e2b6dbefa02ac24885753"
SRC_URI[sha256sum] = "9184dd3dd40b7b84fca70083284bb9dbf2ee8022bf2be066bdc36592d909d53e"

FILES_${PN} += "${datadir}/icons"

EXTRA_OECONF = "--disable-html-docs"
