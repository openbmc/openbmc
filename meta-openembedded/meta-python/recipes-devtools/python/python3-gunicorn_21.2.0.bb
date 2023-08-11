SUMMARY = "WSGI HTTP Server for UNIX"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=302423eeae97079c633da81b6a5fe35e"

SRC_URI[sha256sum] = "88ec8bff1d634f98e61b9f65bc4bf3cd918a90806c6f5c48bc5603849ec81033"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    ${PYTHON_PN}-eventlet \
    ${PYTHON_PN}-gevent \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += "${PYTHON_PN}-setuptools ${PYTHON_PN}-fcntl"
