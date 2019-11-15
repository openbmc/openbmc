SUMMARY = "Tools for Flash-Friendly File System (F2FS)"
HOMEPAGE = "https://git.kernel.org/pub/scm/linux/kernel/git/jaegeuk/f2fs-tools.git"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=362b4b2594cd362b874a97718faa51d3"

# to provide libuuid
DEPENDS = "util-linux"

# v1.13.0
SRCREV = "284f77f0075a16a2ad1f3b0fb89b7f64a1bc755d"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/jaegeuk/f2fs-tools.git \
           file://0001-f2fs-tools-Use-srcdir-prefix-to-denote-include-path.patch \
           "
S = "${WORKDIR}/git"

inherit pkgconfig autotools

BBCLASSEXTEND = "native"
