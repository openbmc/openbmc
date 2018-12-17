SUMMARY = "Tools for Flash-Friendly File System (F2FS)"
HOMEPAGE = "http://sourceforge.net/projects/f2fs-tools/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=362b4b2594cd362b874a97718faa51d3"

# to provide libuuid
DEPENDS = "util-linux"

# v1.11.0
SRCREV = "b98fab3666e498c2e27ad9dcda6874c9b683f06b"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/jaegeuk/f2fs-tools.git \
           file://0001-Remove-AC_CHECK_FILE-for-cross-compilation.patch \
           file://0001-f2fs-tools-Use-srcdir-prefix-to-denote-include-path.patch \
           "
S = "${WORKDIR}/git"

inherit pkgconfig autotools

BBCLASSEXTEND = "native"
