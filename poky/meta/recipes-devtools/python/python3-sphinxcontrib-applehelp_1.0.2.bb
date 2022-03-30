DESCRIPTION = "sphinxcontrib-applehelp is a sphinx extension which outputs Apple help books"
HOMEPAGE = "https://www.sphinx-doc.org"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c7715857042d4c8c0105999ca0c072c5"

SRC_URI[sha256sum] = "a072735ec80e7675e3f432fcae8610ecf509c5f1869d17e2eecff44389cdbc58"

PYPI_PACKAGE = "sphinxcontrib-applehelp"

inherit pypi setuptools3

BBCLASSEXTEND = "native nativesdk"
