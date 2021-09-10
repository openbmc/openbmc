SUMMARY = "UBI utils statically compiled against klibc"
DESCRIPTION = "Small sized tools from mtd-utils for use with initramfs."
SECTION = "base"
DEPENDS = "zlib lzo e2fsprogs util-linux"
HOMEPAGE = "http://www.linux-mtd.infradead.org/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://include/common.h;beginline=1;endline=17;md5=ba05b07912a44ea2bf81ce409380049c"

inherit autotools pkgconfig klibc

SRCREV = "64f61a9dc71b158c7084006cbce4ea23886f0b47"
SRC_URI = "git://git.infradead.org/mtd-utils.git \
             file://0001-libmissing.h-fix-klibc-build-when-using-glibc-toolch.patch \
             file://0002-Instead-of-doing-preprocessor-magic-just-output-off_.patch \
             file://0003-Makefile.am-only-build-ubi-utils.patch \
             file://0004-mtd-utils-common.h-no-features.h-for-klibc-builds.patch \
             file://0005-common.h-replace-getline-with-fgets.patch \
             file://0001-make-Add-compiler-includes-in-cflags.patch \
             "

S = "${WORKDIR}/git"

EXTRA_OECONF += "--disable-tests --without-jffs --without-ubifs"

PACKAGECONFIG ?= "${@bb.utils.filter('DISTRO_FEATURES', 'xattr', d)}"
PACKAGECONFIG[xattr] = ",,acl,"

EXTRA_OEMAKE = "'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS} ${@bb.utils.contains('PACKAGECONFIG', 'xattr', '', '-DWITHOUT_XATTR', d)} -I${S}/include' 'BUILDDIR=${S}'"

do_install () {
	oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} MANDIR=${mandir} INCLUDEDIR=${includedir}
}

PACKAGES = "ubi-utils-klibc-dbg ubi-utils-klibc-doc"

PACKAGES =+ "mtdinfo-klibc ubiattach-klibc ubiblock-klibc ubicrc32-klibc ubidetach-klibc \
             ubiformat-klibc ubimkvol-klibc ubinfo-klibc ubinize-klibc ubirename-klibc \
             ubirmvol-klibc ubirsvol-klibc ubiupdatevol-klibc"

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
FILES:ubiupdatevol-klibc = "${sbindir}/ubiupdatevol"
