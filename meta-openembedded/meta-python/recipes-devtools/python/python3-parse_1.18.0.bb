SUMMARY = "Parse strings using a specification based on the Python format() syntax"
HOMEPAGE = "https://github.com/r1chardj0n3s/parse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8ab458ad281b60e6f1b39b3feafbfc05"

SRC_URI[md5sum] = "42ea0002cf36bfd2bd74f3c05bf25f60"
SRC_URI[sha256sum] = "91666032d6723dc5905248417ef0dc9e4c51df9526aaeef271eacad6491f06a4"

inherit pypi setuptools3 ptest

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-logging \
    "

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/test_parse.py ${D}${PTEST_PATH}/
}
