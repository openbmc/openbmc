DESCRIPTION = "A utility provides a fake certificate authority (CA)"
HOMEPAGE = "https://pypi.org/project/trustme"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d5a7af1a4b73e57431e25d15a2da745a"

SRC_URI[sha256sum] = "5375ad7fb427074bec956592e0d4ee2a4cf4da68934e1ba4bcf4217126bc45e6"

inherit pypi setuptools3 ptest

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
    python3-pyasn1-modules \
    python3-pytest \
    python3-service-identity \
    python3-unittest-automake-output \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/setup.py ${D}${PTEST_PATH}
}
