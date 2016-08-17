SUMMARY = "Gesture Recognition And Instantiation Library"

HOMEPAGE = "https://launchpad.net/grail"

LICENSE = "GPLv3 & LGPLv3"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02 \
    file://COPYING.GPL3;md5=d32239bcb673463ab874e80d47fae504 \
"

inherit autotools pkgconfig

DEPENDS = "frame"

SRC_URI = "https://launchpad.net/${BPN}/trunk/${PV}/+download/${BPN}-${PV}.tar.bz2"
SRC_URI[md5sum] = "2ac56af5f6f466b433c99ca12f34c34f"
SRC_URI[sha256sum] = "c26dced1b3f4317ecf6af36db0e90294d87e43966d56aecc4e97b65368ab78b9"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "--with-x11, --without-x11, libxi"

PACKAGE_BEFORE_PN += "${PN}-test"
FILES_${PN}-test = "${bindir}/grail-test*"
