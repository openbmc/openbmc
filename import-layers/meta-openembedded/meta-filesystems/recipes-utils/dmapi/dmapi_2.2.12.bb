SUMMARY = "Library functions to get attribute bits"
DESCRIPTION = "The Data Management API (DMAPI/XDSM) allows implementation \
               of hierarchical storage management software with no kernel \
               modifications as well as high-performance dump programs \
               without requiring "raw" access to the disk and knowledge \
               of filesystem structures.This interface is implemented by \
               the libdm library."

HOMEPAGE = "http://oss.sgi.com/projects/xfs"
SECTION = "base"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=1678edfe8de9be9564d23761ae2fa794"
DEPENDS = "xfsprogs"

SRC_URI = "ftp://oss.sgi.com/projects/xfs/cmd_tars/dmapi-${PV}.tar.gz \
	   file://remove-install-as-user.patch \
           file://dmapi_aarch64_configure_support.patch \
          "
SRC_URI[md5sum] = "cd825d4e141c16011367e0a0dd98c9c5"
SRC_URI[sha256sum] = "b18e34f47374f6adf7c164993c26df36986a009b86aa004ef9444102653aea69"

inherit autotools-brokensep

PARALLEL_MAKE = ""
EXTRA_OEMAKE += "LIBTOOL="${HOST_SYS}-libtool --tag=CC" V=1"

do_install () {
    export DIST_ROOT=${D}
    install -d ${D}${libdir}
    oe_runmake install install-dev PKG_DEVLIB_DIR=${libdir}
}
