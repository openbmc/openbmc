SUMMARY = "Python Remote Objects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cd13dafd4eeb0802bb6efea6b4a4bdbc"

SRC_URI[md5sum] = "e31fc077e06de9fc0bb061e357401954"
SRC_URI[sha256sum] = "46847ca703de3f483fbd0b2d22622f36eff03e6ef7ec7704d4ecaa3964cb2220"

PYPI_PACKAGE = "Pyro4"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-serpent \
    ${PYTHON_PN}-threading \
    "
