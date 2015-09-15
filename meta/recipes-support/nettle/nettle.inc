SUMMARY = "A low level cryptographic library"
HOMEPAGE = "http://www.lysator.liu.se/~nisse/nettle/"
SECTION = "libs"

LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://serpent-decrypt.c;beginline=53;endline=67;md5=bcfd4745d53ca57f82907089898e390d \
                    file://serpent-set-key.c;beginline=56;endline=70;md5=bcfd4745d53ca57f82907089898e390d"

DEPENDS += "gmp"

SRC_URI = "http://www.lysator.liu.se/~nisse/archive/${BP}.tar.gz \
           file://Add-target-to-only-build-tests-not-run-them.patch \
           file://run-ptest \
           "

SRC_URI[md5sum] = "003d5147911317931dd453520eb234a5"
SRC_URI[sha256sum] = "bc71ebd43435537d767799e414fce88e521b7278d48c860651216e1fc6555b40"

EXTRA_OECONF = "--disable-openssl"

inherit autotools ptest

do_configure_prepend() {
       if [ ! -e ${S}/acinclude.m4 -a -e ${S}/aclocal.m4 ]; then
               cp ${S}/aclocal.m4 ${S}/acinclude.m4
       fi
}

do_compile_ptest() {
        oe_runmake buildtest
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
