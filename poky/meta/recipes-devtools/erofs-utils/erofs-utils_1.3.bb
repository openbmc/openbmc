SUMMARY = "Tools for erofs filesystems"
LICENSE = "GPLv2+"
SECTION = "base"
LIC_FILES_CHKSUM = "file://COPYING;md5=94fa01670a2a8f2d3ab2de15004e0848"
HOMEPAGE = "https://git.kernel.org/pub/scm/linux/kernel/git/xiang/erofs-utils.git/tree/README"

SRCREV = "2cd522105ea771ec30b269cd4c57e2265a4d6349"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/xiang/erofs-utils.git"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>(\d+(\.\d+)+))"

S = "${WORKDIR}/git"

DEPENDS = "util-linux-libuuid"

inherit pkgconfig autotools

PACKAGECONFIG ??= "lz4"
PACKAGECONFIG[lz4] = "--enable-lz4,--disable-lz4,lz4"

EXTRA_OECONF = "${PACKAGECONFIG_CONFARGS} --disable-fuse"

BBCLASSEXTEND = "native nativesdk"
