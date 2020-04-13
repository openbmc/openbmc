SUMMARY = "Parse strings using a specification based on the Python format() syntax"
HOMEPAGE = "https://github.com/r1chardj0n3s/parse"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://parse.py;beginline=1325;endline=1345;md5=3d987b2e73881bbce768a80f9cdd23d9"

SRC_URI[md5sum] = "a5aa82b2b8b4d733d227e3c99d7d01b4"
SRC_URI[sha256sum] = "a6d4e2c2f1fbde6717d28084a191a052950f758c0cbd83805357e6575c2b95c0"

inherit pypi ptest

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
