DESCRIPTION = "langtable is used to guess reasonable defaults for locale,\
keyboard, territory"
HOMEPAGE = "https://github.com/mike-fabian/langtable/"
LICENSE = "GPLv3+"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI[md5sum] = "5d28198fa933dac5c037108d8f5cf3bb"
SRC_URI[sha256sum] = "8d4615cc0bb0fa49faa05b55ff49b1f41122b8092ca18a5d10f1e1699d6d7b3c"

inherit pypi setuptools3 python3native

DISTUTILS_INSTALL_ARGS += " \
    --install-data=${datadir}/langtable"

FILES_${PN} += "${datadir}/*"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-xml \
"
