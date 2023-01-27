SUMMARY = "With this program/Python library you can easily create mock objects on D-Bus"
HOMEPAGE = "https://pypi.org/project/python-dbusmock/"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "0786977750dbcbde6e2553339a2e66d9a8d4453d5c848b945e8fd1badb736ffd"

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
