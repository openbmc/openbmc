SUMMARY = "PKCS#11 (Cryptoki) support for Python"
DESCRIPTION = "A high level, “more Pythonic” interface to the PKCS#11 (Cryptoki) standard to support HSM and Smartcard devices in Python."
HOMEPAGE = "https://pypi.org/project/python-pkcs11/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://README.rst;beginline=337;endline=358;md5=f68bda54505b4002e6ec86e08125ef79"

SRC_URI[sha256sum] = "9737e0c24cabb8bc9d48bf8c57c3df2a70f8cdd96b70c50290803286f9e46bf7"

PYPI_PACKAGE = "python-pkcs11"

inherit pypi setuptools3 cython

BBCLASSEXTEND = "native"

DEPENDS += " \
    python3-setuptools-scm-native \
    python3 \
"

RDEPENDS:${PN} += " \
    python3-asn1crypto \
    python3-cached-property \
"
