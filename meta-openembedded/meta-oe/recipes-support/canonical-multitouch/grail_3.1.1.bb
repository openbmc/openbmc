SUMMARY = "Gesture Recognition And Instantiation Library"

HOMEPAGE = "https://launchpad.net/grail"

LICENSE = "GPL-3.0-only & LGPL-3.0-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02 \
    file://COPYING.GPL3;md5=d32239bcb673463ab874e80d47fae504 \
"

inherit autotools pkgconfig features_check

DEPENDS = "frame"
CXXFLAGS:append:toolchain-clang = " -Wno-pessimizing-move"
SRC_URI = "https://launchpad.net/${BPN}/trunk/${PV}/+download/${BPN}-${PV}.tar.bz2"

UPSTREAM_CHECK_URI = "https://launchpad.net/grail/trunk"

SRC_URI[md5sum] = "0df1b3ec6167920f310e2effe6e2ad44"
SRC_URI[sha256sum] = "5eed1f650f042481daa3a2de5e7d43261fe343b2a1b1e240f3b7fc26572c9df3"

REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'opengl', '', d)}"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "--with-x11, --without-x11, libxi"

PACKAGE_BEFORE_PN += "${PN}-test"
FILES:${PN}-test = "${bindir}/grail-test*"
SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
