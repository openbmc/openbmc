SUMMARY = "Python Remote Objects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cd13dafd4eeb0802bb6efea6b4a4bdbc"

SRC_URI[sha256sum] = "e130da06478b813173b959f7013d134865e07fbf58cc5f1a2598f99479cdac5f"

PYPI_PACKAGE = "Pyro4"

inherit pypi setuptools3

RDEPENDS:${PN} += " \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-serpent \
    ${PYTHON_PN}-threading \
    "
