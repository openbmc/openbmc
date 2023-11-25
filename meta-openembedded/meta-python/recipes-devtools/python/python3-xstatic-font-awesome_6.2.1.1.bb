DESCRIPTION = "Font Awesome icons packaged for setuptools (easy_install) / pip."
HOMEPAGE = "https://pypi.python.org/pypi/XStatic-Font-Awesome"
SECTION = "devel/python"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://PKG-INFO;md5=f1a2fe131dcb2fc6243c26cf05ecdb36"

PYPI_PACKAGE = "XStatic-Font-Awesome"

SRC_URI[sha256sum] = "f075871096128638f2e1539020d8227754c3d885dd68e7ee6de9a01235076828"

DEPENDS += " \
    ${PYTHON_PN}-xstatic \
    ${PYTHON_PN}-pip \
"

inherit pypi setuptools3
