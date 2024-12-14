DESCRIPTION = "A utility provides a fake certificate authority (CA)"
HOMEPAGE = "https://pypi.org/project/trustme"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d5a7af1a4b73e57431e25d15a2da745a"

SRC_URI[sha256sum] = "ed2264fb46c35459e6de9e454ed4bab73be44b6a2a26ad417f9b6854aebb644a"

inherit pypi python_hatchling python_setuptools_build_meta ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN} += " \
    python3-cryptography \
    python3-datetime \
    python3-idna \
    python3-io \
"

RDEPENDS:${PN}-ptest += " \
    python3-attrs \
    python3-pyopenssl \
    python3-pyasn1-modules \
    python3-pytest \
    python3-service-identity \
    python3-six \
    python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
