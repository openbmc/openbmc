SUMMARY = "UBI utils statically compiled against klibc"
DESCRIPTION = "Small sized tools from mtd-utils for use with initramfs."
SECTION = "base"
DEPENDS = "zlib lzo e2fsprogs util-linux"
HOMEPAGE = "http://www.linux-mtd.infradead.org/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://include/common.h;beginline=1;endline=17;md5=ba05b07912a44ea2bf81ce409380049c"

inherit klibc

SRCREV = "aea36417067dade75192bafa03af70b6eb2677b1"
SRC_URI = "git://git.infradead.org/mtd-utils.git \
             file://0001-Makefile-only-build-ubi-utils.patch \
             file://0002-common.mk-for-klibc-CC-is-klcc.patch \
             file://0003-libubi.c-add-klibc-specific-fixes.patch \
             file://0004-common.h-klibc-fixes-1.patch \
             file://0005-common.h-klibc-fixes-2.patch \
             file://0006-libiniparser-remove-unused-function-needing-float.patch \
             "

S = "${WORKDIR}/git/"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS} -I${S}include -DWITHOUT_XATTR' 'BUILDDIR=${S}'"

do_install () {
	oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} MANDIR=${mandir} INCLUDEDIR=${includedir}
}

PACKAGES = "ubi-utils-klibc-dbg"

PACKAGES =+ "mtdinfo-klibc ubiattach-klibc ubiblock-klibc ubicrc32-klibc ubidetach-klibc \
             ubiformat-klibc ubimkvol-klibc ubinfo-klibc ubinize-klibc ubirename-klibc \
             ubirmvol-klibc ubirsvol-klibc ubiupdatevol-klibc"

FILES_mtdinfo-klibc = "${sbindir}/mtdinfo"
FILES_ubiattach-klibc = "${sbindir}/ubiattach"
FILES_ubiblock-klibc = "${sbindir}/ubiblock"
FILES_ubicrc32-klibc = "${sbindir}/ubicrc32"
FILES_ubidetach-klibc = "${sbindir}/ubidetach"
FILES_ubiformat-klibc = "${sbindir}/ubiformat"
FILES_ubimkvol-klibc = "${sbindir}/ubimkvol"
FILES_ubinfo-klibc = "${sbindir}/ubinfo"
FILES_ubinize-klibc = "${sbindir}/ubinize"
FILES_ubirename-klibc = "${sbindir}/ubirename"
FILES_ubirmvol-klibc = "${sbindir}/ubirmvol"
FILES_ubirsvol-klibc = "${sbindir}/ubirsvol"
FILES_ubiupdatevol-klibc = "${sbindir}/ubiupdatevol"
