SUMMARY = "Sphinx Doxygen renderer"
DESCRIPTION = "Breathe is a Sphinx plugin providing beautifully integrated Doxygen output in your user-facing documentation."
HOMEPAGE = "https://www.breathe-doc.org/"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9e49eecf36fc015de7c61a0247df75d6"

SRC_URI[sha256sum] = "ff56af8bfd0b50ebf10c81a07b8a5739787eb015d26b4ba162b6983d6e17c7c3"

SRC_URI += "file://0001-tests-accept-docutils-0.23-boolean-attribute-seriali.patch \
            file://run-ptest \
           "

inherit pypi python_flit_core ptest ptest-python-pytest

RDEPENDS:${PN} = "python3-sphinx"

RDEPENDS:${PN}-ptest += "doxygen"

BBCLASSEXTEND =+ "native nativesdk"
