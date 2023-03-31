DESCRIPTION = "A utility belt for advanced users of python-requests."
HOMEPAGE = "https://toolbelt.readthedocs.org"
AUTHOR = "Ian Cordasco, Cory Benfield"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6f14302a4b4099009ab38b4dde5f1075"

SRC_URI = "file://run-ptest \
          "

SRC_URI[sha256sum] = "62e09f7ff5ccbda92772a29f394a49c3ad6cb181d568b1337626b2abb628a63d"

inherit pypi setuptools3 ptest

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-requests (>=2.0.1) \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
	${PYTHON_PN}-betamax \
	${PYTHON_PN}-mock \
	${PYTHON_PN}-multiprocessing \
	${PYTHON_PN}-trustme \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/setup.py ${D}${PTEST_PATH}
	# remove test test_multipart_encoder.py as it fails,
	# downloaded file is not supported
	rm -f ${D}${PTEST_PATH}/tests/test_multipart_encoder.py
}
