DESCRIPTION = "The tools for verifying whether a certificate is valid for the intended purposes."
HOMEPAGE = "https://pypi.org/project/service-identity"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=76edce6a3fa1b82b0bf2b6ce174c19e2"

SRC_URI[sha256sum] = "b8683ba13f0d39c6cd5d625d2c5f65421d6d707b013b375c355751557cbe8e09"

inherit pypi python_hatchling ptest

PYPI_PACKAGE = "service_identity"

SRC_URI += " \
	file://run-ptest \
"

DEPENDS += "python3-hatch-vcs-native python3-hatch-fancy-pypi-readme-native"

RDEPENDS:${PN} += " \
    python3-attr \
    python3-cryptography \
    python3-pyasn1-modules \
"

RDEPENDS:${PN}-ptest += " \
    python3-attrs \
    python3-pytest \
    python3-six \
    python3-unittest-automake-output \
    python3-pyopenssl \
"

do_install_ptest() {
	install -d ${D}${PTEST_PATH}/tests
	cp -rf ${S}/tests/* ${D}${PTEST_PATH}/tests/
}
