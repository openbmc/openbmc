SUMMARY = "General purpose cryptographic library based on the code from GnuPG"
DESCRIPTION = "A cryptography library developed as a separated module of GnuPG. \
It can also be used independently of GnuPG, but depends on its error-reporting \
library Libgpg-error."
HOMEPAGE = "http://directory.fsf.org/project/libgcrypt/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"
SECTION = "libs"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later & BSD-3-Clause"
LICENSE:${PN} = "LGPL-2.1-or-later & BSD-3-Clause"
LICENSE:${PN}-dev = "GPL-2.0-or-later & LGPL-2.1-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
                    file://LICENSES;md5=034b4e369944ad4b52a68368f1cf98b8 \
                    "

DEPENDS = "libgpg-error"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/libgcrypt/libgcrypt-${PV}.tar.bz2 \
           file://0001-libgcrypt-fix-m4-file-for-oe-core.patch \
           file://0004-tests-Makefile.am-fix-undefined-reference-to-pthread.patch \
           file://0001-tests-Fix-link-errors-for-t-thread-local.patch \
           file://no-native-gpg-error.patch \
           file://no-bench-slope.patch \
           file://run-ptest \
           "
SRC_URI[sha256sum] = "24e91c9123a46c54e8371f3a3a2502f1198f2893fbfbf59af95bc1c21499b00e"

BINCONFIG = "${bindir}/libgcrypt-config"

inherit autotools texinfo binconfig-disabled pkgconfig ptest

require recipes-support/gnupg/drop-unknown-suffix.inc

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
