DESCRIPTION = "Self-service finite-state machines for the programmer on the go"
HOMEPAGE = "https://github.com/glyph/Automat"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4ad213bcca81688e94593e5f60c87477"

FILESEXTRAPATHS_prepend := "${THISDIR}/python-automat:"

SRC_URI[md5sum] = "b72d5b7a83c2b1bd6e9ec3614a69f9ea"
SRC_URI[sha256sum] = "cbd78b83fa2d81fe2a4d23d258e1661dd7493c9a50ee2f1a5b2cac61c1793b0e"

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
