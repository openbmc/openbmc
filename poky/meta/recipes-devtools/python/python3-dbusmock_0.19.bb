SUMMARY = "With this program/Python library you can easily create mock objects on D-Bus"

LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "497f30eed2fcd5deaa2633b9622e4e99af4bdfba4e972b350ba630bac6fc86c2"

PYPI_PACKAGE = "python-dbusmock"

inherit pypi setuptools3

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-dbus \
    ${PYTHON_PN}-pygobject \
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-xml \
    "
