SUMMARY = "With this program/Python library you can easily create mock objects on D-Bus"
HOMEPAGE = "https://pypi.org/project/python-dbusmock/"

LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=e6a600fd5e1d9cbde2d983680233ad02"

SRC_URI[sha256sum] = "221b65e1c2e48de9fd11bf7e8c165adaf91648f49a11f390d086a498386f2984"

PYPI_PACKAGE = "python_dbusmock"
UPSTREAM_CHECK_PYPI_PACKAGE = "${PYPI_PACKAGE}"

inherit pypi python_setuptools_build_meta
DEPENDS += "python3-setuptools-scm-native"

RDEPENDS:${PN} += "\
    python3-dbus \
    python3-unittest \
    python3-xml \
    "

RRECOMMENDS:${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'gobject-introspection-data', '${MLPREFIX}python3-pygobject', '', d)}"

BBCLASSEXTEND = "native"
