SUMMARY = "General purpose cryptographic library based on the code from GnuPG"
DESCRIPTION = "A cryptography library developed as a separated module of GnuPG. \
It can also be used independently of GnuPG, but depends on its error-reporting \
library Libgpg-error."
HOMEPAGE = "http://directory.fsf.org/project/libgcrypt/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"
SECTION = "libs"

LICENSE = "BSD-3-Clause AND GPL-2.0-or-later AND LGPL-2.1-or-later"
LICENSE:${PN} = "BSD-3-Clause AND LGPL-2.1-or-later"
LICENSE:${PN}-dev = "GPL-2.0-or-later AND LGPL-2.1-or-later"

LIC_FILES_CHKSUM = "file://COPYING;md5=570a9b3749dd0463a1778803b12a6dce \
                    file://COPYING.LIB;md5=4bf661c1e3793e55c8d1051bc5e0ae21 \
                    file://LICENSES;md5=47e346a71a2593d19f741408407fe91f \
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
SRC_URI[sha256sum] = "98d1b0b3202d2b03fa754a35aa3cbbfcf526a3260d8d2ee213748001b1043006"

BINCONFIG = "${bindir}/libgcrypt-config"

inherit autotools texinfo binconfig-disabled pkgconfig ptest upstream-stable-release-point

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
