DESCRIPTION = "langtable is used to guess reasonable defaults for locale,\
keyboard, territory"
HOMEPAGE = "https://github.com/mike-fabian/langtable/"
LICENSE = "GPLv3+"
SECTION = "devel/python"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI[md5sum] = "240a62b84ed29f6530ceae60609f7eff"
SRC_URI[sha256sum] = "a2c499704c91c0a23e5db92e80ad1d45e51466b82ffaee478e5bb781460b3db5"

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
