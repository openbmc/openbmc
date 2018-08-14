SUMMARY = "Frame handles simultaneous touches"

HOMEPAGE = "https://launchpad.net/frame"

LICENSE = "GPLv3 & LGPLv3"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02 \
    file://COPYING.GPL3;md5=d32239bcb673463ab874e80d47fae504 \
"

inherit autotools pkgconfig

SRC_URI = "https://launchpad.net/${BPN}/trunk/v${PV}/+download/${BPN}-${PV}.tar.gz \
           file://pessimizing-move.patch"
SRC_URI[md5sum] = "02baa941091c5d198cd1623b3ad36e68"
SRC_URI[sha256sum] = "cfb9ab52cdccd926f1822a457264d0014c7eb9f4600a72626063dd073b26256f"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)}"
PACKAGECONFIG[x11] = "--enable-x11, --disable-x11, libxi xext virtual/xserver"

PACKAGE_BEFORE_PN += "${PN}-test"
FILES_${PN}-test = "${bindir}/frame-test*"

SECURITY_CFLAGS = "${SECURITY_NO_PIE_CFLAGS}"
