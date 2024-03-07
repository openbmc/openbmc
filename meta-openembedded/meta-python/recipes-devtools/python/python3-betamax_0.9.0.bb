DESCRIPTION = "VCR imitation for python requests"
HOMEPAGE = "https://github.com/betamaxpy/betamax"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61c15f0c146c5fb1a8ce8ba2f310d73c"

SRC_URI += " \
        file://run-ptest \
        file://0001-Drop-ptests-fixtures-and-recorde_modes.patch \
"
SRC_URI[sha256sum] = "82316e1679bc6879e3c83318d016b54b7c9225ff08c4462de4813e22038d5f94"

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
