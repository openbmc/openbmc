SUMMARY = "With this program/Python library you can easily create mock objects on D-Bus"
HOMEPAGE = "https://pypi.org/project/python-dbusmock/"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "3b496fab84aff3936054f747e9a22bb07ac5783b1d967c6cc39536c7ba315fce"

PYPI_PACKAGE = "python-dbusmock"

inherit pypi python_setuptools_build_meta
DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-dbus \
    ${PYTHON_PN}-pygobject \
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-xml \
    "

BBCLASSEXTEND = "native"
