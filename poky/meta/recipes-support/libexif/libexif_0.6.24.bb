SUMMARY = "Library for reading extended image information (EXIF) from JPEG files"
DESCRIPTION = "libexif is a library for parsing, editing, and saving EXIF data. It is \
intended to replace lots of redundant implementations in command-line \
utilities and programs with GUIs."
HOMEPAGE = "https://libexif.github.io/"
SECTION = "libs"
LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

SRC_URI = "${GITHUB_BASE_URI}/download/v${PV}/libexif-${PV}.tar.bz2 \
           file://0001-Add-serial-tests-config-needed-by-ptest.patch \
           file://run-ptest \
           "

SRC_URI[sha256sum] = "d47564c433b733d83b6704c70477e0a4067811d184ec565258ac563d8223f6ae"

inherit autotools gettext github-releases ptest

EXTRA_OECONF += "--disable-docs"

do_compile_ptest() {
    oe_runmake -C test buildtest-TESTS
}

do_install_ptest() {
    install ${B}/test/test*[!\.o] ${D}${PTEST_PATH}
    for f in ${D}${PTEST_PATH}/test*; do
        sed -i "s/\(LD_LIBRARY_PATH=\).*\(:\$LD_LIBRARY_PATH\)\"/\1.\2/" $f
    done

    install ${B}/test/Makefile ${D}${PTEST_PATH}
    sed -i -e "/^srcdir/c srcdir = \$\{PWD\}" ${D}${PTEST_PATH}/Makefile

    install -d ${D}${PTEST_PATH}/nls
    install ${B}/test/nls/*[!\.o] ${D}${PTEST_PATH}/nls
    install -d ${D}${PTEST_PATH}/.libs
    install ${B}/test/.libs/* ${D}${PTEST_PATH}/.libs

    install ${S}/test/*.sh ${D}${PTEST_PATH}

    install -d ${D}${PTEST_PATH}/testdata
    install ${S}/test/testdata/* ${D}${PTEST_PATH}/testdata
}

RDEPENDS:${PN}-ptest += "make bash"

BBCLASSEXTEND = "native nativesdk"
