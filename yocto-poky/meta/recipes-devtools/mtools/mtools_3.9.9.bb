# mtools OE build file
# Copyright (C) 2004-2006, Advanced Micro Devices, Inc.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

SUMMARY = "Utilities to access MS-DOS disks without mounting them"
DESCRIPTION = "Mtools is a collection of utilities for accessing MS-DOS disks from Unix without mounting them."
HOMEPAGE = "http://www.gnu.org/software/mtools/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=92b58ec77696788ce278b044d2a8e9d3"
PR = "r6"

RDEPENDS_${PN} = "glibc-gconv-ibm850"
RRECOMMENDS_${PN} = "\
	glibc-gconv-ibm437 \
	glibc-gconv-ibm737 \
	glibc-gconv-ibm775 \
	glibc-gconv-ibm851 \
	glibc-gconv-ibm852 \
	glibc-gconv-ibm855 \
	glibc-gconv-ibm857 \
	glibc-gconv-ibm860 \
	glibc-gconv-ibm861 \
	glibc-gconv-ibm862 \
	glibc-gconv-ibm863 \
	glibc-gconv-ibm865 \
	glibc-gconv-ibm866 \
	glibc-gconv-ibm869 \
	"

#http://mtools.linux.lu/mtools-${PV}.tar.gz 
SRC_URI = "http://downloads.yoctoproject.org/mirror/sources/mtools-${PV}.tar.gz \
	file://mtools-makeinfo.patch \
	file://mtools.patch \
	file://no-x11.patch \
	file://fix-broken-lz.patch \
"

SRC_URI[md5sum] = "3e68b857b4e1f3a6521d1dfefbd30a36"
SRC_URI[sha256sum] = "af083a73425d664d4607ef6c6564fd9319a0e47ee7c105259a45356cb834690e"

S = "${WORKDIR}/mtools-${PV}"

inherit autotools texinfo

EXTRA_OECONF = "--without-x"

BBCLASSEXTEND = "native nativesdk"

PACKAGECONFIG ??= ""
PACKAGECONFIG[libbsd] = "ac_cv_lib_bsd_main=yes,ac_cv_lib_bsd_main=no,libbsd"

do_install_prepend () {
    # Create bindir to fix parallel installation issues
    mkdir -p ${D}/${bindir}
    mkdir -p ${D}/${datadir}
}
