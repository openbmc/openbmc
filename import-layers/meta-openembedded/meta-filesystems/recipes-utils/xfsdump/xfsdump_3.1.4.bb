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

SRC_URI = "ftp://oss.sgi.com/projects/xfs/cmd_tars/${BPN}-${PV}.tar.gz \
	   file://remove-install-as-user.patch \
          "
SRC_URI[md5sum] = "a8b1761be5feb363131e7b506639ad4c"
SRC_URI[sha256sum] = "570eafd0721515bdd79cb0e295b701d49cdf81e71a0a0ff0df6d4c5cc1960943"

inherit autotools-brokensep

PARALLEL_MAKE = ""
PACKAGECONFIG ??= ""
PACKAGECONFIG[gettext] = "--enable-gettext=yes,--enable-gettext=no,gettext"

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
