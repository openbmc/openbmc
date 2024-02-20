DESCRIPTION = "VCR imitation for python requests"
HOMEPAGE = "https://github.com/betamaxpy/betamax"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61c15f0c146c5fb1a8ce8ba2f310d73c"

SRC_URI += " \
        file://run-ptest \
        file://0001-Drop-ptests-fixtures-and-recorde_modes.patch \
"

SRC_URI[md5sum] = "b8182d43a200fc126a3bf7555626f964"
SRC_URI[sha256sum] = "5bf004ceffccae881213fb722f34517166b84a34919b92ffc14d1dbd050b71c2"

inherit pypi setuptools3 ptest

RDEPENDS:${PN} += " \
    python3-requests \
    python3-unittest \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
