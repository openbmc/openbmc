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
SRC_URI[sha256sum] = "6b96a8844463300c10b9692a0a5edad8236eec9e84342f575f83d4fc89331228"

FILES:${PN} += "${datadir}/icons"

EXTRA_OECONF = "--disable-html-docs"

RRECOMMENDS:${PN} += "source-code-pro-fonts"
