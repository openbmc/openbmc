# dosfstools OE build file
# Copyright (C) 2004-2006, Advanced Micro Devices, Inc.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)
SUMMARY = "DOS FAT Filesystem Utilities"
HOMEPAGE = "https://github.com/dosfstools/dosfstools"

SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://mkdosfs/COPYING;md5=cbe67f08d6883bff587f615f0cc81aa8"
PR = "r5"

SRC_URI = "http://pkgs.fedoraproject.org/repo/pkgs/${BPN}/${BP}.src.tar.gz/407d405ade410f7597d364ab5dc8c9f6/${BP}.src.tar.gz \
           file://mkdosfs-bootcode.patch \
           file://mkdosfs-dir.patch \
           file://alignment_hack.patch \
           file://msdos_fat12_undefined.patch \
           file://dosfstools-msdos_fs-types.patch \
           file://include-linux-types.patch \
           file://nofat32_autoselect.patch \
           file://fix_populated_dosfs_creation.patch \
           file://0001-Include-fcntl.h-for-getting-loff_t-definition.patch \
"

SRC_URI[md5sum] = "407d405ade410f7597d364ab5dc8c9f6"
SRC_URI[sha256sum] = "0eac6d12388b3d9ed78684529c1b0d9346fa2abbe406c4d4a3eb5a023c98a484"

CFLAGS += "-D_GNU_SOURCE ${@bb.utils.contains('DISTRO_FEATURES', 'largefile', '-D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64', '', d)}"

EXTRA_OEMAKE = "CC='${CC}' CFLAGS='${CFLAGS}' LDFLAGS='${LDFLAGS}'"

do_install () {
	oe_runmake "PREFIX=${D}" "SBINDIR=${D}${base_sbindir}" \
		   "MANDIR=${D}${mandir}/man8" install
}
