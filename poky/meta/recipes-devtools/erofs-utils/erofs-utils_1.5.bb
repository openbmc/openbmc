SUMMARY = "Tools for erofs filesystems"
# liberofs also available under Apache 2.0
LICENSE = "GPL-2.0-or-later"
SECTION = "base"
LIC_FILES_CHKSUM = "file://COPYING;md5=73001d804ea1e3d84365f652242cca20"
HOMEPAGE = "https://git.kernel.org/pub/scm/linux/kernel/git/xiang/erofs-utils.git/tree/README"

SRCREV = "a2821a66b42aee5430bccee82c280e38d1e9ab29"
SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/xiang/erofs-utils.git;branch=master;protocol=https \
           file://0001-configure-use-AC_SYS_LARGEFILE.patch \
           file://0002-erofs-replace-l-stat64-by-equivalent-l-stat.patch \
           file://0003-internal.h-Make-LFS-mandatory-for-all-usecases.patch \
           file://CVE-2023-33551.patch \
           file://CVE-2023-33552-1.patch \
           file://CVE-2023-33552-2.patch \
           file://CVE-2023-33552-3.patch \	
           "

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>(\d+(\.\d+)+))"

S = "${WORKDIR}/git"

DEPENDS = "util-linux-libuuid"

inherit pkgconfig autotools

PACKAGECONFIG ??= "lz4"
PACKAGECONFIG[lz4] = "--enable-lz4,--disable-lz4,lz4"

EXTRA_OECONF = "${PACKAGECONFIG_CONFARGS} --disable-fuse --enable-largefile"

CFLAGS:append:powerpc64le = " -D__SANE_USERSPACE_TYPES__"

BBCLASSEXTEND = "native nativesdk"
