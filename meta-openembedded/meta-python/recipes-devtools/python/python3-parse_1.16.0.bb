SUMMARY = "Parse strings using a specification based on the Python format() syntax"
HOMEPAGE = "https://github.com/r1chardj0n3s/parse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8ab458ad281b60e6f1b39b3feafbfc05"

SRC_URI[md5sum] = "ba263b3143e9528ae960bbba4d56bf25"
SRC_URI[sha256sum] = "cd89e57aed38dcf3e0ff8253f53121a3b23e6181758993323658bffc048a5c19"

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
