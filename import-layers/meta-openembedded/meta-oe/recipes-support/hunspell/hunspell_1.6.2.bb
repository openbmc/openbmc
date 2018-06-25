SUMMARY = "A spell checker and morphological analyzer library"
HOMEPAGE = "http://hunspell.github.io/"
LICENSE = "GPLv3 | LGPLv3"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
    file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02 \
"

SRC_URI = "https://github.com/${BPN}/${BPN}/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "1a54504cb470aafa6530bb372a10dc04"
SRC_URI[sha256sum] = "3cd9ceb062fe5814f668e4f22b2fa6e3ba0b339b921739541ce180cac4d6f4c4"

inherit autotools pkgconfig gettext

RDEPENDS_${PN} = "perl"

BBCLASSEXTEND = "native"
