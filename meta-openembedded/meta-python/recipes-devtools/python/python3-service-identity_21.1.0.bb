DESCRIPTION = "The tools for verifying whether a certificate is valid for the intended purposes."
HOMEPAGE = "https://pypi.org/project/service-identity"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8a0f079f4e6a215d6bd6f9d97cab4d5f"

SRC_URI[md5sum] = "5e5c195d8fcedc72f9068be2ad9b5a13"
SRC_URI[sha256sum] = "6e6c6086ca271dc11b033d17c3a8bea9f24ebff920c587da090afc9519419d34"

inherit pypi setuptools3 ptest

SRC_URI += " \
	file://run-ptest \
"

RDEPENDS:${PN} += " \
    python3-attr \
    python3-cryptography \
    python3-pyasn1-modules \
"

RDEPENDS:${PN}-ptest += " \
    python3-pytest \
    python3-unittest-automake-output \
    python3-pyopenssl \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
	cp -rf ${S}/setup.py ${D}${PTEST_PATH}
}
