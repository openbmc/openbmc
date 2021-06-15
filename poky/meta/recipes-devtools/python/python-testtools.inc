SUMMARY = "Extensions to the Python standard library unit testing framework"
HOMEPAGE = "https://pypi.org/project/testtools/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e2c9d3e8ba7141c83bfef190e0b9379a"

inherit pypi

SRC_URI[md5sum] = "e8fc7185b47cfb908c641f8c4b2a6add"
SRC_URI[sha256sum] = "64c974a6cca4385d05f4bbfa2deca1c39ce88ede31c3448bee86a7259a9a61c8"

DEPENDS += " \
    ${PYTHON_PN}-pbr \
    "

# Satisfy setup.py 'setup_requires'
DEPENDS += " \
    ${PYTHON_PN}-pbr-native \
    "

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-extras \
    ${PYTHON_PN}-pbr \
    ${PYTHON_PN}-six \
    "

BBCLASSEXTEND = "nativesdk"
