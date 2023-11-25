SUMMARY = "Library for building powerful interactive command lines in Python"
DESCRIPTION = "Measures the displayed width of unicode strings in a terminal"
HOMEPAGE = "https://github.com/jquast/wcwidth"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b15979c39a2543892fca8cd86b4b52cb"

SRC_URI[sha256sum] = "a675d1a4a2d24ef67096a04b85b02deeecd8e226f57b5e3a72dbb9ed99d27da8"

inherit pypi setuptools3 ptest

SRC_URI += " \
        file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
       ${PYTHON_PN}-pytest \
       ${PYTHON_PN}-unittest-automake-output \
"

do_install_ptest() {
      install -d ${D}${PTEST_PATH}/tests
      cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
