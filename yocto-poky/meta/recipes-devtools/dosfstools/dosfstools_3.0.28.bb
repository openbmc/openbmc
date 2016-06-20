# dosfstools OE build file
# Copyright (C) 2004-2006, Advanced Micro Devices, Inc.  All Rights Reserved
# Copyright (C) 2015, SÃ¶ren Brinkmann <soeren.brinkmann@gmail>  All Rights Reserved
# Released under the MIT license (see packages/COPYING)
SUMMARY = "DOS FAT Filesystem Utilities"
HOMEPAGE = "https://github.com/dosfstools/dosfstools"

SECTION = "base"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "https://github.com/dosfstools/dosfstools/releases/download/v3.0.28/dosfstools-3.0.28.tar.xz \
           file://largefile.patch \
           file://0001-mkfs.fat-fix-incorrect-int-type.patch \
          "
SRC_URI[md5sum] = "6a047a6c65186b9ebb1853709adb36db"
SRC_URI[sha256sum] = "ee95913044ecf2719b63ea11212917649709a6e53209a72d622135aaa8517ee2"

UPSTREAM_CHECK_URI = "https://github.com/dosfstools/dosfstools/releases"

CFLAGS += "-D_GNU_SOURCE ${@bb.utils.contains('DISTRO_FEATURES', 'largefile', '-D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64', '', d)}"

FILES_${PN} = "${base_sbindir}"
FILES_${PN}-doc = "${mandir} ${docdir}"

do_install () {
	oe_runmake "PREFIX=${D}${prefix}" "SBINDIR=${D}${base_sbindir}" \
		   "MANDIR=${D}${mandir}" "DOCDIR=${D}${docdir}" install
}

BBCLASSEXTEND = "native"
