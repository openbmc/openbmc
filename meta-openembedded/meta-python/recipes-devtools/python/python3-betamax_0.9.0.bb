DESCRIPTION = "VCR imitation for python requests"
HOMEPAGE = "https://github.com/betamaxpy/betamax"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61c15f0c146c5fb1a8ce8ba2f310d73c"

PV .= "+git"

SRCREV = "8f3d284103676a43d1481b5cffae96f3a601e0be"
SRC_URI += " \
        file://run-ptest \
        git://github.com/betamaxpy/betamax;protocol=https;branch=main \
        file://0001-Drop-ptests-fixtures-and-recorde_modes.patch \
"

S = "${WORKDIR}/git"

inherit setuptools3 ptest

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
