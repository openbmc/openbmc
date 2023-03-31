DESCRIPTION = "A utility provides a fake certificate authority (CA)"
HOMEPAGE = "https://pypi.org/project/trustme"
AUTHOR = "Nathaniel J. Smith"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d5a7af1a4b73e57431e25d15a2da745a"

SRC_URI[md5sum] = "0e4d698e5aecaf8306cf440bf3dcbbe0"
SRC_URI[sha256sum] = "5e07b23d70ceed64f3bb36ae4b9abc52354c16c98d45ab037bee2b5fbffe586c"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
	${PYTHON_PN}-service-identity \
	${PYTHON_PN}-pyasn1-modules \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/setup.py ${D}${PTEST_PATH}
}
