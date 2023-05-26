SUMMARY = "Tools for Flash-Friendly File System (F2FS)"
HOMEPAGE = "https://git.kernel.org/pub/scm/linux/kernel/git/jaegeuk/f2fs-tools.git"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=362b4b2594cd362b874a97718faa51d3"

# to provide libuuid
DEPENDS = "util-linux"

SRCREV = "06c027abc6153c4a97cba5317844e8dcaaee3cf7"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/jaegeuk/f2fs-tools.git;branch=master \
           file://0002-f2fs_io-Define-_FILE_OFFSET_BITS-64.patch \
           "
UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>\d+(\.\d+)+)"

S = "${WORKDIR}/git"

inherit pkgconfig autotools

BBCLASSEXTEND = "native"
