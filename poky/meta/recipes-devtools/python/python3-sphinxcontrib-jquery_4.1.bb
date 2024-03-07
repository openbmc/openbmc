SUMMARY = "Extension to include jQuery on newer Sphinx releases"
HOMEPAGE = "https://pypi.org/project/sphinxcontrib-jquery/"
LICENSE = "0BSD"
LIC_FILES_CHKSUM = "file://LICENCE;md5=926e8b7e89e3ebb2a2d1dfaf0873f241"

SRC_URI[sha256sum] = "1620739f04e36a2c779f1a131a2dfd49b2fd07351bf1968ced074365933abc7a"

PYPI_PACKAGE = "sphinxcontrib-jquery"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
