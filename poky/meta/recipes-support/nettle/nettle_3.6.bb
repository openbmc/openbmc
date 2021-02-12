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

SRC_URI[md5sum] = "c45ee24ed7361dcda152a035d396fe8a"
SRC_URI[sha256sum] = "d24c0d0f2abffbc8f4f34dcf114b0f131ec3774895f3555922fe2f40f3d5e3f1"

UPSTREAM_CHECK_REGEX = "nettle-(?P<pver>\d+(\.\d+)+)\.tar"

inherit autotools ptest multilib_header

EXTRA_AUTORECONF += "--exclude=aclocal"

EXTRA_OECONF = "--disable-openssl"

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
