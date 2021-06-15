SUMMARY = "This module contains functions for converting between Julian dates and calendar dates"
DESCRIPTION = "A function for converting Gregorian calendar dates to Julian dates, \
and another function for converting Julian calendar dates to Julian dates are defined. \
Two functions for the reverse calculations are also defined."

HOMEPAGE = "https://github.com/phn/jdcal"
LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=bd236e1f590973467a427bb354be0f46"

inherit pypi setuptools3 ptest

SRC_URI[md5sum] = "e05bdb60fa80f25bc60e73e0c6b7c5dc"
SRC_URI[sha256sum] = "472872e096eb8df219c23f2689fc336668bdb43d194094b5cc1707e1640acfc8"

RDEPENDS_${PN} += "${PYTHON_PN}-compression ${PYTHON_PN}-io ${PYTHON_PN}-pprint ${PYTHON_PN}-shell"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	cp -f ${S}/test_jdcal.py ${D}${PTEST_PATH}/
}
