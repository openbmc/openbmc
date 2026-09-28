SUMMARY = "Convert SHACL model to code bindings"
HOMEPAGE = "https://pypi.org/project/shacl2code/"
SECTION = "devel/python"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=0582f358628f299f29c23bf5fb2f73c9"

PYPI_PACKAGE = "shacl2code"
SRC_URI[sha256sum] = "0f3a243c6482a0f95c5a793288d304908506b51b82dc6133de22be477cd75c24"

inherit pypi python_hatchling

RDEPENDS:${PN} += " \
    python3-jinja2 \
    python3-rdflib \
"

BBCLASSEXTEND = "native nativesdk"
