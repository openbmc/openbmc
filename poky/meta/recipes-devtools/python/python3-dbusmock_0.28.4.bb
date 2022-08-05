SUMMARY = "With this program/Python library you can easily create mock objects on D-Bus"
HOMEPAGE = "https://pypi.org/project/python-dbusmock/"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "52ad024a44d46602084fd91c0c467ab95b7ecf27c4e2168e0d2160623f18c0a2"

PYPI_PACKAGE = "python-dbusmock"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-dbus \
    ${PYTHON_PN}-pygobject \
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-xml \
    "
