SUMMARY = "SMPP library for python"
SECTION = "devel/python"
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://README.md;md5=8b4e2ac8cf248f7b991784f88b630852"

PYPI_PACKAGE = "smpplib"
SRC_URI[sha256sum] = "6f3b036fcb2643c1b7a3289bb5ac4c9a720af1bf73e572e2729db6b5d800c273"

inherit pypi setuptools3 ptest-python-pytest

RDEPENDS:${PN} += " \
        python3-logging \
        python3-six \
"

RDEPENDS:${PN}-ptest += " \
    python3-mock \
    python3-profile \
"

do_install_ptest:append() {
        install -d ${D}${PTEST_PATH}/tests
        cp -rf ${S}/smpplib/tests/* ${D}${PTEST_PATH}/tests/
}
