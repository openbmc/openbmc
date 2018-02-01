SUMMARY = "Tools for Flash-Friendly File System (F2FS)"
HOMEPAGE = "http://sourceforge.net/projects/f2fs-tools/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=362b4b2594cd362b874a97718faa51d3"

# to provide libuuid
DEPENDS = "util-linux"

SRCREV = "1e7aedf99b85d16f94d1d8ad2fcf846403bb2174"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/jaegeuk/f2fs-tools.git \
    file://0001-Remove-AC_CHECK_FILE-for-cross-compilation.patch \
    file://0002-Fix-mkfs-out-of-tree-builds.patch"
S = "${WORKDIR}/git"

inherit pkgconfig autotools

BBCLASSEXTEND = "native"
