DESCRIPTION = "Graphviz protocol implementation"
HOMEPAGE = "https://graphviz.readthedocs.io/en/stable/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f7831c564aeff14e68aa4ff7a93dc89f"

SRC_URI[md5sum] = "436cb0f41f6a949f916357ca037e205a"
SRC_URI[sha256sum] = "f5aad52a652c06825dcc5ee018d920fca26aef339386866094597fb3f2f222ce"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "zip"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
