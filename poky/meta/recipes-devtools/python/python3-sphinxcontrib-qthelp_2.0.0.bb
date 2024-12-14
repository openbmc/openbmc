SUMMARY = "Is a sphinx extension which outputs QtHelp document."
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENCE.rst;md5=f7a83b72ea86d04827575ec0b63430eb"

SRC_URI[sha256sum] = "4fe7d0ac8fc171045be623aba3e2a8f613f8682731f9153bb2e40ece16b9bbab"

PYPI_PACKAGE = "sphinxcontrib_qthelp"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
