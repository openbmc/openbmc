SUMMARY = "A fast and lightweight IDE"
HOMEPAGE = "http://www.geany.org/"
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=bd7b2c994af21d318bd2cd3b3f80c2d5"

DEPENDS = "gtk+3 libxml-parser-perl-native python3-docutils-native intltool-native"

inherit features_check autotools pkgconfig perlnative gettext mime-xdg

REQUIRED_DISTRO_FEATURES = "x11"

SRC_URI = "https://download.geany.org/${BP}.tar.bz2 \
           file://0001-configure-Upgrade-to-a-modern-Gettext.patch \
           file://geany-2.0-gcc15.patch \
          "
SRC_URI[sha256sum] = "565b4cd2f0311c1e3a167ec71c4a32dba642e0fe554ae5bb6b8177b7a74ccc92"

FILES:${PN} += "${datadir}/icons"

EXTRA_OECONF = "--disable-html-docs"

RRECOMMENDS:${PN} += "source-code-pro-fonts"
