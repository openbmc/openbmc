SUMMARY = "A pure python RFC3339 validator"
HOMEPAGE = "https://github.com/naimetti/rfc3339-validator"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=a21b13b5a996f08f7e0b088aa38ce9c6"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-rfc3339-validator:"

SRC_URI[md5sum] = "2c233007189d5ef21046cb2afac51a96"
SRC_URI[sha256sum] = "c9659c3183488a1875c4d327c9873d9e92c54cdcd69dfbfae7546ad8b27baf9a"

PYPI_PACKAGE = "rfc3339_validator"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-core \
    ${PYTHON_PN}-datetime \
    ${PYTHON_PN}-six \
"

BBCLASSEXTEND = "native nativesdk"
