SUMMARY = "Small library that defines common error values for all GnuPG components"
DESCRIPTION = "Contains common error codes and error handling functions used by GnuPG, Libgcrypt, GPGME and more packages. "
HOMEPAGE = "http://www.gnupg.org/related_software/libgpg-error/"
BUGTRACKER = "https://bugs.g10code.com/gnupg/index"

LICENSE = "GPL-2.0-or-later & LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/gpg-error.h.in;beginline=2;endline=18;md5=20f4cf5d81ea2fad18f6297500018654 \
                    file://src/init.c;beginline=2;endline=17;md5=f01cdfcf747af5380590cfd9bbfeaaf7 \
                    "


SECTION = "libs"

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/libgpg-error/libgpg-error-${PV}.tar.bz2 \
           file://pkgconfig.patch \
           file://0001-Do-not-fail-when-testing-config-scripts.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "89ce1ae893e122924b858de84dc4f67aae29ffa610ebf668d5aa539045663d6f"

BINCONFIG = "${bindir}/gpg-error-config"

inherit autotools binconfig-disabled pkgconfig gettext multilib_header multilib_script ptest

RDEPENDS:${PN}-ptest:append = " make bash"

MULTILIB_SCRIPTS = "${PN}-dev:${bindir}/gpgrt-config"

CPPFLAGS += "-P"

do_install:append() {
	# we don't have common lisp in OE
	rm -rf "${D}${datadir}/common-lisp/"
	oe_multilib_header gpg-error.h gpgrt.h
}

do_compile_ptest() {
    oe_runmake -C tests buildtest-TESTS
}

do_install_ptest() {
    install ${B}/tests/t-*[!\.o] ${D}${PTEST_PATH}
    install ${B}/tests/Makefile ${D}${PTEST_PATH}
}

FILES:${PN}-dev += "${bindir}/gpg-error"
FILES:${PN}-doc += "${datadir}/libgpg-error/errorref.txt"

BBCLASSEXTEND = "native nativesdk"
