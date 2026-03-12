SUMMARY = "Sphinx Doxygen renderer"
DESCRIPTION = "Breathe is a Sphinx plugin providing beautifully integrated Doxygen output in your user-facing documentation."
HOMEPAGE = "https://www.breathe-doc.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9e49eecf36fc015de7c61a0247df75d6"

SRC_URI[sha256sum] = "14860b73118ac140b7a3f55446890c777d1b67149cb024279fe3710dad7f535c"

inherit pypi python_flit_core ptest ptest-python-pytest

RDEPENDS:${PN} = "python3-sphinx"

PYPI_PACKAGE = "breathe"

BBCLASSEXTEND =+ "native nativesdk"
