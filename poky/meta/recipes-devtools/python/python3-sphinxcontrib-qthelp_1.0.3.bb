DESCRIPTION = "Is a sphinx extension which outputs QtHelp document."
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f7a83b72ea86d04827575ec0b63430eb"

SRC_URI[sha256sum] = "4c33767ee058b70dba89a6fc5c1892c0d57a54be67ddd3e7875a18d14cba5a72"

PYPI_PACKAGE = "sphinxcontrib-qthelp"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
