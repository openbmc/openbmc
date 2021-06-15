SUMMARY = "RDFLib is a pure Python package for working with RDF"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=68c1a3bb687bd63b8e5552f3ea249840"

SRC_URI[md5sum] = "80d7c6adc2e4040cdd8dade2e0e61403"
SRC_URI[sha256sum] = "78149dd49d385efec3b3adfbd61c87afaf1281c30d3fcaf1b323b34f603fb155"

inherit pypi setuptools3

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-isodate \
    ${PYTHON_PN}-pyparsing \
"

BBCLASSEXTEND = "native nativesdk"
