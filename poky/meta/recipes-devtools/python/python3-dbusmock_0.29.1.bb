SUMMARY = "With this program/Python library you can easily create mock objects on D-Bus"
HOMEPAGE = "https://pypi.org/project/python-dbusmock/"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "b03590057c236d352c38973f4b71ae2c97b3a1cb1dc6f03278ce4072a3716a74"

PYPI_PACKAGE = "python-dbusmock"

inherit pypi python_setuptools_build_meta
DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    ${PYTHON_PN}-dbus \
    ${PYTHON_PN}-unittest \
    ${PYTHON_PN}-xml \
    "

RRECOMMENDS:${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'gobject-introspection-data', '${MLPREFIX}${PYTHON_PN}-pygobject', '', d)}"

BBCLASSEXTEND = "native"
