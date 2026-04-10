SUMMARY = "WSGI HTTP Server for UNIX"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5dc9171ccd8fcbd7827c850148b3ca98"

SRC_URI[sha256sum] = "f74e1b2f9f76f6cd1ca01198968bd2dd65830edc24b6e8e4d78de8320e2fe889"

inherit pypi python_setuptools_build_meta ptest

CVE_PRODUCT = "gunicorn"

SRC_URI += " \
	file://run-ptest \
"

# python-misc for wsgiref
RDEPENDS:${PN}-ptest += " \
    bash \
    python3-eventlet \
    python3-gevent \
    python3-misc \
    python3-pytest \
    python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

RDEPENDS:${PN} += "python3-setuptools python3-fcntl"
