SUMMARY = "URI parsing, classification and composition"
HOMEPAGE = "https://github.com/tkem/uritools/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=539275e657c6b7af026bb908356f7541"

SRC_URI[sha256sum] = "ee06a182a9c849464ce9d5fa917539aacc8edd2a4924d1b7aabeeecabcae3bc2"

SRC_URI += "file://run-ptest"

inherit pypi python_setuptools_build_meta ptest

do_install_ptest() {
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/
}

RDEPENDS:${PN}-ptest += " \
	python3-pytest \
	python3-unittest-automake-output \
"

BBCLASSEXTEND = "native nativesdk"
