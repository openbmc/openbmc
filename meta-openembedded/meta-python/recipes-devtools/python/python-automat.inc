DESCRIPTION = "Self-service finite-state machines for the programmer on the go"
HOMEPAGE = "https://github.com/glyph/Automat"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ad213bcca81688e94593e5f60c87477"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-automat:"

SRC_URI[md5sum] = "ad7bba58d262d8956d732330cb5ef53d"
SRC_URI[sha256sum] = "3c1fd04ecf08ac87b4dd3feae409542e9bf7827257097b2b6ed5692f69d6f6a8"

PYPI_PACKAGE = "Automat"

inherit pypi

SRC_URI_append = " \
    file://0001-setup.py-remove-the-dependency-on-m2r.patch \
"

DEPENDS += "${PYTHON_PN}-setuptools-scm-native"

RDEPENDS_${PN} += "\
   ${PYTHON_PN}-attrs \
   ${PYTHON_PN}-six \
"
