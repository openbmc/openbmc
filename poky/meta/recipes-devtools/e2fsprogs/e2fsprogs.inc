SUMMARY = "Ext2 Filesystem Utilities"
DESCRIPTION = "The Ext2 Filesystem Utilities (e2fsprogs) contain all of the standard utilities for creating, \
fixing, configuring , and debugging ext2 filesystems."
HOMEPAGE = "http://e2fsprogs.sourceforge.net/"

LICENSE = "GPLv2 & LGPLv2 & BSD & MIT"
LICENSE_e2fsprogs-e2fsck = "GPLv2"
LICENSE_e2fsprogs-mke2fs = "GPLv2"
LICENSE_e2fsprogs-fsck = "GPLv2"
LICENSE_e2fsprogs-tune2fs = "GPLv2"
LICENSE_e2fsprogs-badblocks = "GPLv2"
LIC_FILES_CHKSUM = "file://NOTICE;md5=d50be0580c0b0a7fbc7a4830bbe6c12b \
                      file://lib/ext2fs/ext2fs.h;beginline=1;endline=9;md5=596a8dedcb4e731c6b21c7a46fba6bef \
                      file://lib/e2p/e2p.h;beginline=1;endline=7;md5=8a74ade8f9d65095d70ef2d4bf48e36a \
                      file://lib/uuid/uuid.h.in;beginline=1;endline=32;md5=dbb8079e114a5f841934b99e59c8820a \
                      file://lib/uuid/COPYING;md5=58dcd8452651fc8b07d1f65ce07ca8af \
                      file://lib/et/et_name.c;beginline=1;endline=11;md5=ead236447dac7b980dbc5b4804d8c836 \
                      file://lib/ss/ss.h;beginline=1;endline=20;md5=6e89ad47da6e75fecd2b5e0e81e1d4a6"
SECTION = "base"
DEPENDS = "util-linux attr"

SRC_URI = "git://git.kernel.org/pub/scm/fs/ext2/e2fsprogs.git"
S = "${WORKDIR}/git"

inherit autotools gettext texinfo pkgconfig multilib_header update-alternatives ptest

BBCLASSEXTEND = "native nativesdk"
