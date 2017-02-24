SUMMARY  = "Yet Another Python Profiler"
HOMEPAGE = "http://yappi.googlecode.com/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=ad426a7287507a8db02778ff586d5370"

SRC_URI[md5sum] = "a02c49efe783c4e31d6bbd805a37adec"
SRC_URI[sha256sum] = "15cc17dba1252ecaae29ced1e96c216165d93fd3e9ea05dff1f5e5866f16bd59"

PYPI_PACKAGE = "yappi"
inherit pypi setuptools

RDEPENDS_${PN} += "\
    ${PYTHON_PN}-pickle \
    ${PYTHON_PN}-threading \
    "
