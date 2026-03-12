SUMMARY = "Convert SHACL model to code bindings"
HOMEPAGE = "https://pypi.org/project/shacl2code/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0582f358628f299f29c23bf5fb2f73c9"

PYPI_PACKAGE = "shacl2code"
SRC_URI[sha256sum] = "d8b511054ca564b4514b9186ece7f5eb8048cfc5daa6625def1a3adba13c4f66"

inherit pypi python_hatchling

RDEPENDS:${PN} += " \
    python3-jinja2 \
    python3-rdflib \
"

BBCLASSEXTEND = "native nativesdk"
