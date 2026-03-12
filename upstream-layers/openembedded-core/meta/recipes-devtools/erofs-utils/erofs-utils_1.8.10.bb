SUMMARY = "Tools for erofs filesystems"
# liberofs also available under Apache 2.0
LICENSE = "GPL-2.0-or-later"
SECTION = "base"
LIC_FILES_CHKSUM = "file://COPYING;md5=73001d804ea1e3d84365f652242cca20"
HOMEPAGE = "https://git.kernel.org/pub/scm/linux/kernel/git/xiang/erofs-utils.git/tree/README"

SRCREV = "51b5939b5f783221310d25146e6a2019ba8129b6"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/xiang/erofs-utils.git;branch=master;protocol=https"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>(\d+(\.\d+)+))"

DEPENDS = "util-linux-libuuid"

inherit pkgconfig autotools

PACKAGECONFIG ??= "lz4 zlib"
PACKAGECONFIG[lz4] = "--enable-lz4,--disable-lz4,lz4"
PACKAGECONFIG[zlib] = "--with-zlib,--without-zlib,zlib"

EXTRA_OECONF = "${PACKAGECONFIG_CONFARGS} --disable-fuse"

CFLAGS:append:powerpc64le = " -D__SANE_USERSPACE_TYPES__"

BBCLASSEXTEND = "native nativesdk"
