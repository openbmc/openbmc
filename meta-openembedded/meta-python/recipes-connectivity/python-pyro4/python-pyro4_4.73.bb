SUMMARY = "Python Remote Objects"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cd13dafd4eeb0802bb6efea6b4a4bdbc"

SRC_URI[md5sum] = "0375c061b93411feb86da01e801df888"
SRC_URI[sha256sum] = "536b07a097d0619e7ab1effa3747fda177a24168d17a07a93ca9ac30977608f7"

PYPI_PACKAGE = "Pyro4"

inherit pypi setuptools

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
    ${PYTHON_PN}-selectors34 \
    ${PYTHON_PN}-serpent \
    ${PYTHON_PN}-threading \
    ${PYTHON_PN}-zlib \
    "
