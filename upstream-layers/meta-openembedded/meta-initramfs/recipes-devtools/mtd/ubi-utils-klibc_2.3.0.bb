SUMMARY = "UBI utils statically compiled against klibc"
DESCRIPTION = "Small sized tools from mtd-utils for use with initramfs."
SECTION = "base"
DEPENDS = "zlib e2fsprogs util-linux"
HOMEPAGE = "http://www.linux-mtd.infradead.org/"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://include/common.h;beginline=1;endline=17;md5=ba05b07912a44ea2bf81ce409380049c"

inherit autotools pkgconfig klibc

SRCREV = "4594fc1f4496a0ed55cabd31fbeba4e3fbf05602"
SRC_URI = "git://git.infraroot.at/mtd-utils.git;protocol=https;branch=master \
             file://0001-libmissing.h-fix-klibc-build-when-using-glibc-toolch.patch \
             file://0003-Makefile.am-only-build-ubi-utils.patch \
             file://0004-mtd-utils-common.h-no-features.h-for-klibc-builds.patch \
             file://0001-make-Add-compiler-includes-in-cflags.patch \
             "

# ubihealthd fails to build since 2.3.0
EXTRA_OECONF += "--without-tests --without-jffs --without-ubifs --disable-ubihealthd"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'xattr', d)}"
PACKAGECONFIG[xattr] = ",,acl,"
PACKAGECONFIG[lzo] = "--with-lzo,--without-lzo,lzo"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS} ${@bb.utils.contains('PACKAGECONFIG', 'xattr', '', '-DWITHOUT_XATTR', d)} -I${S}/include' 'BUILDDIR=${S}'"

do_install () {
	oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} MANDIR=${mandir} INCLUDEDIR=${includedir}
}

PACKAGES = "ubi-utils-klibc-dbg ubi-utils-klibc-doc"

PACKAGES =+ "mtdinfo-klibc ubiattach-klibc ubiblock-klibc ubicrc32-klibc ubidetach-klibc \
             ubiformat-klibc ubimkvol-klibc ubinfo-klibc ubinize-klibc ubirename-klibc \
             ubirmvol-klibc ubirsvol-klibc ubiscan-klibc ubiupdatevol-klibc"

FILES:mtdinfo-klibc = "${sbindir}/mtdinfo"
FILES:ubiattach-klibc = "${sbindir}/ubiattach"
FILES:ubiblock-klibc = "${sbindir}/ubiblock"
FILES:ubicrc32-klibc = "${sbindir}/ubicrc32"
FILES:ubidetach-klibc = "${sbindir}/ubidetach"
FILES:ubiformat-klibc = "${sbindir}/ubiformat"
FILES:ubimkvol-klibc = "${sbindir}/ubimkvol"
FILES:ubinfo-klibc = "${sbindir}/ubinfo"
FILES:ubinize-klibc = "${sbindir}/ubinize"
FILES:ubirename-klibc = "${sbindir}/ubirename"
FILES:ubirmvol-klibc = "${sbindir}/ubirmvol"
FILES:ubirsvol-klibc = "${sbindir}/ubirsvol"
FILES:ubiscan-klibc = "${sbindir}/ubiscan"
FILES:ubiupdatevol-klibc = "${sbindir}/ubiupdatevol"
