SUMMARY = "A low level cryptographic library"
HOMEPAGE = "http://www.lysator.liu.se/~nisse/nettle/"
DESCRIPTION = "It tries to solve a problem of providing a common set of \
cryptographic algorithms for higher-level applications by implementing a \
context-independent set of cryptographic algorithms"
SECTION = "libs"
LICENSE = "LGPLv3+ | GPLv2+"

LIC_FILES_CHKSUM = "file://COPYING.LESSERv3;md5=6a6a8e020838b23406c81b19c1d46df6 \
                    file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://serpent-decrypt.c;beginline=14;endline=36;md5=ca0d220bc413e1842ecc507690ce416e \
                    file://serpent-set-key.c;beginline=14;endline=36;md5=ca0d220bc413e1842ecc507690ce416e"

DEPENDS += "gmp"

SRC_URI = "${GNU_MIRROR}/${BPN}/${BP}.tar.gz \
           file://Add-target-to-only-build-tests-not-run-them.patch \
           file://run-ptest \
           file://check-header-files-of-openssl-only-if-enable_.patch \
           "

SRC_URI_append_class-target = "\
            file://dlopen-test.patch \
            "

SRC_URI[md5sum] = "0e5707b418c3826768d41130fbe4ee86"
SRC_URI[sha256sum] = "75cca1998761b02e16f2db56da52992aef622bf55a3b45ec538bc2eedadc9419"

UPSTREAM_CHECK_REGEX = "nettle-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools ptest multilib_header

EXTRA_AUTORECONF += "--exclude=aclocal"

EXTRA_OECONF = "--disable-openssl"
CFLAGS_append = " -std=gnu99"

do_compile_ptest() {
        oe_runmake buildtest
}

do_install_append() {
    oe_multilib_header nettle/version.h
}

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/testsuite/
        install ${S}/testsuite/gold-bug.txt ${D}${PTEST_PATH}/testsuite/
        install ${S}/testsuite/*-test ${D}${PTEST_PATH}/testsuite/
        # tools can be found in PATH, not in ../tools/
        sed -i -e 's|../tools/||' ${D}${PTEST_PATH}/testsuite/*-test
        install ${B}/testsuite/*-test ${D}${PTEST_PATH}/testsuite/
}

RDEPENDS_${PN}-ptest += "${PN}-dev"
INSANE_SKIP_${PN}-ptest += "dev-deps"

BBCLASSEXTEND = "native nativesdk"
