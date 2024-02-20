SUMMARY = "Simple Python module for working with HTML/CSS color definitions."
HOMEPAGE = "https://pypi.org/project/webcolors/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=702b1ef12cf66832a88f24c8f2ee9c19"

SRC_URI[sha256sum] = "c225b674c83fa923be93d235330ce0300373d02885cef23238813b0d5668304a"

inherit pypi python_setuptools_build_meta ptest

RDEPENDS:${PN}:class-target = "\
    python3-stringold \
"

SRC_URI += " \
    file://run-ptest \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
"

do_install_ptest() {
    install -d ${D}${PTEST_PATH}/tests
    cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}

BBCLASSEXTEND = "native nativesdk"
