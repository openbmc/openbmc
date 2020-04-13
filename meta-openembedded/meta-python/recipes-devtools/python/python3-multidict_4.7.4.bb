SUMMARY = "Multidicts are useful for working with HTTP headers, URL query args etc."
HOMEPAGE = "https://github.com/aio-libs/multidict/"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e74c98abe0de8f798ca609137f9cef4a"

inherit pypi setuptools3 ptest

SRC_URI[md5sum] = "22b46f759cf2cc3ca1d2c9f82cc9bb79"
SRC_URI[sha256sum] = "d7d428488c67b09b26928950a395e41cc72bb9c3d5abfe9f0521940ee4f796d4"

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS_${PN}-ptest += " \
	${PYTHON_PN}-pytest \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
