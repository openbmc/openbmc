DESCRIPTION = "Graphviz protocol implementation"
HOMEPAGE = "https://graphviz.readthedocs.io/en/stable/"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE.txt;md5=f7831c564aeff14e68aa4ff7a93dc89f"

SRC_URI[md5sum] = "7123efabb68270f579525df74bf8b0c9"
SRC_URI[sha256sum] = "92b7637ece63c77e3d39221ae1f4df98e9256cb449e9860c598335b34496d195"

inherit pypi setuptools3

PYPI_PACKAGE_EXT = "zip"

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-logging \
"

BBCLASSEXTEND = "native nativesdk"
