DESCRIPTION = "langtable is used to guess reasonable defaults for locale,\
keyboard, territory"
HOMEPAGE = "https://github.com/mike-fabian/langtable/"
LICENSE = "GPLv3+"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI[md5sum] = "d8f99adfb184f9def22539310f97ce80"
SRC_URI[sha256sum] = "fb17fd4d8e491c79159f81aa06ebacb18673fce59dac96f4e9d2d2db27a2e374"

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
