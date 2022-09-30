DESCRIPTION = "langtable is used to guess reasonable defaults for locale,\
keyboard, territory"
HOMEPAGE = "https://github.com/mike-fabian/langtable/"
LICENSE = "GPL-3.0-or-later"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI[sha256sum] = "ae77d62fe6002308ce6197310c4a933c4e13632bbaf7219a3533dc45d36223f8"

inherit pypi setuptools3 python3native

DISTUTILS_INSTALL_ARGS += " \
    --install-data=${datadir}/langtable"

FILES:${PN} += "${datadir}/*"

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-xml \
"
