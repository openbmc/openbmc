SUMMARY = "Frame handles simultaneous touches"

HOMEPAGE = "https://launchpad.net/frame"

LICENSE = "GPL-3.0-only & LGPL-3.0-only"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02 \
    file://COPYING.GPL3;md5=d32239bcb673463ab874e80d47fae504 \
"

inherit autotools pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'opengl', '', d)}"

SRC_URI = "https://launchpad.net/${BPN}/trunk/v${PV}/+download/${BPN}-${PV}.tar.gz \
           file://pessimizing-move.patch"

UPSTREAM_CHECK_URI = "https://launchpad.net/frame/trunk"

SRC_URI[sha256sum] = "cfb9ab52cdccd926f1822a457264d0014c7eb9f4600a72626063dd073b26256f"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "--enable-x11, --disable-x11, libxi xext virtual/xserver"

PACKAGE_BEFORE_PN += "${PN}-test"
FILES:${PN}-test = "${bindir}/frame-test*"

SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
