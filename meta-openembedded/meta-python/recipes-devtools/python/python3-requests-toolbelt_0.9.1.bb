DESCRIPTION = "A utility belt for advanced users of python-requests."
HOMEPAGE = "https://toolbelt.readthedocs.org"
AUTHOR = "Ian Cordasco, Cory Benfield"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=71760e0f1dda8cff91b0bc9246caf571"

SRC_URI = "file://run-ptest \
          "

SRC_URI[md5sum] = "b1509735c4b4cf95df2619facbc3672e"
SRC_URI[sha256sum] = "968089d4584ad4ad7c171454f0a5c6dac23971e9472521ea3b6d49d610aa6fc0"

inherit pypi setuptools3 ptest

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-requests (>=2.0.1) \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
	${PYTHON_PN}-betamax \
	${PYTHON_PN}-mock \
	${PYTHON_PN}-multiprocessing \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/setup.py ${D}${PTEST_PATH}
	# remove test test_multipart_encoder.py as it fails,
	# downloaded file is not supported
	rm -f ${D}${PTEST_PATH}/tests/test_multipart_encoder.py
}
