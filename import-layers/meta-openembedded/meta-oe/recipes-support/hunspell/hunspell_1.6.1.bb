SUMMARY = "A spell checker and morphological analyzer library"
HOMEPAGE = "http://hunspell.github.io/"
LICENSE = "GPLv3 | LGPLv3"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
    file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02 \
"

SRC_URI = "https://github.com/${BPN}/${BPN}/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "0df1c18c1284f8625af5ae5a8d5c4cef"
SRC_URI[sha256sum] = "30f593733c50b794016bb03d31fd2a2071e4610c6fa4708e33edad2335102c49"

inherit autotools pkgconfig gettext

RDEPENDS_${PN} = "perl"

BBCLASSEXTEND = "native"
