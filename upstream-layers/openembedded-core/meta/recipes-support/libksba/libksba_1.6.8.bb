SUMMARY = "Easy API to create and parse X.509 and CMS related objects"
DESCRIPTION = "A library to make the tasks of working with X.509 certificates, \
CMS data and related objects more easy. It provides a highlevel interface to \
the implemented protocols and presents the data in a consistent way. The \
library does not rely on another cryptographic library but provides \
hooks for easy integration with Libgcrypt. "
HOMEPAGE = "http://www.gnupg.org/related_software/libksba/"
LICENSE = "GPL-3.0-or-later & (GPL-2.0-or-later | LGPL-3.0-or-later)"
LICENSE:${PN} = "GPL-2.0-or-later | LGPL-3.0-or-later"
LICENSE:${PN}-doc = "GPL-3.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=fd541d83f75d038c4e0617b672ed8bda \
                    file://COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.GPLv3;md5=2f31b266d3440dd7ee50f92cf67d8e6c \
                    file://COPYING.LGPLv3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                   "

DEPENDS = "libgpg-error"

BINCONFIG = "${bindir}/ksba-config"

inherit autotools binconfig-disabled pkgconfig texinfo ptest

require recipes-support/gnupg/drop-unknown-suffix.inc

UPSTREAM_CHECK_URI = "https://gnupg.org/download/index.html"
SRC_URI = "${GNUPG_MIRROR}/${BPN}/${BPN}-${PV}.tar.bz2 \
           file://ksba-add-pkgconfig-support.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "0f4510f1c7a679c3545990a31479f391ad45d84e039176309d42f80cf41743f5"

do_configure:prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/gpg-error.m4
}

do_compile_ptest() {
    oe_runmake -C tests check TESTS=
}

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests

    for test in ${B}/tests/cert-basic ${B}/tests/t-cms-parser ${B}/tests/t-crl-parser \
                ${B}/tests/t-der-builder ${B}/tests/t-dnparser ${B}/tests/t-ocsp \
                ${B}/tests/t-oid ${B}/tests/t-reader; do
        ${B}/libtool --mode=install install -m 0755 $test ${D}${PTEST_PATH}/tests/
    done

    cp -r ${S}/tests/samples ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
