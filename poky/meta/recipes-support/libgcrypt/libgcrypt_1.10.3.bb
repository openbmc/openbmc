SUMMARY = "General purpose cryptographic library based on the code from GnuPG"
DESCRIPTION = "A cryptography library developed as a separated module of GnuPG. \
It can also be used independently of GnuPG, but depends on its error-reporting \
library Libgpg-error."
HOMEPAGE = "http://directory.fsf.org/project/libgcrypt/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"
SECTION = "libs"

# helper program gcryptrnd and getrandom are under GPL, rest LGPL
LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LICENSE:${PN} = "LGPL-2.1-or-later"
LICENSE:${PN}-dev = "GPL-2.0-or-later & LGPL-2.1-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
                    file://COPYING.LIB;md5=bbb461211a33b134d42ed5ee802b37ff \
                    file://LICENSES;md5=ef545b6cc717747072616519a1256d69 \
                    "

DEPENDS = "libgpg-error"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/libgcrypt/libgcrypt-${PV}.tar.bz2 \
           file://0001-libgcrypt-fix-m4-file-for-oe-core.patch \
           file://0004-tests-Makefile.am-fix-undefined-reference-to-pthread.patch \
           file://no-native-gpg-error.patch \
           file://no-bench-slope.patch \
           file://run-ptest \
           file://0001-Fix-building-error-with-O2-in-sysroot-path.patch \
           "
SRC_URI[sha256sum] = "8b0870897ac5ac67ded568dcfadf45969cfa8a6beb0fd60af2a9eadc2a3272aa"

BINCONFIG = "${bindir}/libgcrypt-config"

inherit autotools texinfo binconfig-disabled pkgconfig ptest

EXTRA_OECONF = "--disable-asm"
EXTRA_OEMAKE:class-target = "LIBTOOLFLAGS='--tag=CC'"

PACKAGECONFIG ??= "capabilities"
PACKAGECONFIG[capabilities] = "--with-capabilities,--without-capabilities,libcap"

do_configure:prepend () {
	# Else this could be used in preference to the one in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
}

do_install_ptest() {
    cd tests
    oe_runmake testdrv-build testdrv
    install testdrv $(srcdir=${S}/tests ./testdrv-build --files | sort | uniq) ${D}${PTEST_PATH}
}

FILES:${PN}-dev += "${bindir}/hmac256 ${bindir}/dumpsexp"

BBCLASSEXTEND = "native nativesdk"
