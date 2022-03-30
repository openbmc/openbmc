SUMMARY = "A fast and lightweight IDE"
HOMEPAGE = "http://www.geany.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd7b2c994af21d318bd2cd3b3f80c2d5"

DEPENDS = "gtk+3 libxml-parser-perl-native python3-docutils-native intltool-native"

inherit features_check autotools pkgconfig perlnative gettext mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://download.geany.org/${BP}.tar.bz2 \
           file://0001-configure-Upgrade-to-a-modern-Gettext.patch \
          "
SRC_URI[sha256sum] = "abff176e4d48bea35ee53037c49c82f90b6d4c23e69aed6e4a5ca8ccd3aad546"

FILES:${PN} += "${datadir}/icons"

EXTRA_OECONF = "--disable-html-docs"

RRECOMMENDS:${PN} += "source-code-pro-fonts"
