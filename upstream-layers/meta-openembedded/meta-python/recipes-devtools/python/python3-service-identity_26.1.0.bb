DESCRIPTION = "The tools for verifying whether a certificate is valid for the intended purposes."
HOMEPAGE = "https://pypi.org/project/service-identity"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=76edce6a3fa1b82b0bf2b6ce174c19e2"

SRC_URI[sha256sum] = "6358c52882c96e66ac4a55eb3a72c7dd4a70763f8cc6fa4e70abde2656f4bf3b"

inherit pypi python_hatchling ptest-python-pytest

PYPI_PACKAGE = "service_identity"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

DEPENDS += "python3-hatch-vcs-native python3-hatch-fancy-pypi-readme-native"

RDEPENDS:${PN} += " \
    python3-attr \
    python3-cryptography \
    python3-pyasn1-modules \
"

RDEPENDS:${PN}-ptest += " \
    python3-attrs \
    python3-six \
    python3-pyopenssl \
"
