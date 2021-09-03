SUMMARY = "WSGI HTTP Server for UNIX"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f75f3fb94cdeab1d607e2adaa6077752"

SRC_URI[sha256sum] = "e0a968b5ba15f8a328fdfd7ab1fcb5af4470c28aaf7e55df02a99bc13138e6e8"

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
