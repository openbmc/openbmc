DESCRIPTION = "A utility belt for advanced users of python-requests."
HOMEPAGE = "https://toolbelt.readthedocs.org"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6f14302a4b4099009ab38b4dde5f1075"

SRC_URI = " \
	   file://090856f4159c40a2927fb88546419f2e1697ad5f.patch \
	   file://720240501dca0b4eacc3295665d7ced8719e11d2.patch \
          "

SRC_URI[sha256sum] = "7681a0a3d047012b5bdc0ee37d7f8f07ebe76ab08caeccfc3921ce23c88d5bc6"

inherit pypi setuptools3 ptest-python-pytest

RDEPENDS:${PN} += " \
    python3-requests (>=2.0.1) \
"

RDEPENDS:${PN}-ptest += " \
    python3-betamax \
    python3-mock \
    python3-multiprocessing \
    python3-trustme \
"

do_install_ptest:append() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/setup.py ${D}${PTEST_PATH}
	# remove test test_multipart_encoder.py as it fails,
	# downloaded file is not supported
	rm -f ${D}${PTEST_PATH}/tests/test_multipart_encoder.py
}
