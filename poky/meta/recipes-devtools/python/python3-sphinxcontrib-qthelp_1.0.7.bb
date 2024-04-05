SUMMARY = "Is a sphinx extension which outputs QtHelp document."
HOMEPAGE = "http://babel.edgewall.org/"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=f7a83b72ea86d04827575ec0b63430eb"

SRC_URI[sha256sum] = "053dedc38823a80a7209a80860b16b722e9e0209e32fea98c90e4e6624588ed6"

PYPI_PACKAGE = "sphinxcontrib_qthelp"

inherit pypi python_flit_core

BBCLASSEXTEND = "native nativesdk"
