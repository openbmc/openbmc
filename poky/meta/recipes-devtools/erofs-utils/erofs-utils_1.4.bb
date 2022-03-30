SUMMARY = "Tools for erofs filesystems"
LICENSE = "GPL-2.0-or-later"
SECTION = "base"
LIC_FILES_CHKSUM = "file://COPYING;md5=94fa01670a2a8f2d3ab2de15004e0848"
HOMEPAGE = "https://git.kernel.org/pub/scm/linux/kernel/git/xiang/erofs-utils.git/tree/README"

SRCREV = "ee97fe5fb77c737df0f77d92ab0d92edd3a11be6"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/xiang/erofs-utils.git;branch=master \
           file://0001-fsck-main.c-add-missing-include.patch \
           "

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>(\d+(\.\d+)+))"

S = "${WORKDIR}/git"

DEPENDS = "util-linux-libuuid"

inherit pkgconfig autotools

PACKAGECONFIG ??= "lz4"
PACKAGECONFIG[lz4] = "--enable-lz4,--disable-lz4,lz4"

EXTRA_OECONF = "${PACKAGECONFIG_CONFARGS} --disable-fuse"

CFLAGS:append:powerpc64le = " -D__SANE_USERSPACE_TYPES__"

BBCLASSEXTEND = "native nativesdk"
