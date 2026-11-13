SUMMARY = "Library for reading extended image information (EXIF) from JPEG files"
DESCRIPTION = "libexif is a library for parsing, editing, and saving EXIF data. It is \
intended to replace lots of redundant implementations in command-line \
utilities and programs with GUIs."
HOMEPAGE = "https://libexif.github.io/"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/libexif-${PV}.tar.xz \
           file://0001-Add-serial-tests-config-needed-by-ptest.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "4a055ed6575e61ca46c3172be3c753cc16c9becd0f99ec71d58dd0e471476c0c"

inherit autotools gettext github-releases ptest

EXTRA_OECONF += "--disable-docs"

do_compile_ptest() {
    oe_runmake -C test buildtest-TESTS
}

do_install_ptest() {
    install ${S}/test/*.sh ${D}${PTEST_PATH}

    for file in $(find ${B}/test/test-* -executable -type f); do
        ${B}/libtool --mode=install install $file ${D}/${PTEST_PATH}
    done

    install -d ${D}${PTEST_PATH}/testdata
    install ${S}/test/testdata/* ${D}${PTEST_PATH}/testdata

    sed -i -e "s/@TESTS@/$(makefile-getvar ${B}/test/Makefile TESTS)/" ${D}${PTEST_PATH}/run-ptest
}

BBCLASSEXTEND = "native nativesdk"
