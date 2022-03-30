SUMMARY = "Python implementation of subunit test streaming protocol"
HOMEPAGE = "https://pypi.org/project/python-subunit/"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://README.rst;beginline=1;endline=20;md5=909c08e291647fd985fbe5d9836d51b6"

PYPI_PACKAGE = "python-subunit"

SRC_URI[sha256sum] = "042039928120fbf392e8c983d60f3d8ae1b88f90a9f8fd7188ddd9c26cad1e48"

inherit pypi setuptools3

RDEPENDS:${PN} = " python3-testtools"

BBCLASSEXTEND = "nativesdk"
