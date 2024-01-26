SUMMARY = "Multidicts are useful for working with HTTP headers, URL query args etc."
HOMEPAGE = "https://github.com/aio-libs/multidict/"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b4fef6e4b0828c2401fb983363985b39"

inherit pypi setuptools3 ptest

PV .= "+git"

SRCREV = "82b559cdd0b41548f3dadc5561a9aaaa7f93ae14"
PYPI_SRC_URI = "git://github.com/aio-libs/multidict;branch=master;protocol=https"
S = "${WORKDIR}/git"

SRC_URI += " \
  file://0001-Extend-aio-libs-multidict-909-to-3.12-as-well-to-add.patch \
	file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
	${PYTHON_PN}-pytest \
	${PYTHON_PN}-pytest-cov \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
