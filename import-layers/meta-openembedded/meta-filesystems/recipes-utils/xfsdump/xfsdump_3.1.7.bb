SUMMARY = "XFS Filesystem Dump Utility"
DESCRIPTION = "The xfsdump package contains xfsdump, xfsrestore and a \
               number of other utilities for administering XFS filesystems.\
               xfsdump examines files in a filesystem, determines which \
               need to be backed up, and copies those files to a \
               specified disk, tape or other storage medium."
HOMEPAGE = "http://oss.sgi.com/projects/xfs"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=15c832894d10ddd00dfcf57bee490ecc"
DEPENDS = "xfsprogs attr"

SRC_URI = "https://www.kernel.org/pub/linux/utils/fs/xfs/xfsdump/${BP}.tar.xz \
           file://remove-install-as-user.patch \
           file://0001-Include-fcntl.h-for-O_EXCL.patch \
           file://0002-Replace-__uint32_t-with-uint32_t.patch \
           file://0003-replace-use-of-SIGCLD-with-SIGCHLD.patch \
           file://0004-include-limit.h-for-PATH_MAX.patch \
           file://0005-include-sys-types.h-for-u_int32_t-in-attr-attributes.patch \
           file://0001-xfsdump-Use-c99-defined-int64_t-instead-of-__int64_t.patch \
           "
SRC_URI[md5sum] = "c6e91f2ac8b76c796db2d236f5ca5947"
SRC_URI[sha256sum] = "99e6d4df257ebc6d29ca9e970ca20672c2ea03481ad949bc68f98de3e4d56dce"

inherit autotools-brokensep

PARALLEL_MAKE = ""
PACKAGECONFIG ??= ""
PACKAGECONFIG[gettext] = "--enable-gettext=yes,--enable-gettext=no,gettext"

CFLAGS += "-D_FILE_OFFSET_BITS=64"

EXTRA_OEMAKE += "'LIBTOOL=${HOST_SYS}-libtool' V=1"

do_configure () {
    export DEBUG="-DNDEBUG"
    oe_runconf
}

do_install () {
    export DIST_ROOT=${D}
    oe_runmake install
    oe_runmake install-dev
}
