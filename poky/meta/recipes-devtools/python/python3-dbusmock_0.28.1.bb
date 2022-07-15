SUMMARY = "With this program/Python library you can easily create mock objects on D-Bus"
HOMEPAGE = "https://pypi.org/project/python-dbusmock/"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "6434e544c055e3570b20e341e50a3d2dd50b19d9e55d579b919e14879f9f1e57"

PYPI_PACKAGE = "python-dbusmock"

inherit pypi setuptools3

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-dbus \
    ${PYTHON_PN}-pygobject \
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-xml \
    "
