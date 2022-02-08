DESCRIPTION = "langtable is used to guess reasonable defaults for locale,\
keyboard, territory"
HOMEPAGE = "https://github.com/mike-fabian/langtable/"
LICENSE = "GPLv3+"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

S = "${WORKDIR}/git"
B = "${S}"

SRCREV = "35687ca957b746f153a6872139462b1443f8cad1"
PV = "0.0.38+git${SRCPV}"
SRC_URI = "git://github.com/mike-fabian/langtable.git;branch=master;protocol=https \
"

inherit setuptools3 python3native

DISTUTILS_INSTALL_ARGS += " \
    --install-data=${datadir}/langtable"

FILES_${PN} += "${datadir}/*"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-compression \
    ${PYTHON_PN}-doctest \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-xml \
"
