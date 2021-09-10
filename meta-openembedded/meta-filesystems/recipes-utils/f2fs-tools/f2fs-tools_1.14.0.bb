SUMMARY = "Tools for Flash-Friendly File System (F2FS)"
HOMEPAGE = "https://git.kernel.org/pub/scm/linux/kernel/git/jaegeuk/f2fs-tools.git"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=362b4b2594cd362b874a97718faa51d3"

# to provide libuuid
DEPENDS = "util-linux"

SRCREV = "d41dcbdf46dc3841cd0a0507e6573e38cb6c55bb"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/jaegeuk/f2fs-tools.git \
           "
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit pkgconfig autotools

BBCLASSEXTEND = "native"
