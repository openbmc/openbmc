SUMMARY = "A low level cryptographic library"
HOMEPAGE = "http://www.lysator.liu.se/~nisse/nettle/"
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

SRC_URI[md5sum] = "9bdebb0e2f638d3b9d91f7fc264b70c1"
SRC_URI[sha256sum] = "f941cf1535cd5d1819be5ccae5babef01f6db611f9b5a777bae9c7604b8a92ad"

UPSTREAM_CHECK_REGEX = "nettle-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools ptest multilib_header

EXTRA_AUTORECONF += "--exclude=aclocal"

EXTRA_OECONF = "--disable-openssl"
CFLAGS_append = " -std=gnu99"

do_compile_ptest() {
        oe_runmake buildtest
}

do_install_append() {
    oe_multilib_header nettle/nettle-stdint.h nettle/version.h
}

do_install_ptest() {
        install -d ${D}${PTEST_PATH}/testsuite/
        install ${S}/testsuite/gold-bug.txt ${D}${PTEST_PATH}/testsuite/
        install ${S}/testsuite/*-test ${D}${PTEST_PATH}/testsuite/
        # tools can be found in PATH, not in ../tools/
        sed -i -e 's|../tools/||' ${D}${PTEST_PATH}/testsuite/*-test
        install ${B}/testsuite/*-test ${D}${PTEST_PATH}/testsuite/
}

BBCLASSEXTEND = "native nativesdk"
